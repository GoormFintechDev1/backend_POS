package com.example.pos.service.pg;

import com.example.pos.dto.pg.PaymentRequestDTO;
import com.example.pos.dto.pg.PaymentResponseDTO;
import com.example.pos.model.enumset.PayType;
import com.example.pos.model.pg.Payment;
import com.example.pos.repository.pg.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final RedisTemplate<String, PaymentRequestDTO> redisTemplate;
    @Qualifier("customStringRedisTemplate")
    private final RedisTemplate<String, String> StringRedisTemplate;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;




    // 1. 결제 요청 데이터 Redis 저장
    public void savePaymentRequest(PaymentRequestDTO requestDTO) {
        String redisKey = "payment:" + requestDTO.getOrderId();
        redisTemplate.opsForValue().set(redisKey, requestDTO, 10, TimeUnit.MINUTES); // 10분 간 저장

    }

    // 2. Redis와 요청 데이터 검증
    public void verifyPaymentRequestwithLock(String orderId, Long amount) {
        String redisKey = "payment:" + orderId;
        String lockKey = "lock:payment:" + orderId;

        // Redis 락 설정 (setNX) 이게 바로 분산락..!!
        Boolean lockAcquired = StringRedisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 10, TimeUnit.MINUTES);
        if (!lockAcquired) {
            throw new RuntimeException("결제 요청이 이미 처리 중입니다.");
        }
        try {
            PaymentRequestDTO savedRequest = redisTemplate.opsForValue().get(redisKey);
            if (savedRequest == null) {
                throw new RuntimeException("결제 요청 데이터가 존재하지 않습니다");
            }
            if (!savedRequest.getAmount().equals(amount)) {
                throw new RuntimeException("결제 요청 금액이 일치하지 않습니다.");
            }
        } finally {
            // Redis 락 해제
            StringRedisTemplate.opsForValue().set(redisKey + ":completed", "true", 10, TimeUnit.MINUTES);
            redisTemplate.delete(lockKey);
        }
    }

    // 3. 토스 페이먼츠 승인 요청

    public PaymentResponseDTO confirmToss(PaymentRequestDTO requestDTO) throws Exception {
        log.info("결제 승인 요청 시작: {}", requestDTO);

        // API 호출 데이터 준비
        JSONObject requestJson = new JSONObject();
        requestJson.put("orderId", requestDTO.getOrderId());
        requestJson.put("amount", requestDTO.getAmount());
        requestJson.put("paymentKey", requestDTO.getPaymentKey());

        // 인증 헤더 생성
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        String authorizations = "Basic " + Base64.getEncoder()
                .encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        log.info("API 호출 준비 완료: {}", requestJson);

        // API 호출 설정
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestJson.toString().getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        InputStream responseStream = responseCode == 200
                ? connection.getInputStream()
                : connection.getErrorStream();

        // 응답 JSON을 문자열로 읽기
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStream is = responseStream) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                responseBuilder.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
        }
        String responseJson = responseBuilder.toString();
        log.info("결제 승인 API 응답: {}", responseJson);

        // 응답을 DTO로 매핑
        PaymentResponseDTO responseDTO = objectMapper.readValue(responseJson, PaymentResponseDTO.class);

        // 추가 데이터 처리
        Map<String, Object> responseMap = objectMapper.readValue(responseJson, Map.class);
        if (responseMap.containsKey("easyPay")) {
            Map<String, Object> easyPay = (Map<String, Object>) responseMap.get("easyPay");
            responseDTO.setProvider((String) easyPay.get("provider"));
            responseDTO.setEasyPayAmount(Long.valueOf(String.valueOf(easyPay.get("amount"))));
            responseDTO.setEasyPayDiscountAmount(Long.valueOf(String.valueOf(easyPay.get("discountAmount"))));
        }

        // 응답 상태 확인
        if (responseCode != 200) {
            log.error("결제 승인 실패. 응답 코드: {}, 응답 내용: {}", responseCode, responseJson);
            throw new RuntimeException("결제 승인 실패: " + responseJson);
        }

        log.info("결제 승인 처리 완료: {}", responseDTO);
        return responseDTO;
    }



    // 4. 결제 성공 시 데이터 DB에 저장
    public void savePayment(PaymentRequestDTO requestDTO, PaymentResponseDTO responseDTO) {
        Payment payment = Payment.builder()
                .paymentKey(responseDTO.getPaymentKey())
                .orderId(responseDTO.getOrderId())
                .orderName(responseDTO.getOrderName())
                .requestedAt(responseDTO.getRequestedAt())
                .approvedAt(responseDTO.getApprovedAt())
                .provider(responseDTO.getProvider())
                .easyPayAmount(responseDTO.getEasyPayAmount())
                .easyPayDiscountAmount(responseDTO.getEasyPayDiscountAmount())
                .currency(responseDTO.getCurrency())
                .totalAmount(responseDTO.getTotalAmount())
                .vat(responseDTO.getVat())
                .method(responseDTO.getMethod())
                .paySuccessYN(true)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
    }


    // 5. Redis에서 데이터 삭제
    public void deleteRedisData(String orderId) {
        String redisKey = "payment:" + orderId;
        redisTemplate.delete(redisKey);
    }


//    public ResponseEntity<JSONObject> confirmPayment(@RequestBody PaymentRequestDTO requestDTO) throws Exception {
//
//        // 결제 승인 요청 데이터 준비
//        JSONObject obj = new JSONObject();
//        obj.put("orderId", requestDTO.getOrderId());
//        obj.put("amount", requestDTO.getAmount());
////        obj.put("paymentKey", requestDTO.getPaymentKey());
//
//        // 토스페이먼츠 API 인증 헤더 생성
//        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
//        Base64.Encoder encoder = Base64.getEncoder();
//        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
//        String authorizations = "Basic " + new String(encodedBytes);
//
//        // HTTP 연결 설정
//        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestProperty("Authorization", authorizations);
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setRequestMethod("POST");
//        connection.setDoOutput(true);
//
//        // 요청 데이터 전송
//        try (OutputStream outputStream = connection.getOutputStream()) {
//            outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));
//        }
//
//        // 응답 코드 확인
//        int code = connection.getResponseCode();
//        InputStream responseStream = code == 200 ? connection.getInputStream() : connection.getErrorStream();
//
//        // JSON 응답 파싱
//        JSONObject responseJson = objectMapper.readValue(responseStream, JSONObject.class);
//
//        // 응답 반환
//        return ResponseEntity.status(code).body(responseJson);
//    }


}

