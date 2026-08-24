package com.lastmile.delivery.service;

import com.lastmile.delivery.dto.RateCalculationRequest;
import com.lastmile.delivery.dto.RateCalculationResponse;
import com.lastmile.delivery.entity.Area;
import com.lastmile.delivery.entity.Order;
import com.lastmile.delivery.entity.RateCard;
import com.lastmile.delivery.entity.Zone;
import com.lastmile.delivery.repository.AreaRepository;
import com.lastmile.delivery.repository.RateCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateCalculationServiceTest {

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private RateCardRepository rateCardRepository;

    @InjectMocks
    private RateCalculationService rateCalculationService;

    private Zone zone1;
    private Zone zone2;
    private Area area1;
    private Area area2;
    private Area area3;
    private RateCard b2cRateCard;

    @BeforeEach
    void setUp() {
        zone1 = Zone.builder().id(1L).name("Zone 1").build();
        zone2 = Zone.builder().id(2L).name("Zone 2").build();

        area1 = Area.builder().id(1L).name("Area 1").zone(zone1).build();
        area2 = Area.builder().id(2L).name("Area 2").zone(zone1).build();
        area3 = Area.builder().id(3L).name("Area 3").zone(zone2).build();

        b2cRateCard = RateCard.builder()
                .id(1L)
                .orderType(RateCard.OrderType.B2C)
                .intraZoneRatePerKg(new BigDecimal("40.00"))
                .interZoneRatePerKg(new BigDecimal("80.00"))
                .codSurcharge(new BigDecimal("50.00"))
                .build();
    }

    @Test
    void testIntraZonePrepaidCalculation() {
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area1));
        when(areaRepository.findById(2L)).thenReturn(Optional.of(area2));
        when(rateCardRepository.findByOrderType(RateCard.OrderType.B2C)).thenReturn(Optional.of(b2cRateCard));

        RateCalculationRequest request = RateCalculationRequest.builder()
                .pickupAreaId(1L)
                .dropAreaId(2L)
                .length(20.0)
                .breadth(20.0)
                .height(20.0) // volumetric = (20*20*20)/5000 = 1.6 kg
                .actualWeight(2.0) // max(2.0, 1.6) = 2.0 billable
                .orderType(Order.OrderType.B2C)
                .paymentType(Order.PaymentType.PREPAID)
                .build();

        RateCalculationResponse response = rateCalculationService.calculate(request);

        assertNotNull(response);
        assertEquals(1.6, response.getVolumetricWeight(), 0.01);
        assertEquals(2.0, response.getBillableWeight(), 0.01);
        assertEquals(0, new BigDecimal("80.00").compareTo(response.getBaseCharge())); // 2 kg * 40
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getCodSurcharge()));
        assertEquals(0, new BigDecimal("80.00").compareTo(response.getTotalCharge()));
    }

    @Test
    void testInterZoneCodCalculation() {
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area1));
        when(areaRepository.findById(3L)).thenReturn(Optional.of(area3));
        when(rateCardRepository.findByOrderType(RateCard.OrderType.B2C)).thenReturn(Optional.of(b2cRateCard));

        RateCalculationRequest request = RateCalculationRequest.builder()
                .pickupAreaId(1L)
                .dropAreaId(3L)
                .length(50.0)
                .breadth(40.0)
                .height(30.0) // volumetric = 60000/5000 = 12.0 kg
                .actualWeight(5.0) // max(5.0, 12.0) = 12.0 billable
                .orderType(Order.OrderType.B2C)
                .paymentType(Order.PaymentType.COD)
                .build();

        RateCalculationResponse response = rateCalculationService.calculate(request);

        assertNotNull(response);
        assertEquals(12.0, response.getVolumetricWeight(), 0.01);
        assertEquals(12.0, response.getBillableWeight(), 0.01);
        assertEquals(0, new BigDecimal("960.00").compareTo(response.getBaseCharge())); // 12 * 80
        assertEquals(0, new BigDecimal("50.00").compareTo(response.getCodSurcharge()));
        assertEquals(0, new BigDecimal("1010.00").compareTo(response.getTotalCharge()));
    }
}
