package com.lastmile.delivery.service;

import com.lastmile.delivery.entity.Area;
import com.lastmile.delivery.entity.Zone;
import com.lastmile.delivery.repository.AreaRepository;
import com.lastmile.delivery.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaService {

    private final AreaRepository areaRepository;
    private final ZoneRepository zoneRepository;

    public AreaService(AreaRepository areaRepository, ZoneRepository zoneRepository) {
        this.areaRepository = areaRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }

    public List<Area> getAreasByZoneId(Long zoneId) {
        return areaRepository.findByZoneId(zoneId);
    }

    public Area createArea(String name, Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found with ID: " + zoneId));

        Area area = Area.builder()
                .name(name)
                .zone(zone)
                .build();

        return areaRepository.save(area);
    }
}
