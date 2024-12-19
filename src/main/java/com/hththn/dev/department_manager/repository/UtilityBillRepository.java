package com.hththn.dev.department_manager.repository;

import com.hththn.dev.department_manager.entity.Invoice;
import com.hththn.dev.department_manager.entity.UtilityBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilityBillRepository extends JpaRepository<UtilityBill, String>, JpaSpecificationExecutor<UtilityBill> {
    Optional<UtilityBill> findById(Long id);
}
