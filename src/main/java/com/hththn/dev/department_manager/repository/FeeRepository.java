package com.hththn.dev.department_manager.repository;

import com.hththn.dev.department_manager.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long>, JpaSpecificationExecutor<Fee> {

}
