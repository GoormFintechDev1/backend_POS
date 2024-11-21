package com.example.pos.repository.pg;

import com.example.pos.model.pg.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
