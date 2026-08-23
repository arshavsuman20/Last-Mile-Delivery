package com.lastmile.delivery.controller;

import com.lastmile.delivery.entity.Order;
import com.lastmile.delivery.entity.TrackingHistory;
import com.lastmile.delivery.service.TrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<TrackingHistory> updateStatus(
            @PathVariable Long orderId,
            @RequestParam Order.Status status,
            @RequestParam Long actorId) {

        return ResponseEntity.ok(
                trackingService.updateStatus(orderId, status, actorId)
        );
    }

    @PutMapping("/{orderId}/admin-status")
    public ResponseEntity<TrackingHistory> adminOverrideStatus(
            @PathVariable Long orderId,
            @RequestParam Order.Status status,
            @RequestParam Long actorId) {

        return ResponseEntity.ok(
                trackingService.updateStatus(orderId, status, actorId)
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<List<TrackingHistory>> getTrackingHistory(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                trackingService.getTrackingHistory(orderId)
        );
    }
}