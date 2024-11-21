package com.example.pos.controller.shop;

import com.example.pos.dto.shop.ItemDTO;
import com.example.pos.service.shop.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    // 품목 등록
    @PostMapping("/create")
    public ItemDTO createItem(@RequestBody ItemDTO itemDTO) {
        return itemService.createItem(itemDTO);
    }

    // 품목 조회 (전체)
    @GetMapping("/all")
    public List<ItemDTO> getAllItems() {
        return itemService.getAllItems();
    }

    // 품목 삭제
    @DeleteMapping("/delete/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }

    // 품목 수정
    @PutMapping("/edit/{id}")
    public ItemDTO updateItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        return itemService.updateItem(id, itemDTO);
    }
}
