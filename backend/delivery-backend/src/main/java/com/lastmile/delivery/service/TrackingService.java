package com.lastmile.delivery.service;

import com.lastmile.delivery.entity.Order;
import com.lastmile.delivery.entity.TrackingHistory;
import com.lastmile.delivery.entity.User;
import com.lastmile.delivery.repository.OrderRepository;
import com.lastmile.delivery.repository.TrackingHistoryRepository;
import com.lastmile.delivery.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackingService {
    private final NotificationService notificationService;

    private final TrackingHistoryRepository trackingHistoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public TrackingService(
            TrackingHistoryRepository trackingHistoryRepository,
            OrderRepository orderRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.trackingHistoryRepository = trackingHistoryRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public TrackingHistory updateStatus(
            Long orderId,
            Order.Status status,
            Long actorId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found"));

        order.setStatus(status);
        orderRepository.save(order);

        notificationService.sendStatusNotification(order);

        TrackingHistory history = TrackingHistory.builder()
                .order(order)
                .status(status)
                .actor(actor)
                .build();

        return trackingHistoryRepository.save(history);
    }

    public List<TrackingHistory> getTrackingHistory(Long orderId) {

        return trackingHistoryRepository
            .findByOrderIdOrderByTimestampAsc(orderId);
    }
}