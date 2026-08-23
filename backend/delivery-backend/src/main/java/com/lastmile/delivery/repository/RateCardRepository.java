package com.lastmile.delivery.repository;

import com.lastmile.delivery.entity.RateCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateCardRepository extends JpaRepository<RateCard, Long> {

    Optional<RateCard> findByOrderType(RateCard.OrderType orderType);
}