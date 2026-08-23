package com.lastmile.delivery.dto;

import java.math.BigDecimal;

public class RateCalculationResponse {

    private Double volumetricWeight;
    private Double billableWeight;

    private BigDecimal baseCharge;
    private BigDecimal codSurcharge;
    private BigDecimal totalCharge;

    public RateCalculationResponse(
            Double volumetricWeight,
            Double billableWeight,
            BigDecimal baseCharge,
            BigDecimal codSurcharge,
            BigDecimal totalCharge) {

        this.volumetricWeight = volumetricWeight;
        this.billableWeight = billableWeight;
        this.baseCharge = baseCharge;
        this.codSurcharge = codSurcharge;
        this.totalCharge = totalCharge;
    }

    public Double getVolumetricWeight() {
        return volumetricWeight;
    }

    public Double getBillableWeight() {
        return billableWeight;
    }

    public BigDecimal getBaseCharge() {
        return baseCharge;
    }

    public BigDecimal getCodSurcharge() {
        return codSurcharge;
    }

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }
}