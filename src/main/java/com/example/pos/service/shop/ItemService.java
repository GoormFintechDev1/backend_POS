package com.example.pos.service.shop;


import com.example.pos.dto.shop.ItemDTO;
import com.example.pos.model.shop.Item;
import com.example.pos.model.shop.QItem;
import com.example.pos.repository.shop.ItemRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final JPAQueryFactory queryFactory;
    private final ItemRepository itemRepository;

    // 품목 등록
    public ItemDTO createItem(ItemDTO itemDTO) {
        Item itemEntity = Item.builder()
                .itemName(itemDTO.getItemName())
                .itemPrice(itemDTO.getItemPrice())
                .description(itemDTO.getDescription())
                .build();

        Item savedItem = itemRepository.save(itemEntity);

        return ItemDTO.builder()
                .itemId(savedItem.getItemId())
                .itemName(savedItem.getItemName())
                .itemPrice(savedItem.getItemPrice())
                .description(savedItem.getDescription())
                .build();
    }

    // 품목 조회 (전체)
    @Transactional
    public List<ItemDTO> getAllItems() {
        QItem qItem = QItem.item;

        return queryFactory.selectFrom(qItem)
                .fetch()
                .stream()
                .map(i -> ItemDTO.builder()
                        .itemId(i.getItemId())
                        .itemName(i.getItemName())
                        .itemPrice(i.getItemPrice())
                        .description(i.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    // 품목 삭제
    public void deleteItem(Long itemId) {
        QItem qItem = QItem.item; // 로컬 변수로 QItem 정의

        Item existingItem = queryFactory.selectFrom(qItem)
                .where(qItem.itemId.eq(itemId))
                .fetchOne();

        if (existingItem == null) {
            throw new IllegalArgumentException("품목을 찾을 수 없습니다.");
        }

        itemRepository.delete(existingItem);
    }

    // 품목 수정
    public ItemDTO updateItem(Long itemId, ItemDTO itemDTO) {
        QItem qItem = QItem.item; // 로컬 변수로 QItem 정의

        Item existingItem = queryFactory.selectFrom(qItem)
                .where(qItem.itemId.eq(itemId))
                .fetchOne();

        if (existingItem == null) {
            throw new IllegalArgumentException("품목을 찾을 수 없습니다.");
        }

        existingItem.setItemName(itemDTO.getItemName());
        existingItem.setItemPrice(itemDTO.getItemPrice());
        existingItem.setDescription(itemDTO.getDescription());

        Item updatedItem = itemRepository.save(existingItem);

        return ItemDTO.builder()
                .itemId(updatedItem.getItemId())
                .itemName(updatedItem.getItemName())
                .itemPrice(updatedItem.getItemPrice())
                .description(updatedItem.getDescription())
                .build();
    }

}


