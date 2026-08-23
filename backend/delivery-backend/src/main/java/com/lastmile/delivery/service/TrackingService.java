package com.lastmile.delivery.service;

import com.lastmile.delivery.repository.TrackingHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    private final TrackingHistoryRepository trackingHistoryRepository;

    public TrackingService(TrackingHistoryRepository trackingHistoryRepository) {
        this.trackingHistoryRepository = trackingHistoryRepository;
    }
}