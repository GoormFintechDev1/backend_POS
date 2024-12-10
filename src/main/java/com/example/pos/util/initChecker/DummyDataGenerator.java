package com.example.pos.util.initChecker;

import com.example.pos.model.pos.Order;
import com.example.pos.model.pos.Pos;
import com.example.pos.model.enumset.OrderStatus;
import com.example.pos.model.enumset.PaymentStatus;
import com.example.pos.repository.pos.OrderRepository;
import com.example.pos.repository.pos.PosRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DummyDataGenerator {

    private final OrderRepository orderRepository;
    private final PosRepository posRepository;

    private final Random random = new Random();

    private final List<String> productNames = List.of(
            "아메리카노", "아이스 아메리카노", "라떼",
            "아이스 라떼", "바닐라 라떼", "아이스 바닐라 라떼"
    );

    private final List<Integer> productPrices = List.of(
            3000, 3500, 4000, 4500, 5000, 5500
    );

    @PostConstruct
    public void generateDummyData() {
        // 주문 데이터가 이미 존재하면 데이터 생성을 건너뛰기
        if (orderRepository.count() > 0) {
            System.out.println("주문 데이터가 이미 존재합니다. 더 이상 생성하지 않습니다.");
            return;
        }

        List<Pos> posList = posRepository.findAll(); // POS 데이터 로드

        for (Pos pos : posList) {
            generateOrdersForPos(pos);
        }
    }

    private void generateOrdersForPos(Pos pos) {
        for (int month = 10; month <= 12; month++) {
            generateMonthlyOrders(pos, month);
        }
    }

    private void generateMonthlyOrders(Pos pos, int month) {
        // 500만 원 ~ 800만 원 범위로 설정
        int totalSalesTarget = random.nextInt(3_000_001) + 5_000_000; // 500만 ~ 800만
        int totalSales = 0;

        while (totalSales < totalSalesTarget) {
            Order order = createRandomOrder(pos, month);
            totalSales += order.getTotalPrice();
            orderRepository.save(order);
        }
    }

    private Order createRandomOrder(Pos pos, int month) {
        int productIndex = random.nextInt(productNames.size());
        String productName = productNames.get(productIndex);
        int productPrice = productPrices.get(productIndex);
        int quantity = random.nextInt(5) + 1; // 1 ~ 5개 랜덤
        int totalPrice = productPrice * quantity;

        return Order.builder()
                .orderDate(generateRandomDateInMonth(month))
                .productName(productName)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .orderStatus(OrderStatus.COMPLETED)
                .paymentStatus(PaymentStatus.APPROVED)
                .pos(pos)
                .build();
    }

    private LocalDateTime generateRandomDateInMonth(int month) {
        int day = random.nextInt(28) + 1; // 1 ~ 28일 랜덤
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        return LocalDateTime.of(2024, month, day, hour, minute).truncatedTo(ChronoUnit.MINUTES);
    }
}
