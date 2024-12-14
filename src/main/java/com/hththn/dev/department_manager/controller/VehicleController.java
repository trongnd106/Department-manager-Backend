package com.hththn.dev.department_manager.controller;


import com.hththn.dev.department_manager.dto.response.ApiResponse;
import com.hththn.dev.department_manager.entity.Vehicle;
import com.hththn.dev.department_manager.service.VehicleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "http://localhost:5173")
public class VehicleController {
    VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(this.vehicleService.create(vehicle));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Vehicle>> getAllVehiclesById(@PathVariable("id") long apartmentId) {
        return ResponseEntity.ok(this.vehicleService.findAll(apartmentId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteVehicle(@PathVariable("id") Long apartmentId, @RequestBody Vehicle vehicle) throws Exception {
        return this.vehicleService.deleteVehicle(apartmentId, vehicle);
    }
}
