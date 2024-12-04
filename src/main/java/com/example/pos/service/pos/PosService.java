package com.example.pos.service.pos;

import com.example.pos.model.pos.Pos;
import com.example.pos.repository.pos.PosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PosService {

    private final PosRepository posRepository;

    public Long getPosIdByBrNum(String brNum) {
        return posRepository.findByBrNum(brNum)
                .map(Pos::getPosId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid business registration number"));
    }
}
