package com.hththn.dev.department_manager.repository;

import com.hththn.dev.department_manager.entity.Fee;
import com.hththn.dev.department_manager.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceRepository extends JpaRepository<Invoice, String>, JpaSpecificationExecutor<Invoice> {
}
