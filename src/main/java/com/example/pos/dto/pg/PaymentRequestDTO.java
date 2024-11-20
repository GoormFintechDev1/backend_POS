package com.example.pos.dto.pg;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    public Long orderId;
    private int amount;
    private String orderName;
    private String customerName;
    private String successUrl;
    private String failUrl;
}
