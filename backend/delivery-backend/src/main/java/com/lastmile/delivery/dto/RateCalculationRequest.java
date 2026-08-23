package com.lastmile.delivery.dto;

import com.lastmile.delivery.entity.Order;

public class RateCalculationRequest {

    private Long pickupAreaId;
    private Long dropAreaId;

    private Double length;
    private Double breadth;
    private Double height;
    private Double actualWeight;

    private Order.OrderType orderType;
    private Order.PaymentType paymentType;

    public Long getPickupAreaId() {
        return pickupAreaId;
    }

    public void setPickupAreaId(Long pickupAreaId) {
        this.pickupAreaId = pickupAreaId;
    }

    public Long getDropAreaId() {
        return dropAreaId;
    }

    public void setDropAreaId(Long dropAreaId) {
        this.dropAreaId = dropAreaId;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getBreadth() {
        return breadth;
    }

    public void setBreadth(Double breadth) {
        this.breadth = breadth;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(Double actualWeight) {
        this.actualWeight = actualWeight;
    }

    public Order.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(Order.OrderType orderType) {
        this.orderType = orderType;
    }

    public Order.PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Order.PaymentType paymentType) {
        this.paymentType = paymentType;
    }
}