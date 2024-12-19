package com.hththn.dev.department_manager.service;

import com.hththn.dev.department_manager.dto.response.ApiResponse;
import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.entity.Apartment;
import com.hththn.dev.department_manager.entity.Vehicle;
import com.hththn.dev.department_manager.repository.ApartmentRepository;
import com.hththn.dev.department_manager.repository.VehicleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleService {
    VehicleRepository vehicleRepository;
    ApartmentRepository apartmentRepository;

    @Transactional
    public List<Vehicle> findAllByApartmentId(long apartmentId) {
        if (!this.apartmentRepository.existsById(apartmentId)) {
            throw new RuntimeException("Apartment with id " + apartmentId + " does not exist");
        }
        return this.vehicleRepository.findAllByApartment_AddressNumber(apartmentId);
    }

    @Transactional
    public PaginatedResponse<Vehicle> getAll(Specification<Vehicle> spec, Pageable pageable) {
        Page<Vehicle> pageVehicle = vehicleRepository.findAll(spec,pageable);
        return PaginatedResponse.<Vehicle>builder()
                .pageSize(pageable.getPageSize())
                .curPage(pageable.getPageNumber())
                .totalPages(pageVehicle.getTotalPages())
                .totalElements(pageVehicle.getNumberOfElements())
                .result(pageVehicle.getContent())
                .build();
    }

    @Transactional
    public Vehicle create(Vehicle vehicleRequest) {
        if (this.vehicleRepository.findById(vehicleRequest.getId()).isPresent()) {
            throw new RuntimeException("Vehicle with id = " + vehicleRequest.getId()+ " already exists");
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleRequest.getId());
        vehicle.setCategory(vehicleRequest.getCategory());
        vehicle.setApartment(this.apartmentRepository.findById(vehicleRequest.getApartmentId()).orElse(null));
        return this.vehicleRepository.save(vehicle);
    }

    @Transactional
    public ApiResponse<String> deleteVehicle(Long id, Vehicle vehicleRequest) throws Exception {
        Apartment apartment = this.apartmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Apartment with id " + id + " does not exist"));
        Vehicle vehicle = this.vehicleRepository.findById(vehicleRequest.getId()).orElse(null);
        List<Vehicle> vehicleList = apartment.getVehicleList();
        vehicleList.remove(vehicle);
        apartment.setVehicleList(vehicleList);
        apartmentRepository.saveAndFlush(apartment);
        assert vehicle != null;
        this.vehicleRepository.delete(vehicle);
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("delete vehicle success");
        response.setData(null);
        return response;
    }
}
