package com.hththn.dev.department_manager.repository;

import com.hththn.dev.department_manager.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FundRepository extends JpaRepository<Fund, Long>, JpaSpecificationExecutor<Fund> {
    Fund findByFundCode(String fundCode);
}
