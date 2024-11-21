package com.example.pos.dto.shop;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {
    private Long itemId;
    private String itemName;
    private int itemPrice;
    private String description;
}
