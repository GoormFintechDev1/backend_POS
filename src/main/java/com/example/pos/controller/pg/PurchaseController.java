package com.example.pos.controller.pg;

import com.example.pos.dto.pg.PurchaseRequestDTO;
import com.example.pos.dto.pg.PurchaseResponseDTO;
import com.example.pos.service.pg.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    // 품목 구매 (결제 처리)
    @PostMapping
    public PurchaseResponseDTO puchaseItem(@RequestBody PurchaseRequestDTO requestDTO) {
        return purchaseService.purchaseItem(requestDTO);
    }
}
