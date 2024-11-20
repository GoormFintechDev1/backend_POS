package com.example.pos.dto.pos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private String customerName;
    private List<OrderItemDTO> orderItems;

}
