package com.example.pos.repository.pg;

import com.example.pos.model.pg.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
