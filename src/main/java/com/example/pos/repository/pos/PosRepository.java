package com.example.pos.repository.pos;

import com.example.pos.model.pos.Pos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PosRepository extends JpaRepository<Pos, Long> {
}
