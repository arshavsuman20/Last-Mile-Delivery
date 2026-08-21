package com.lastmile.delivery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "pickup_area_id", nullable = false)
    private Area pickupArea;

    @ManyToOne
    @JoinColumn(name = "drop_area_id", nullable = false)
    private Area dropArea;

    @Column(nullable = false)
    private String pickupAddress;

    @Column(nullable = false)
    private String dropAddress;

    @Column(nullable = false)
    private Double length;

    @Column(nullable = false)
    private Double breadth;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double actualWeight;

    @Column(nullable = false)
    private Double volumetricWeight;

    @Column(nullable = false)
    private Double billableWeight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal baseCharge;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal codSurcharge;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCharge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "assigned_agent_id")
    private DeliveryAgent assignedAgent;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum OrderType {
        B2B,
        B2C
    }

    public enum PaymentType {
        PREPAID,
        COD
    }

    public enum Status {
        CREATED,
        PICKED_UP,
        IN_TRANSIT,
        OUT_FOR_DELIVERY,
        DELIVERED,
        FAILED
    }
}