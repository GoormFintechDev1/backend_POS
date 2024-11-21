package com.example.pos.service.pg;

import com.example.pos.dto.pg.PurchaseRequestDTO;
import com.example.pos.dto.pg.PurchaseResponseDTO;
import com.example.pos.model.enumset.PaymentStatus;
import com.example.pos.model.pg.Purchase;
import com.example.pos.repository.pg.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    // 물건 구매 (결제 처리 및 저장)
    public PurchaseResponseDTO purchaseItem(PurchaseRequestDTO requestDTO) {
        // 토스 페이먼츠 결제 승인 요청
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://api.tosspayments.com/v1/payments/confirm";

        // API 요청 데이터
        var body = new org.springframework.util.LinkedMultiValueMap<String, String>();
        body.add("paymentKey", requestDTO.getPaymentKey());
        body.add("orderId", requestDTO.getOrderId());
        body.add("amount", String.valueOf(requestDTO.getPrice()));

        var headers = new org.springframework.http.HttpHeaders();
        headers.add("Authorization", "Basic YOUR_SECRET_KEY"); // 토스페이먼츠 시크릿 키
        headers.add("Content-Type", "application/json");

        var entity = new org.springframework.http.HttpEntity<>(body, headers);

        try {
            var response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // 결제 성공
                Purchase purchase = savePurchase(requestDTO, PaymentStatus.COMPLETED);
                return PurchaseResponseDTO.builder()
                        .message("결제가 성공적으로 처리되었습니다.")
                        .orderId(purchase.getOrderId())
                        .amount(purchase.getPrice())
                        .status(PaymentStatus.COMPLETED.name())
                        .build();
            }
        } catch (Exception e) {
            // 결제 실패
            savePurchase(requestDTO, PaymentStatus.FAILED);
            throw new RuntimeException("결제 실패: " + e.getMessage());
        }

        return null;
    }

    // 구매 내역 저장
    private Purchase savePurchase(PurchaseRequestDTO requestDTO, PaymentStatus status) {
        Purchase purchase = Purchase.builder()
                .itemName(requestDTO.getItenName())
                .price(requestDTO.getPrice())
                .orderId(requestDTO.getOrderId())
                .paymentKey(requestDTO.getPaymentKey())
                .paymentStatus(status)
                .purchasedAt(LocalDateTime.now())
                .build();

        return purchaseRepository.save(purchase);
    }
}
