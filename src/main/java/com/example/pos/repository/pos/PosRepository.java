package com.example.pos.repository.pos;

import com.example.pos.model.pos.Pos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosRepository extends JpaRepository<Pos, Long> {
}
