package com.lastmile.delivery.service;

import com.lastmile.delivery.entity.Zone;
import com.lastmile.delivery.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found with ID: " + id));
    }

    public Zone createZone(Zone zone) {
        return zoneRepository.save(zone);
    }
}
