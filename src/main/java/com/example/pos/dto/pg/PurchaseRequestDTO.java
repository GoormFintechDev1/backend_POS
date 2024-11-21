package com.example.pos.dto.pg;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestDTO {
    private String itenName;
    private int price;
    private String orderId;
    private String paymentKey;
}
