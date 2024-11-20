package com.example.pos.repository.pos;

import com.example.pos.model.pos.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
