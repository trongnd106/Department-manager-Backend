package com.hththn.dev.department_manager.service;

import com.hththn.dev.department_manager.dto.request.InvoiceCreateRequest;
import com.hththn.dev.department_manager.dto.response.ApiResponse;
import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.entity.Fee;
import com.hththn.dev.department_manager.entity.Invoice;
import com.hththn.dev.department_manager.exception.UserInfoException;
import com.hththn.dev.department_manager.repository.InvoiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public PaginatedResponse<Invoice> fetchAllInvoices(Specification<Invoice> spec, Pageable pageable) {
        Page<Invoice> pageInvoice = invoiceRepository.findAll(spec, pageable);
        PaginatedResponse<Invoice> page = new PaginatedResponse<>();
        page.setPageSize(pageable.getPageSize());
        page.setCurPage(pageable.getPageNumber()+1);
        page.setTotalPages(pageInvoice.getTotalPages());
        page.setTotalElements(pageInvoice.getNumberOfElements());
        page.setResult(pageInvoice.getContent());
        return page;
    }

    public Invoice fetchInvoiceById(String id) throws UserInfoException {
        return invoiceRepository.findById(id).orElseThrow(() -> new UserInfoException("Invoice with code = " + id + " is not found"));
    }

    public Invoice createInvoice(InvoiceCreateRequest invoiceCreateRequest) throws Exception {
        if(this.invoiceRepository.findById(invoiceCreateRequest.getId()).isPresent()) throw new RuntimeException("Invoice with id = " + invoiceCreateRequest.getId() + " already exists");
        Invoice invoice = new Invoice();
        invoice.setId(invoiceCreateRequest.getId());
        invoice.setName(invoiceCreateRequest.getName());
        invoice.setDescription(invoiceCreateRequest.getDescription());
        return this.invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice (Invoice invoice) throws Exception {
        Invoice oldInvoice = this.fetchInvoiceById(invoice.getId());
        if(oldInvoice != null) {
            if(invoice.getId() != null) oldInvoice.setId(invoice.getId());
            if(invoice.getName() != null) oldInvoice.setName(invoice.getName());
            if(invoice.getDescription() != null) oldInvoice.setDescription(invoice.getDescription());
        }
        else throw new UserInfoException("Invoice id = " + invoice.getId() + " is not found");
        return this.invoiceRepository.save(oldInvoice);
    }

    public ApiResponse<String> deleteInvoice(String id) throws UserInfoException {
        Invoice invoice = this.fetchInvoiceById(id);
        invoiceRepository.delete(invoice);

        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("delete invoice success");
        response.setData(null);
        return response;
    }



}

