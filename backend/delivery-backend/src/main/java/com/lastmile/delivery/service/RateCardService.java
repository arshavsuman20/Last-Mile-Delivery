package com.lastmile.delivery.service;

import com.lastmile.delivery.entity.RateCard;
import com.lastmile.delivery.repository.RateCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateCardService {

    private final RateCardRepository rateCardRepository;

    public RateCardService(RateCardRepository rateCardRepository) {
        this.rateCardRepository = rateCardRepository;
    }

    public List<RateCard> getAllRateCards() {
        return rateCardRepository.findAll();
    }

    public RateCard updateRateCard(RateCard.OrderType orderType, RateCard updated) {
        RateCard existing = rateCardRepository.findByOrderType(orderType)
                .orElseGet(() -> RateCard.builder().orderType(orderType).build());

        existing.setIntraZoneRatePerKg(updated.getIntraZoneRatePerKg());
        existing.setInterZoneRatePerKg(updated.getInterZoneRatePerKg());
        existing.setCodSurcharge(updated.getCodSurcharge());

        return rateCardRepository.save(existing);
    }
}
