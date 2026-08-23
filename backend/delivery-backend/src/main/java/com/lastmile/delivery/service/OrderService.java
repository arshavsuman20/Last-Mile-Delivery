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

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getTotalCharge(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt()
        );
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
}