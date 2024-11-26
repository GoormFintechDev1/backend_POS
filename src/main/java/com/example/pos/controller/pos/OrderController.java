package com.example.pos.controller.pos;

import com.example.pos.dto.pg.DateRangeRequestDTO;
import com.example.pos.dto.pos.OrderRequestDTO;
import com.example.pos.dto.pos.OrderResponseDTO;
import com.example.pos.service.pos.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public OrderResponseDTO createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        return orderService.createOrder(orderRequestDTO);
    }

    @GetMapping("/all")
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping("/send")
    public List<OrderResponseDTO> getOrdersByDateRange(
            @RequestBody DateRangeRequestDTO dateRange) {
        if (dateRange.getStartDate() == null || dateRange.getEndDate() == null
                || dateRange.getStartDate().isAfter(dateRange.getEndDate())) {
            throw new IllegalArgumentException("Invalid date range. Ensure startDate is before endDate.");
        }

        return orderService.getOrdersByDateRange(dateRange.getStartDate(), dateRange.getEndDate());
    }
}
