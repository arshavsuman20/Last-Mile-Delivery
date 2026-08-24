package com.lastmile.delivery.service;

import com.lastmile.delivery.dto.CreateOrderRequest;
import com.lastmile.delivery.dto.OrderResponse;
import com.lastmile.delivery.dto.RateCalculationRequest;
import com.lastmile.delivery.dto.RateCalculationResponse;
import com.lastmile.delivery.entity.Area;
import com.lastmile.delivery.entity.Order;
import com.lastmile.delivery.entity.User;
import com.lastmile.delivery.repository.AreaRepository;
import com.lastmile.delivery.repository.OrderRepository;
import com.lastmile.delivery.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AreaRepository areaRepository;
    private final RateCalculationService rateCalculationService;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            AreaRepository areaRepository,
            RateCalculationService rateCalculationService) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.areaRepository = areaRepository;
        this.rateCalculationService = rateCalculationService;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Customer not found"));

        Area pickupArea = areaRepository.findById(request.getPickupAreaId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Pickup area not found"));

        Area dropArea = areaRepository.findById(request.getDropAreaId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Drop area not found"));

        RateCalculationRequest rateRequest = new RateCalculationRequest();

        rateRequest.setPickupAreaId(request.getPickupAreaId());
        rateRequest.setDropAreaId(request.getDropAreaId());
        rateRequest.setLength(request.getLength());
        rateRequest.setBreadth(request.getBreadth());
        rateRequest.setHeight(request.getHeight());
        rateRequest.setActualWeight(request.getActualWeight());
        rateRequest.setOrderType(request.getOrderType());
        rateRequest.setPaymentType(request.getPaymentType());

        RateCalculationResponse rate =
                rateCalculationService.calculate(rateRequest);

        Order order = Order.builder()
                .customer(customer)
                .pickupArea(pickupArea)
                .dropArea(dropArea)
                .pickupAddress(request.getPickupAddress())
                .dropAddress(request.getDropAddress())
                .length(request.getLength())
                .breadth(request.getBreadth())
                .height(request.getHeight())
                .actualWeight(request.getActualWeight())
                .volumetricWeight(rate.getVolumetricWeight())
                .billableWeight(rate.getBillableWeight())
                .orderType(request.getOrderType())
                .paymentType(request.getPaymentType())
                .baseCharge(rate.getBaseCharge())
                .codSurcharge(rate.getCodSurcharge())
                .totalCharge(rate.getTotalCharge())
                .status(Order.Status.CREATED)
                .build();

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    public OrderResponse mapToResponse(Order order) {
        if (order == null) return null;
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getName() : null)
                .customerEmail(order.getCustomer() != null ? order.getCustomer().getEmail() : null)
                .pickupAreaId(order.getPickupArea() != null ? order.getPickupArea().getId() : null)
                .pickupAreaName(order.getPickupArea() != null ? order.getPickupArea().getName() : null)
                .pickupAddress(order.getPickupAddress())
                .dropAreaId(order.getDropArea() != null ? order.getDropArea().getId() : null)
                .dropAreaName(order.getDropArea() != null ? order.getDropArea().getName() : null)
                .dropAddress(order.getDropAddress())
                .length(order.getLength())
                .breadth(order.getBreadth())
                .height(order.getHeight())
                .actualWeight(order.getActualWeight())
                .volumetricWeight(order.getVolumetricWeight())
                .billableWeight(order.getBillableWeight())
                .orderType(order.getOrderType())
                .paymentType(order.getPaymentType())
                .baseCharge(order.getBaseCharge())
                .codSurcharge(order.getCodSurcharge())
                .totalCharge(order.getTotalCharge())
                .status(order.getStatus())
                .assignedAgentId(order.getAssignedAgent() != null ? order.getAssignedAgent().getId() : null)
                .assignedAgentName(order.getAssignedAgent() != null && order.getAssignedAgent().getUser() != null 
                        ? order.getAssignedAgent().getUser().getName() : null)
                .createdAt(order.getCreatedAt())
                .build();
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order getById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public List<Order> getByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> filterOrders(
                Order.Status status,
                Long zoneId,
                Long agentId) {

        if (status != null) {
                return orderRepository.findByStatus(status);
        }

        if (zoneId != null) {
                return orderRepository.findByPickupAreaZoneId(zoneId);
        }

        if (agentId != null) {
                return orderRepository.findByAssignedAgentId(agentId);
        }

        return orderRepository.findAll();
    }
}