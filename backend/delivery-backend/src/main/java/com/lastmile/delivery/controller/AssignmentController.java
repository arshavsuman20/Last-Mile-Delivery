package com.lastmile.delivery.controller;

import com.lastmile.delivery.entity.Assignment;
import com.lastmile.delivery.entity.DeliveryAgent;
import com.lastmile.delivery.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/available-agents")
    public ResponseEntity<List<DeliveryAgent>> getAvailableAgents() {
        return ResponseEntity.ok(
                assignmentService.getAvailableAgents()
        );
    }

    @PostMapping("/{orderId}/agent/{agentId}")
    public ResponseEntity<Assignment> assignOrder(
            @PathVariable Long orderId,
            @PathVariable Long agentId) {

        return ResponseEntity.ok(
                assignmentService.assignOrder(orderId, agentId)
        );
    }
}
