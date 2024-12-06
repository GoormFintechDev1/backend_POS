package com.example.pos.dto.pos;

import com.example.pos.model.enumset.OrderStatus;
import com.example.pos.model.enumset.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long orderId;
    private int totalPrice;
    private String productName;
    private int quantity;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime orderDate;
    private String orderStatus;
    private String paymentStatus;
    private Long posId;
}
