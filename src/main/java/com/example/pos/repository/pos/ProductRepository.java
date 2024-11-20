package com.example.pos.repository.pos;

import com.example.pos.model.pos.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
