package com.hththn.dev.department_manager.service;

import com.hththn.dev.department_manager.constant.PaymentEnum;
import com.hththn.dev.department_manager.dto.request.InvoiceRequest;
import com.hththn.dev.department_manager.dto.response.ApiResponse;
import com.hththn.dev.department_manager.dto.response.InvoiceApartmentResponse;
import com.hththn.dev.department_manager.dto.response.InvoiceResponse;
import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.entity.*;
import com.hththn.dev.department_manager.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final FeeRepository feeRepository;
    private final FeeInvoiceRepository feeInvoiceRepository;
    private final ApartmentRepository apartmentRepository;
    private final InvoiceApartmentRepository invoiceApartmentRepository;

    public PaginatedResponse<InvoiceResponse> fetchAllInvoices(Specification<Invoice> spec, Pageable pageable) {
        Page<Invoice> pageInvoice = invoiceRepository.findAll(spec, pageable);
        PaginatedResponse<InvoiceResponse> page = new PaginatedResponse<>();
        page.setPageSize(pageable.getPageSize());
        page.setCurPage(pageable.getPageNumber()+1);
        page.setTotalPages(pageInvoice.getTotalPages());
        page.setTotalElements(pageInvoice.getNumberOfElements());
        //map Invoice to InvoiceResponse
        page.setResult(
                pageInvoice.getContent()
                        .stream()
                        .map(invoice -> {
                            // transform Instant to LocalDate
                            LocalDate updatedAtLocalDate = invoice.getUpdatedAt()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

                            // Extract Fee from FeeInvoice
                            List<Fee> feeList = invoice.getFeeInvoices()
                                    .stream()
                                    .map(FeeInvoice::getFee)
                                    .collect(Collectors.toList());

                            // Create InvoiceResponse
                            return new InvoiceResponse(
                                    invoice.getId(),
                                    invoice.getName(),
                                    invoice.getDescription(),
                                    updatedAtLocalDate,
                                    feeList
                            );
                        })
                        .collect(Collectors.toList())
        );
        return page;
    }

    @Transactional
    public InvoiceResponse fetchInvoiceById(String id) throws RuntimeException {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice with code = " + id + " is not found"));

        LocalDate localDate = invoice.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
        List<Fee> feeList = feeInvoiceRepository.findFeesByInvoiceId(id);

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .name(invoice.getName())
                .description(invoice.getDescription())
                .lastUpdated(localDate)
                .feeList(feeList)
                .build();
    }

    @Transactional
    public List<InvoiceApartmentResponse> fetchAllInvoicesByApartmentId(Long id) throws RuntimeException {
        Apartment apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found apartment " + id));
        return invoiceApartmentRepository.findInvoicesByApartmentId(id);
    }

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) throws RuntimeException {
        //Check if the invoice exists or not
        if (invoiceRepository.findById(request.getInvoiceId()).isPresent()) {
            throw new RuntimeException("Invoice with id = " + request.getInvoiceId() + " already exists");
        }
        // If it does not exist, create a new Invoice
        Invoice invoice = new Invoice();
        invoice.setId(request.getInvoiceId());
        invoice.setName(request.getName());
        invoice.setDescription(request.getDescription());
        invoiceRepository.save(invoice); //Must save here early to have information provided for side table 'fee_invoice'

        List<Fee> feeList = feeRepository.findAllById(request.getFeeIds()); //Get all feeIDs from the request and save it as a list
        for (Fee f : feeList) {
            FeeInvoice feeInvoice = new FeeInvoice();
            feeInvoice.setFee(f);
            feeInvoice.setInvoice(invoiceRepository.findById(request.getInvoiceId()).orElse(null));
            feeInvoiceRepository.save(feeInvoice);
        }

        LocalDate localDate = Objects.requireNonNull(invoiceRepository.findById(invoice.getId()).orElse(null)).getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
        List<Fee> feeListAfterCreate = feeInvoiceRepository.findFeesByInvoiceId(request.getInvoiceId());

        List<Apartment> apartmentList = apartmentRepository.findAll();
        for (Apartment a : apartmentList) {
            InvoiceApartment invoiceApartment = new InvoiceApartment();
            invoiceApartment.setApartment(a);
            invoiceApartment.setInvoice(invoice);
            invoiceApartment.setPaymentStatus(PaymentEnum.Unpaid);
            invoiceApartmentRepository.save(invoiceApartment);
        }

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .name(invoice.getName())
                .description(invoice.getDescription())
                .lastUpdated(localDate)
                .feeList(feeListAfterCreate)
                .build();
    }

    public InvoiceResponse updateInvoice (InvoiceRequest request) throws RuntimeException {
        // Fetch invoice by ID
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId()).orElseThrow(() -> new RuntimeException("Invoice with code = " + request.getInvoiceId() + " is not found"));

        // Update invoice fields
        invoice.setId(request.getInvoiceId());
        invoice.setName(request.getName());
        invoice.setDescription(request.getDescription());
        invoiceRepository.save(invoice);

        // Delete all FeeInvoice records for the invoiceId
        feeInvoiceRepository.deleteByInvoiceId(request.getInvoiceId());
        List<Fee> feeList = feeRepository.findAllById(request.getFeeIds()); //Get all feeIDs from the request and save it as a list
        for (Fee f : feeList) {
            FeeInvoice feeInvoice = new FeeInvoice();
            feeInvoice.setFee(f);
            //feeInvoice.setInvoice(invoiceRepository.findById(request.getInvoiceId()).orElse(null));
            feeInvoice.setInvoice(invoice); // No need to call repository again as above.
            feeInvoiceRepository.save(feeInvoice);
        }

        LocalDate localDate = Objects.requireNonNull(invoiceRepository.findById(invoice.getId()).orElse(null)).getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
        // Fetch updated fee list
        List<Fee> feeListAfterUpdate = feeInvoiceRepository.findFeesByInvoiceId(invoice.getId());

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .name(invoice.getName())
                .description(invoice.getDescription())
                .lastUpdated(localDate)
                .feeList(feeListAfterUpdate)
                .build();
    }

    public ApiResponse<String> deleteInvoice(String id) throws RuntimeException {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        //Delete all record by invoiceId in fee_invoice table
        feeInvoiceRepository.deleteByInvoiceId(id);
        //Delete the invoice record in invoices table
        invoiceRepository.deleteById(id);
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("delete invoice success");
        response.setData(null);
        return response;
    }
}

