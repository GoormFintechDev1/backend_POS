package com.example.pos.controller.pos;

import com.example.pos.dto.pos.PosRequestDTO;
import com.example.pos.service.pos.PosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class PosController {

    private final PosService posService;

    @PostMapping("/get-pos-id")
    public ResponseEntity<Long> getPosId(@RequestBody PosRequestDTO request) {
        Long posId = posService.getPosIdByBrNum(request.getBrNum());
        return ResponseEntity.ok(posId);
    }
}
