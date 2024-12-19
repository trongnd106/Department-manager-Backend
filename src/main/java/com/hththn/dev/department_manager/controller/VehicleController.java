package com.hththn.dev.department_manager.controller;


import com.hththn.dev.department_manager.dto.response.ApiResponse;
import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.entity.Apartment;
import com.hththn.dev.department_manager.entity.Vehicle;
import com.hththn.dev.department_manager.service.VehicleService;
import com.turkraft.springfilter.boot.Filter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<PaginatedResponse<Vehicle>> getAllVehicles(@Filter Specification<Vehicle> spec,
                                                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                                                     @RequestParam(value = "size", defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page - 1, size);
        PaginatedResponse<Vehicle> result = vehicleService.getAll(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Vehicle>> getAllVehiclesById(@PathVariable("id") long apartmentId) {
        return ResponseEntity.ok(this.vehicleService.findAllByApartmentId(apartmentId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteVehicle(@PathVariable("id") Long apartmentId, @RequestBody Vehicle vehicle) throws Exception {
        return this.vehicleService.deleteVehicle(apartmentId, vehicle);
    }
}
