package com.hththn.dev.department_manager.repository;

import com.hththn.dev.department_manager.entity.Fee;
import com.hththn.dev.department_manager.entity.FeeInvoice;
import com.hththn.dev.department_manager.entity.Resident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeInvoiceRepository extends JpaRepository<FeeInvoice, Long>, JpaSpecificationExecutor<FeeInvoice> {
    @Query("SELECT fi.fee FROM FeeInvoice fi WHERE fi.invoice.id = :invoiceId")
    List<Fee> findFeesByInvoiceId(@Param("invoiceId") String invoiceId);

    @Modifying //Required to be used in queries that change data such as UPDATE, DELETE (i.e. not a SELECT query.)
    @Transactional
    @Query("DELETE FROM FeeInvoice fi WHERE fi.invoice.id = :invoiceId")
    void deleteByInvoiceId(String invoiceId);


}
