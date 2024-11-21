package com.example.pos.model.pg;

import com.example.pos.model.enumset.PaymentStatus;
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
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // APPROVED, FAILED

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime approvedAt; // 승인된 시간

}
