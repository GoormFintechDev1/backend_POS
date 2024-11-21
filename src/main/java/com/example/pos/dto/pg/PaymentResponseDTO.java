package com.example.pos.dto.pg;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentResponseDTO {
  private String orderId;
  private int amount;
  private String status; // APPROVED / FAILED
  private String message;
}
