package com.lastmile.delivery.repository;

import com.lastmile.delivery.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByZoneId(Long zoneId);
}