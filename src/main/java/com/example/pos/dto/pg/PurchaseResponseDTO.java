package com.example.pos.dto.pg;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponseDTO {
    private String message;
    private String orderId;
    private int amount;
    private String status;
}
