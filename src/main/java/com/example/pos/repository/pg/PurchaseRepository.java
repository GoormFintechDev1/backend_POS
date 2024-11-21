package com.example.pos.repository.pg;

import com.example.pos.model.pg.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
