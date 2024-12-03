package com.example.pos.dto.pos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private Long orderId;
    private List<OrderItemDTO> orderItems;
    private Long posId;

}
