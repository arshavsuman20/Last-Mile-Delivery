package com.lastmile.delivery.dto;

import com.lastmile.delivery.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

    private Long id;
    private BigDecimal totalCharge;
    private Order.Status status;
    private LocalDateTime createdAt;

    public OrderResponse(
            Long id,
            BigDecimal totalCharge,
            Order.Status status,
            LocalDateTime createdAt) {

        this.id = id;
        this.totalCharge = totalCharge;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }

    public Order.Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}