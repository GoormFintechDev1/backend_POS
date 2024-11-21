package com.example.pos.dto.pos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long orderId;
    private int totalPrice;
    private List<OrderItemDTO> orderItems;
    private String orderStatus;
    private String paymentStatus;
}
