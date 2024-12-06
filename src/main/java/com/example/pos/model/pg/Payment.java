package com.example.pos.model.pg;

import com.example.pos.model.enumset.PayType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, unique = true)
    private Long paymentId;

    private String paymentKey; // 결제 키
    private String orderId; // 주문 ID
    private String orderName; // 주문 이름
    private String requestedAt; // 결제 요청 시간
    private String approvedAt; // 결제 승인 시간

    private String provider; // 간편결제 제공자
    private Long easyPayAmount; // 간편결제 금액
    private Long easyPayDiscountAmount; // 간편결제 할인 금액

    private String currency; // 통화 정보
    private Long totalAmount; // 총 결제 금액
    private Long vat; // 부가세

    private String method; // 결제 방식 (ex. 카드, 간편결제 등)
    private boolean paySuccessYN; // 결제 성공 여부

    @Enumerated(EnumType.STRING)
    private PayType payType; // 결제 타입 (카드, 현금 등)

    private LocalDateTime createdAt; // 생성 시간
}
