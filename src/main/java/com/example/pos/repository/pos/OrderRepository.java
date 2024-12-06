package com.example.pos.repository.pos;

import com.example.pos.model.pos.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
