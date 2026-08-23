package com.lastmile.delivery.controller;

import com.lastmile.delivery.dto.CreateOrderRequest;
import com.lastmile.delivery.dto.OrderResponse;
import com.lastmile.delivery.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(
            orderService.getAll().stream()
                .map(order -> new OrderResponse(
                    order.getId(),
                    order.getTotalCharge(),
                    order.getStatus(),
                    order.getCreatedAt()
                ))
                .toList()
        );
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request) {

        return ResponseEntity.ok(
                orderService.createOrder(request)
        );
    }
}