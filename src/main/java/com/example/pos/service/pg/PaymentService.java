package com.example.pos.service.pg;


import com.example.pos.dto.pg.PaymentRequestDTO;
import com.example.pos.dto.pg.PaymentResponseDTO;
import com.example.pos.model.enumset.PaymentStatus;
import com.example.pos.model.pg.Payment;
import com.example.pos.repository.pg.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${TOSS_SECRET_KEY}")
    private String tossSecretKey;

    private final ObjectMapper objectMapper;


        @RequestMapping(value = "/confirm")
        public ResponseEntity<JSONObject> confirmPayment(@RequestBody PaymentRequestDTO requestDTO) throws Exception {

            // 결제 승인 요청 데이터 준비
            JSONObject obj = new JSONObject();
            obj.put("orderId", requestDTO.getOrderId());
            obj.put("amount", requestDTO.getAmount());
            obj.put("paymentKey", requestDTO.getPaymentKey());

            // 토스페이먼츠 API 인증 헤더 생성
            String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
            String authorizations = "Basic " + new String(encodedBytes);

            // HTTP 연결 설정
            URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", authorizations);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 요청 데이터 전송
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));
            }

            // 응답 코드 확인
            int code = connection.getResponseCode();
            InputStream responseStream = code == 200 ? connection.getInputStream() : connection.getErrorStream();

            // JSON 응답 파싱
            JSONObject responseJson = objectMapper.readValue(responseStream, JSONObject.class);

            // 응답 반환
            return ResponseEntity.status(code).body(responseJson);
        }
//    public PaymentResponseDTO confirmPayment(PaymentRequestDTO requestDTO) {
//        try {
//            // 토스페이먼츠 결제 승인 요청
//            URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Authorization", createAuthorizationHeader());
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setDoOutput(true);
//
//            JSONObject requestBody = new JSONObject();
//            requestBody.put("paymentKey", requestDTO.getPaymentKey());
//            requestBody.put("orderId", requestDTO.getOrderId());
//            requestBody.put("amount", requestDTO.getAmount());
//
//            OutputStream outputStream = connection.getOutputStream();
//            outputStream.write(requestBody.toJSONString().getBytes(StandardCharsets.UTF_8));

//            int responseCode = connection.getResponseCode();
//            if (responseCode == 200) {
//                // 결제 승인 성공
//                savePayment(requestDTO, PaymentStatus.APPROVED);
//                return new PaymentResponseDTO(requestDTO.getOrderId(), requestDTO.getAmount(), "APPROVED", "결제가 성공적으로 완료되었습니다.");
//            } else {
//                // 실패 처리
//                savePayment(requestDTO, PaymentStatus.FAILED);
//                throw new RuntimeException("결제 승인 실패");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("결제 실패: " + e.getMessage());
//        }
    }

//    private void savePayment(PaymentRequestDTO requestDTO, PaymentStatus status) {
//        Payment payment = Payment.builder()
//                .orderId(requestDTO.getOrderId())
//                .amount(requestDTO.getAmount())
//                .paymentKey(requestDTO.getPaymentKey())
//                .status(status)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(status == PaymentStatus.APPROVED ? LocalDateTime.now() : null)
//                .build();
//
//        paymentRepository.save(payment);
//    }

//    private String createAuthorizationHeader() {
//        Base64.Encoder encoder = Base64.getEncoder();
//        String encodedAuth = encoder.encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
//        return "Basic " + encodedAuth;
//    }


