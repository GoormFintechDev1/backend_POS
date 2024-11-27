package com.example.pos.dto.pg;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PosSalesRequestDTO {
    private String productName; // 상품명
    private int quantity; // 수량
    private BigDecimal totalAmount; // 총 금액
    private LocalDateTime saleTime; // 판매 시간
}
