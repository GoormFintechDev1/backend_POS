package com.example.pos.controller.pg;

import com.example.pos.dto.pg.PaymentRequestDTO;
import com.example.pos.dto.pg.PaymentResponseDTO;
import com.example.pos.service.pg.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

//    // 결제 승인 요청
//    @PostMapping("/confirm")
//    public ResponseEntity<PaymentResponseDTO> confirmPayment(@RequestBody PaymentRequestDTO requestDTO) {
//        PaymentResponseDTO responseDTO = paymentService.confirmPayment(requestDTO);
//        return ResponseEntity.ok(responseDTO);
//    }

    @PostMapping("/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody PaymentRequestDTO requestDTO) {
        try {
            return paymentService.confirmPayment(requestDTO);
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("message", "결제 확인 중 오류가 발생했습니다.");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}
