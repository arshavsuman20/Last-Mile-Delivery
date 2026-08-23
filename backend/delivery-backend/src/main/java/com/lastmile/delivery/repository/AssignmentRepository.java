package com.lastmile.delivery.repository;

import com.lastmile.delivery.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}