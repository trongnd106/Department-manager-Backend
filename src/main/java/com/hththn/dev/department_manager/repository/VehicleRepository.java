package com.hththn.dev.department_manager.repository;

import com.hththn.dev.department_manager.entity.UtilityBill;
import com.hththn.dev.department_manager.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    List<Vehicle> findAllByApartment_AddressNumber(Long addressNumber);
    Optional<Vehicle> findById(String id);
}
