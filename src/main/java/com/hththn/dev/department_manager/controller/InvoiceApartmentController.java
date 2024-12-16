package com.hththn.dev.department_manager.controller;

import com.hththn.dev.department_manager.dto.response.InvoiceApartmentResponse;
import com.hththn.dev.department_manager.entity.Invoice;
import com.hththn.dev.department_manager.service.InvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoiceapartment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "http://localhost:5173")
public class InvoiceApartmentController {
    InvoiceService invoiceService;

    @GetMapping("/{id}")
    public ResponseEntity<List<InvoiceApartmentResponse>> getAllInvoicesByApartmentId(@PathVariable("id") Long apartmentId) {
        return ResponseEntity.status(HttpStatus.OK).body(invoiceService.fetchAllInvoicesByApartmentId(apartmentId));
    }
}
