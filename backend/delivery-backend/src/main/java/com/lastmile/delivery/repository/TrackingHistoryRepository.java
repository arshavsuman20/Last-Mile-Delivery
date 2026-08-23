package com.lastmile.delivery.repository;

import com.lastmile.delivery.entity.TrackingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingHistoryRepository extends JpaRepository<TrackingHistory, Long> {
}