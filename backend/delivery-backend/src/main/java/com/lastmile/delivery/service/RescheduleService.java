package com.lastmile.delivery.service;

import com.lastmile.delivery.entity.DeliveryAgent;
import com.lastmile.delivery.entity.Order;
import com.lastmile.delivery.entity.Reschedule;
import com.lastmile.delivery.repository.DeliveryAgentRepository;
import com.lastmile.delivery.repository.OrderRepository;
import com.lastmile.delivery.repository.RescheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RescheduleService {

    private final RescheduleRepository rescheduleRepository;
    private final OrderRepository orderRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final TrackingService trackingService;
    private final AssignmentService assignmentService;

    public RescheduleService(
            RescheduleRepository rescheduleRepository,
            OrderRepository orderRepository,
            DeliveryAgentRepository deliveryAgentRepository,
            TrackingService trackingService,
            AssignmentService assignmentService) {

        this.rescheduleRepository = rescheduleRepository;
        this.orderRepository = orderRepository;
        this.deliveryAgentRepository = deliveryAgentRepository;
        this.trackingService = trackingService;
        this.assignmentService = assignmentService;
    }

    public Reschedule reschedule(
            Long orderId,
            LocalDate newDeliveryDate,
            String reason,
            Long actorId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (newDeliveryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "New delivery date must be in the future");
        }

        DeliveryAgent oldAgent = order.getAssignedAgent();

        if (oldAgent != null) {
            oldAgent.setAvailable(true);
            deliveryAgentRepository.save(oldAgent);
        }

        trackingService.updateStatus(
                orderId,
                Order.Status.FAILED,
                actorId
        );

        Reschedule reschedule = Reschedule.builder()
                .order(order)
                .previousAttemptDate(LocalDate.now())
                .newDeliveryDate(newDeliveryDate)
                .reason(reason)
                .build();

        Reschedule saved = rescheduleRepository.save(reschedule);

        try {
            assignmentService.autoAssignOrder(orderId);
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Auto-assignment skipped during reschedule: "
                            + e.getMessage()
            );
        }

        return saved;
    }
}
