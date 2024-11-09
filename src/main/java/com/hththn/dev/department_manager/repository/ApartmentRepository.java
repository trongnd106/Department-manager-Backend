package com.hththn.dev.department_manager.repository;

import com.hththn.dev.department_manager.entity.Apartment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {
    @EntityGraph(attributePaths = {"residentList", "owner"})
    Optional<Apartment> findById(Long addressNumber);
}
