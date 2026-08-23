package com.lastmile.delivery.service;

import com.lastmile.delivery.dto.RateCalculationRequest;
import com.lastmile.delivery.dto.RateCalculationResponse;
import com.lastmile.delivery.entity.Area;
import com.lastmile.delivery.entity.RateCard;
import com.lastmile.delivery.repository.AreaRepository;
import com.lastmile.delivery.repository.RateCardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RateCalculationService {

    private static final double VOLUMETRIC_DIVISOR = 5000.0;

    private final AreaRepository areaRepository;
    private final RateCardRepository rateCardRepository;

    public RateCalculationService(
            AreaRepository areaRepository,
            RateCardRepository rateCardRepository) {

        this.areaRepository = areaRepository;
        this.rateCardRepository = rateCardRepository;
    }

    public RateCalculationResponse calculate(RateCalculationRequest request) {

        Area pickupArea = areaRepository.findById(request.getPickupAreaId())
                .orElseThrow(() -> new IllegalArgumentException("Pickup area not found"));

        Area dropArea = areaRepository.findById(request.getDropAreaId())
                .orElseThrow(() -> new IllegalArgumentException("Drop area not found"));

        double volumetricWeight =
                (request.getLength()
                        * request.getBreadth()
                        * request.getHeight()) / VOLUMETRIC_DIVISOR;

        double billableWeight =
                Math.max(request.getActualWeight(), volumetricWeight);

        RateCard.OrderType rateCardType =
                request.getOrderType() == com.lastmile.delivery.entity.Order.OrderType.B2B
                        ? RateCard.OrderType.B2B
                        : RateCard.OrderType.B2C;

        RateCard rateCard = rateCardRepository
                .findAll()
                .stream()
                .filter(r -> r.getOrderType() == rateCardType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rate card not found"));

        boolean sameZone =
                pickupArea.getZone().getId().equals(dropArea.getZone().getId());

        BigDecimal ratePerKg = sameZone
                ? rateCard.getIntraZoneRatePerKg()
                : rateCard.getInterZoneRatePerKg();

        BigDecimal baseCharge =
                ratePerKg.multiply(BigDecimal.valueOf(billableWeight));

        BigDecimal codSurcharge =
                request.getPaymentType() == com.lastmile.delivery.entity.Order.PaymentType.COD
                        ? rateCard.getCodSurcharge()
                        : BigDecimal.ZERO;

        BigDecimal totalCharge =
                baseCharge.add(codSurcharge);

        return new RateCalculationResponse(
                volumetricWeight,
                billableWeight,
                baseCharge,
                codSurcharge,
                totalCharge
        );
    }
}