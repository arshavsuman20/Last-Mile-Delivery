package com.lastmile.delivery.dto;

import com.lastmile.delivery.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    
    private Long pickupAreaId;
    private String pickupAreaName;
    private String pickupAddress;
    
    private Long dropAreaId;
    private String dropAreaName;
    private String dropAddress;
    
    private Double length;
    private Double breadth;
    private Double height;
    private Double actualWeight;
    private Double volumetricWeight;
    private Double billableWeight;
    
    private Order.OrderType orderType;
    private Order.PaymentType paymentType;
    
    private BigDecimal baseCharge;
    private BigDecimal codSurcharge;
    private BigDecimal totalCharge;
    
    private Order.Status status;
    
    private Long assignedAgentId;
    private String assignedAgentName;
    
    private LocalDateTime createdAt;
}