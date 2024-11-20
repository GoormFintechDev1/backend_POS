package com.example.pos.dto.pos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long productId;
    private String productName;
    private int productPrice;
    private int stockQuantity;
}
