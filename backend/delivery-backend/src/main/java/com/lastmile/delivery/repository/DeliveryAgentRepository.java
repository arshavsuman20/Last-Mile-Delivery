package com.lastmile.delivery.repository;

import com.lastmile.delivery.entity.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {

    List<DeliveryAgent> findByAvailableTrue();
    List<DeliveryAgent> findByAvailableTrueAndZoneId(Long zoneId);
    java.util.Optional<DeliveryAgent> findByUserId(Long userId);
}