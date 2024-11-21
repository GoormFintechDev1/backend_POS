package com.example.pos.service.pg;


import com.example.pos.dto.pg.PaymentRequestDTO;
import com.example.pos.dto.pg.PaymentResponseDTO;
import com.example.pos.model.enumset.PaymentStatus;
import com.example.pos.model.pg.Payment;
import com.example.pos.repository.pg.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
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

    public PaymentResponseDTO confirmPayment(PaymentRequestDTO requestDTO) {
        try {
            // 토스페이먼츠 결제 승인 요청
            URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", createAuthorizationHeader());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("paymentKey", requestDTO.getPaymentKey());
            requestBody.put("orderId", requestDTO.getOrderId());
            requestBody.put("amount", requestDTO.getAmount());

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.toJSONString().getBytes(StandardCharsets.UTF_8));

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // 결제 승인 성공
                savePayment(requestDTO, PaymentStatus.APPROVED);
                return new PaymentResponseDTO(requestDTO.getOrderId(), requestDTO.getAmount(), "APPROVED", "결제가 성공적으로 완료되었습니다.");
            } else {
                // 실패 처리
                savePayment(requestDTO, PaymentStatus.FAILED);
                throw new RuntimeException("결제 승인 실패");
            }
        } catch (Exception e) {
            throw new RuntimeException("결제 실패: " + e.getMessage());
        }
    }

    private void savePayment(PaymentRequestDTO requestDTO, PaymentStatus status) {
        Payment payment = Payment.builder()
                .orderId(requestDTO.getOrderId())
                .amount(requestDTO.getAmount())
                .paymentKey(requestDTO.getPaymentKey())
                .status(status)
                .createdAt(LocalDateTime.now())
                .approvedAt(status == PaymentStatus.APPROVED ? LocalDateTime.now() : null)
                .build();

        paymentRepository.save(payment);
    }

    private String createAuthorizationHeader() {
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedAuth = encoder.encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedAuth;
    }

}
