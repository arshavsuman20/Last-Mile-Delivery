package com.lastmile.delivery.controller;

import com.lastmile.delivery.dto.CreateOrderRequest;
import com.lastmile.delivery.dto.OrderResponse;
import com.lastmile.delivery.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lastmile.delivery.entity.Order;
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterOrders(
            @RequestParam(required = false) Order.Status status,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Long agentId) {

        return ResponseEntity.ok(
            orderService.filterOrders(status, zoneId, agentId)
                .stream()
                .map(order -> orderService.mapToResponse(order))
                .toList()
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerOrders(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
            orderService.getByCustomerId(customerId).stream()
                .map(order -> orderService.mapToResponse(order))
                .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderService.mapToResponse(order));
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(
            orderService.getAll().stream()
                .map(order -> orderService.mapToResponse(order))
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