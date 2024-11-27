package com.example.pos.service.pg;

import com.example.pos.dto.pg.PosSalesRequestDTO;
import com.example.pos.dto.pos.OrderResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesTransferService {
    private final WebClient webClient;

    @Value("${backend.api.url.pos-sales}")
    private String posSalesUrl;

    public void sendOrdersToBackend(List<OrderResponseDTO> orders) {
        log.info("Preparing to send {} orders to backend", orders.size());
        try {
            // 변환 로직 orderResponse->PosSales DTO
            List<PosSalesRequestDTO> salesRequests = orders.stream()
                    .map(this::convertToPosSalesRequestDTO)
                    .toList();
            log.info("Converted orders to PosSalesRequestDTO: {}", salesRequests);

            // WebClient로 POST 요청 전송
            String response = webClient.post()
                    .uri(posSalesUrl)
                    .bodyValue(salesRequests) // 요청 본문에 변환된 데이터를 포함
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(res -> log.info("Orders sent successfully. Response: {}", res))
                    .doOnError(err -> log.error("Failed to send orders: {}", err.getMessage()))
                    .block(); // 동기식 처리

            log.info("Backend response: {}", response);
        } catch (Exception e) {
            log.error("Unexpected error while sending orders to backend: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send orders to backend.", e);
        }
    }

    // OrderResponseDTO -> PosSalesRequestDTO 변환 메서드
    private PosSalesRequestDTO convertToPosSalesRequestDTO(OrderResponseDTO order) {
        log.info("Converting OrderResponseDTO to PosSalesRequestDTO: {}", order);
        PosSalesRequestDTO posSalesRequestDTO = PosSalesRequestDTO.builder()
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .totalAmount(BigDecimal.valueOf(order.getTotalPrice()))
                .saleTime(order.getOrderDate())
                .build();
        log.info("Converted DTO: {}", posSalesRequestDTO);
        return posSalesRequestDTO;
    }
}
