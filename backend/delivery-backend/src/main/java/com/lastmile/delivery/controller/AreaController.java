package com.lastmile.delivery.controller;

import com.lastmile.delivery.entity.Area;
import com.lastmile.delivery.service.AreaService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    public ResponseEntity<List<Area>> getAllAreas(
            @RequestParam(required = false) Long zoneId) {
        if (zoneId != null) {
            return ResponseEntity.ok(areaService.getAreasByZoneId(zoneId));
        }
        return ResponseEntity.ok(areaService.getAllAreas());
    }

    @PostMapping
    public ResponseEntity<Area> createArea(@RequestBody CreateAreaDto dto) {
        return ResponseEntity.ok(areaService.createArea(dto.getName(), dto.getZoneId()));
    }

    @Data
    public static class CreateAreaDto {
        private String name;
        private Long zoneId;
    }
}
