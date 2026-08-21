package com.lastmile.delivery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private OrderType orderType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal intraZoneRatePerKg;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal interZoneRatePerKg;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal codSurcharge;

    public enum OrderType {
        B2B,
        B2C
    }
}