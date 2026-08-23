package com.lastmile.delivery.repository;

import com.lastmile.delivery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByStatus(Order.Status status);

    List<Order> findByPickupAreaZoneId(Long zoneId);

    List<Order> findByAssignedAgentId(Long agentId);
}