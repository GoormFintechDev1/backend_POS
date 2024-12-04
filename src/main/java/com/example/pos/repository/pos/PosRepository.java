package com.example.pos.repository.pos;

import com.example.pos.model.pos.Pos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PosRepository extends JpaRepository<Pos, Long> {
    Optional<Pos> findByBrNum(String brNum);

}
