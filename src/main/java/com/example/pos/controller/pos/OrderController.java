package com.example.pos.controller.pos;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pos.dto.pg.DateRangeRequestDTO;
import com.example.pos.dto.pos.OrderRequestDTO;
import com.example.pos.dto.pos.OrderResponseDTO;
import com.example.pos.service.pos.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public OrderResponseDTO createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("Creating order: {}", orderRequestDTO);
        OrderResponseDTO response = orderService.createOrder(orderRequestDTO);
        log.info("Order created successfully: {}", response);
        return response;
    }

    @GetMapping("/all")
    public List<OrderResponseDTO> getAllOrders() {
        log.info("Fetching all orders");
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        log.info("Fetched {} orders", orders.size());
        return orders;
    }

    @PostMapping("/send")
    public List<OrderResponseDTO> getOrdersByDateRange(@RequestBody DateRangeRequestDTO dateRange) {
        log.info("Fetching orders with date range: {}", dateRange);
        if (dateRange.getStartDate() == null || dateRange.getEndDate() == null
                || dateRange.getStartDate().isAfter(dateRange.getEndDate())) {
            log.warn("Invalid date range provided: {}", dateRange);
            throw new IllegalArgumentException("Invalid date range. Ensure startDate is before endDate.");
        }

        List<OrderResponseDTO> orders = orderService.getOrdersByDateRange(dateRange.getStartDate(), dateRange.getEndDate());
        log.info("Fetched {} orders for date range: {}", orders.size(), dateRange);
        return orders;
    }


    // @PostMapping("/send-to-backend")
    // public ResponseEntity<?> sendOrdersToBackend(@RequestBody DateRangeRequestDTO dateRange) {
    //     log.info("Request received to send orders to backend for date range: {}", dateRange);

    //     if (dateRange.getStartDate() == null || dateRange.getEndDate() == null ||
    //             dateRange.getStartDate().isAfter(dateRange.getEndDate())) {
    //         log.warn("Invalid date range provided: {}", dateRange);
    //         return ResponseEntity.badRequest().body("Invalid date range.");
    //     }

    //     List<OrderResponseDTO> orders = orderService.getOrdersByDateRange(dateRange.getStartDate(), dateRange.getEndDate());
    //     log.info("Fetched {} orders for backend transfer", orders.size());

    //     if (orders.isEmpty()) {
    //         log.warn("No orders found for the specified date range: {}", dateRange);
    //         return ResponseEntity.ok("No orders to send for the specified date range.");
    //     }

    //     salesTransferService.sendOrdersToBackend(orders);

    //     log.info("Orders successfully sent to backend.");
    //     return ResponseEntity.ok("Orders successfully sent to backend.");
    // }
}
