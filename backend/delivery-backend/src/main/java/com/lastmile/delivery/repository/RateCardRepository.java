package com.lastmile.delivery.repository;

import com.lastmile.delivery.entity.RateCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateCardRepository extends JpaRepository<RateCard, Long> {
}