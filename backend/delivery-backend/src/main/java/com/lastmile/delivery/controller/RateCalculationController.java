package com.lastmile.delivery.controller;

import com.lastmile.delivery.dto.RateCalculationRequest;
import com.lastmile.delivery.dto.RateCalculationResponse;
import com.lastmile.delivery.service.RateCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rates")
public class RateCalculationController {

    private final RateCalculationService rateCalculationService;

    public RateCalculationController(
            RateCalculationService rateCalculationService) {
        this.rateCalculationService = rateCalculationService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<RateCalculationResponse> calculate(
            @RequestBody RateCalculationRequest request) {

        return ResponseEntity.ok(
                rateCalculationService.calculate(request)
        );
    }
}