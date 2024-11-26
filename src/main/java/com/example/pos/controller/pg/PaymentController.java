package com.example.pos.controller.pg;


import com.example.pos.dto.pg.DateRangeRequestDTO;
import com.example.pos.dto.pg.PaymentRequestDTO;
import com.example.pos.dto.pg.PaymentResponseDTO;
import com.example.pos.service.pg.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 요청 전 redis 저장
    @PostMapping("/save")
    public ResponseEntity<Void> savePaymentRequest(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        log.info("Received Payment Request: {}", paymentRequestDTO);
        System.out.println("Received Payment Request: " + paymentRequestDTO);
        paymentService.savePaymentRequest(paymentRequestDTO);
        return ResponseEntity.ok().build();
    }


    // 결제 성공 응답 처리
    @PostMapping("/confirm")
    public ResponseEntity<?> handlePaymentConfirmation(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            // 1. redis 검증
            paymentService.verifyPaymentRequestwithLock(paymentRequestDTO.getOrderId(), paymentRequestDTO.getAmount());

            // 2.Toss payments 승인 요청
            PaymentResponseDTO paymentResponseDTO = paymentService.confirmToss(paymentRequestDTO);

            // 3. DB 저장
            paymentService.savePayment(paymentRequestDTO, paymentResponseDTO);

            // 4. Redis 데이터 삭제
            paymentService.deleteRedisData(paymentRequestDTO.getOrderId());


            // 5. 성공 응답 반환
            log.info("결제 확인 완료: {}", paymentResponseDTO);
            return ResponseEntity.ok().body(paymentResponseDTO);


        } catch (Exception e) {
            log.error("결제 승인 중 오류: {}", e.getMessage(), e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "결제 승인 중 오류가 발생했습니다.");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/send")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByDateRange(@RequestBody DateRangeRequestDTO dateRange) {
        if (dateRange.getStartDate() == null || dateRange.getEndDate() == null || dateRange.getStartDate().isAfter(dateRange.getEndDate())) {
            return ResponseEntity.badRequest().body(null);
        }

        List<PaymentResponseDTO> payments = paymentService.getPaymentsByDateRange(dateRange.getStartDate(), dateRange.getEndDate());
        return ResponseEntity.ok(payments);
    }

}