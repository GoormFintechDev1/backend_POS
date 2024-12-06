package com.example.pos.repository.shop;

import com.example.pos.model.shop.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
