package com.hththn.dev.department_manager.controller;

import com.hththn.dev.department_manager.dto.request.InvoiceCreateRequest;
import com.hththn.dev.department_manager.dto.response.ApiResponse;
import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.entity.Fee;
import com.hththn.dev.department_manager.entity.Invoice;
import com.hththn.dev.department_manager.service.InvoiceService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/invoices")
@CrossOrigin(origins = "http://localhost:5173")
public class InvoiceController {
    private final InvoiceService invoiceService;

    //fetch all invoices
    @GetMapping
    public ResponseEntity<PaginatedResponse<Invoice>> getAllInvoices(@Filter Specification<Invoice> spec, Pageable pageable){
        PaginatedResponse<Invoice> invoiceResponses = this.invoiceService.fetchAllInvoices(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(invoiceResponses);
    }

    //fetch invoice by id
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable String id) throws Exception {
        Invoice invoice = this.invoiceService.fetchInvoiceById(id);
        return ResponseEntity.status(HttpStatus.OK).body(invoice);
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody InvoiceCreateRequest apiInvoice) throws Exception  {
        Invoice invoice = this.invoiceService.createInvoice(apiInvoice);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    //update invoice
    @PutMapping()
    public ResponseEntity<Invoice> updateInvoice(@RequestBody Invoice apiInvoice) throws Exception {
        Invoice invoice = this.invoiceService.updateInvoice(apiInvoice);
        return ResponseEntity.status(HttpStatus.OK).body(invoice);
    }

    //Delete resident by feeCode
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteInvoice(@PathVariable("id") String id) throws Exception {
        ApiResponse<String> response = this.invoiceService.deleteInvoice(id);
        return ResponseEntity.ok(response);
    }
}
