package com.lastmile.delivery.service;

import com.lastmile.delivery.repository.DeliveryAgentRepository;
import org.springframework.stereotype.Service;

@Service
public class AssignmentService {

    private final DeliveryAgentRepository deliveryAgentRepository;

    public AssignmentService(DeliveryAgentRepository deliveryAgentRepository) {
        this.deliveryAgentRepository = deliveryAgentRepository;
    }
}