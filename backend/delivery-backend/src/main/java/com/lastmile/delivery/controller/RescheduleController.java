package com.lastmile.delivery.controller;

import com.lastmile.delivery.entity.Reschedule;
import com.lastmile.delivery.service.RescheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reschedules")
public class RescheduleController {

    private final RescheduleService rescheduleService;

    public RescheduleController(RescheduleService rescheduleService) {
        this.rescheduleService = rescheduleService;
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<Reschedule> reschedule(
            @PathVariable Long orderId,
            @RequestParam LocalDate newDeliveryDate,
            @RequestParam String reason,
            @RequestParam Long actorId) {

        return ResponseEntity.ok(
                rescheduleService.reschedule(
                        orderId,
                        newDeliveryDate,
                        reason,
                        actorId
                )
        );
    }
}