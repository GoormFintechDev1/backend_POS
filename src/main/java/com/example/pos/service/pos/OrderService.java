package com.example.pos.service.pos;

import com.example.pos.dto.pos.OrderItemDTO;
import com.example.pos.dto.pos.OrderRequestDTO;
import com.example.pos.dto.pos.OrderResponseDTO;
import com.example.pos.model.enumset.OrderStatus;
import com.example.pos.model.enumset.PaymentStatus;
import com.example.pos.model.pos.Order;
import com.example.pos.model.pos.OrderItem;
import com.example.pos.model.pos.Product;
import com.example.pos.repository.pos.OrderRepository;
import com.example.pos.repository.pos.ProductRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.pos.model.pos.QProduct.product;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final JPAQueryFactory queryFactory;


    // 주문 생성 
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        List<OrderItem> orderItems = orderRequestDTO.getOrderItems().stream()
                .map(item -> {
                    Product productEntity = queryFactory.selectFrom(product)
                            .where(product.productId.eq(item.getProductId()))
                            .fetchOne();

                    if (productEntity == null) {
                        throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
                    }

                    if (productEntity.getStockQuantity() < item.getQuantity()) {
                        throw new IllegalArgumentException("재고가 부족합니다.");
                    }

                    productEntity.setStockQuantity(productEntity.getStockQuantity() - item.getQuantity());

                    return OrderItem.builder()
                            .product(productEntity)
                            .quantity(item.getQuantity())
                            .price(productEntity.getProductPrice() * item.getQuantity())
                            .build();

                }).collect(Collectors.toList());

        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getPrice)
                .sum();

        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .orderItems(orderItems)
                .totalPrice(totalPrice)
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        return OrderResponseDTO.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .orderItems(order.getOrderItems().stream()
                        .map(item -> OrderItemDTO.builder()
                                .productId(item.getProduct().getProductId())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
