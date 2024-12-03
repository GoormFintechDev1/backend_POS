package com.example.pos.service.pos;

import com.example.pos.dto.pos.OrderItemDTO;
import com.example.pos.dto.pos.OrderRequestDTO;
import com.example.pos.dto.pos.OrderResponseDTO;
import com.example.pos.model.enumset.OrderStatus;
import com.example.pos.model.enumset.PaymentStatus;
import com.example.pos.model.pos.Order;
import com.example.pos.model.pos.OrderItem;
import com.example.pos.model.pos.Pos;
import com.example.pos.model.pos.Product;
import com.example.pos.repository.pos.OrderRepository;
import com.example.pos.repository.pos.PosRepository;
import com.example.pos.repository.pos.ProductRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.pos.model.pos.QOrder.order;
import static com.example.pos.model.pos.QOrderItem.orderItem;
import static com.example.pos.model.pos.QProduct.product;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final JPAQueryFactory queryFactory;
    private final PosRepository posRepository;


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


                    return OrderItem.builder()
                            .product(productEntity)
                            .quantity(item.getQuantity())
                            .price(productEntity.getProductPrice() * item.getQuantity())
                            .build();

                }).collect(Collectors.toList());

        // 총 가격 계산
        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getPrice)
                .sum();

        // 총 수량 계산

        int totalQuantity = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        Pos pos = posRepository.findById(orderRequestDTO.getPosId())
                .orElseThrow(() -> new IllegalArgumentException("POS를 찾을 수 없음"));

        String productName = orderItems.stream()
                .map(item -> item.getProduct().getProductName())
                .collect(Collectors.joining(", "));


        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .orderItems(orderItems)
                .totalPrice(totalPrice)
                .productName(productName)
                .quantity(totalQuantity)
                .orderStatus(OrderStatus.COMPLETED)
                .paymentStatus(PaymentStatus.APPROVED)
                .build();

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
        }


        order = orderRepository.save(order);

        return OrderResponseDTO.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .orderStatus(order.getOrderStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .orderDate(order.getOrderDate())
                .orderItems(order.getOrderItems().stream()
                        .map(item -> OrderItemDTO.builder()
                                .productId(item.getProduct().getProductId())
                                .quantity(item.getQuantity())
                                .productName(item.getProduct().getProductName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // 모든 주문 조회
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = queryFactory
                .selectFrom(order)
                .leftJoin(order.orderItems, orderItem).fetchJoin()
                .leftJoin(orderItem.product, product).fetchJoin()
                .fetch();

        return orders.stream()
                .map(o -> OrderResponseDTO.builder()
                        .orderId(o.getOrderId())
                        .totalPrice(o.getTotalPrice())
                        .productName(o.getProductName()) // 상품 이름 포함
                        .quantity(o.getQuantity()) // 총 수량 포함
                        .orderStatus(o.getOrderStatus().name())
                        .paymentStatus(o.getPaymentStatus().name())
                        .orderDate(o.getOrderDate())
                        .orderItems(o.getOrderItems().stream()
                                .map(item -> OrderItemDTO.builder()
                                        .productId(item.getProduct().getProductId())
                                        .quantity(item.getQuantity())
                                        .productName(item.getProduct().getProductName()) // 상품 이름 추가
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findAllByOrderDateBetween(startDate, endDate);

        return orders.stream()
                .map(o -> OrderResponseDTO.builder()
                        .orderId(o.getOrderId())
                        .totalPrice(o.getTotalPrice())
                        .productName(o.getProductName())
                        .quantity(o.getQuantity())
                        .orderStatus(o.getOrderStatus().name())
                        .paymentStatus(o.getPaymentStatus().name())
                        .orderDate(o.getOrderDate())
                        .orderItems(o.getOrderItems().stream()
                                .map(item -> OrderItemDTO.builder()
                                        .productId(item.getProduct().getProductId())
                                        .quantity(item.getQuantity())
                                        .productName(item.getProduct().getProductName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

}





