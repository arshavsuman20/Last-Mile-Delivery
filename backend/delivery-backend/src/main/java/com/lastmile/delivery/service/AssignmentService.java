package com.lastmile.delivery.service;

import com.lastmile.delivery.entity.Assignment;
import com.lastmile.delivery.entity.DeliveryAgent;
import com.lastmile.delivery.entity.Order;
import com.lastmile.delivery.repository.AssignmentRepository;
import com.lastmile.delivery.repository.DeliveryAgentRepository;
import com.lastmile.delivery.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final OrderRepository orderRepository;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            DeliveryAgentRepository deliveryAgentRepository,
            OrderRepository orderRepository) {

        this.assignmentRepository = assignmentRepository;
        this.deliveryAgentRepository = deliveryAgentRepository;
        this.orderRepository = orderRepository;
    }

    public Assignment assignOrder(Long orderId, Long agentId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));

        if (!agent.getAvailable()) {
            throw new IllegalArgumentException("Delivery agent is not available");
        }

        order.setAssignedAgent(agent);
        orderRepository.save(order);

        agent.setAvailable(false);
        deliveryAgentRepository.save(agent);

        Assignment assignment = Assignment.builder()
                .order(order)
                .agent(agent)
                .assignmentType(Assignment.AssignmentType.MANUAL)
                .build();

        return assignmentRepository.save(assignment);
    }

    public List<DeliveryAgent> getAvailableAgents() {
        return deliveryAgentRepository.findByAvailableTrue();
    }

    public Assignment autoAssignOrder(Long orderId) {
        return autoAssignOrder(orderId, Assignment.AssignmentType.AUTO);
    }

    public Assignment rescheduleAssignOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        DeliveryAgent previousAgent = order.getAssignedAgent();

        if (previousAgent != null) {
            previousAgent.setAvailable(true);
            deliveryAgentRepository.save(previousAgent);
        }

        order.setAssignedAgent(null);
        orderRepository.save(order);

        return autoAssignOrder(orderId, Assignment.AssignmentType.RESCHEDULE);
    }

    private Assignment autoAssignOrder(
            Long orderId,
            Assignment.AssignmentType assignmentType) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Long zoneId = order.getPickupArea().getZone().getId();

        List<DeliveryAgent> agents =
                deliveryAgentRepository.findByAvailableTrueAndZoneId(zoneId);

        if (agents.isEmpty()) {
            throw new IllegalArgumentException(
                    "No available delivery agent in pickup zone");
        }

        DeliveryAgent agent = agents.get(0);

        order.setAssignedAgent(agent);
        orderRepository.save(order);

        agent.setAvailable(false);
        deliveryAgentRepository.save(agent);

        Assignment assignment = Assignment.builder()
                .order(order)
                .agent(agent)
                .assignmentType(assignmentType)
                .build();

        return assignmentRepository.save(assignment);
    }
}
