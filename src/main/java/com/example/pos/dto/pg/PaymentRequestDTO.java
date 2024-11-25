package com.example.pos.dto.pg;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentRequestDTO {
    private String orderId;
    private String paymentKey;
    private Long amount;
}
