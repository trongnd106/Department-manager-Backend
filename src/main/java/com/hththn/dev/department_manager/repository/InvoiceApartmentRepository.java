package com.hththn.dev.department_manager.repository;

import com.hththn.dev.department_manager.dto.response.InvoiceApartmentResponse;
import com.hththn.dev.department_manager.entity.Fee;
import com.hththn.dev.department_manager.entity.Invoice;
import com.hththn.dev.department_manager.entity.InvoiceApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InvoiceApartmentRepository extends JpaRepository<InvoiceApartment, Long>, JpaSpecificationExecutor<InvoiceApartment> {
    @Query("SELECT new com.hththn.dev.department_manager.dto.response.InvoiceApartmentResponse(ia.invoice.id, ia.invoice.name, ia.invoice.description, ia.invoice.updatedAt, ia.paymentStatus) " +
            "FROM InvoiceApartment ia WHERE ia.apartment.addressNumber = :apartmentId")
    List<InvoiceApartmentResponse> findInvoicesByApartmentId(@Param("apartmentId") Long apartmentId);

    @Modifying //Required to be used in queries that change data such as UPDATE, DELETE (i.e. not a SELECT query.)
    @Transactional
    @Query("DELETE FROM InvoiceApartment ia WHERE ia.invoice.id = :invoiceId")
    void deleteByInvoiceId(String invoiceId);
}

