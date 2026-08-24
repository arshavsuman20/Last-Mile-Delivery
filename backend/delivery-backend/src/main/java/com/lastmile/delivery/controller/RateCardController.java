package com.lastmile.delivery.controller;

import com.lastmile.delivery.entity.RateCard;
import com.lastmile.delivery.service.RateCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rates/cards")
public class RateCardController {

    private final RateCardService rateCardService;

    public RateCardController(RateCardService rateCardService) {
        this.rateCardService = rateCardService;
    }

    @GetMapping
    public ResponseEntity<List<RateCard>> getAllRateCards() {
        return ResponseEntity.ok(rateCardService.getAllRateCards());
    }

    @PutMapping("/{orderType}")
    public ResponseEntity<RateCard> updateRateCard(
            @PathVariable RateCard.OrderType orderType,
            @RequestBody RateCard rateCard) {
        return ResponseEntity.ok(rateCardService.updateRateCard(orderType, rateCard));
    }
}
