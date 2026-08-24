package com.lastmile.delivery.dto;

import com.lastmile.delivery.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCalculationRequest {

    private Long pickupAreaId;
    private Long dropAreaId;

    private Double length;
    private Double breadth;
    private Double height;
    private Double actualWeight;

    private Order.OrderType orderType;
    private Order.PaymentType paymentType;
}