package com.hththn.dev.department_manager.service;

import com.hththn.dev.department_manager.constant.FeeTypeEnum;
import com.hththn.dev.department_manager.constant.PaymentEnum;
import com.hththn.dev.department_manager.dto.request.InvoiceRequest;
import com.hththn.dev.department_manager.dto.response.*;
import com.hththn.dev.department_manager.entity.*;
import com.hththn.dev.department_manager.exception.UserInfoException;
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
import java.util.*;
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
        page.setCurPage(pageable.getPageNumber());
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
                                    invoice.getIsActive(),
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
        if (invoice.getIsActive() == 0) {
            throw new RuntimeException("Invoice with id " + id + " is not active");
        }
        LocalDate localDate = invoice.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
        List<Fee> feeList = feeInvoiceRepository.findFeesByInvoiceId(id);

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .isActive(invoice.getIsActive())
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
        List<InvoiceApartmentResponse> invoiceApartmentResponseList = invoiceApartmentRepository.findInvoicesByApartmentId(id);
        invoiceApartmentResponseList.forEach(response -> {
            InvoiceApartment invoiceApartment = invoiceApartmentRepository.findByInvoiceIdAndApartmentAddressNumber(response.getId(),id);
            // Fetch fees by invoice ID
            List<Fee> feeList = feeInvoiceRepository.findFeesByInvoiceId(response.getId());

            // Map fees to FeeResponse and calculate amount
            List<FeeResponse> feeResponses = feeList.stream()
                    .map(fee -> {
                        double amount;

                        // Process the amount based on the fee type
                        if (fee.getFeeTypeEnum() == FeeTypeEnum.DepartmentFee) {
                            // Apartment fee: calculated based on the apartment area
                            amount = fee.getUnitPrice().doubleValue() * apartment.getArea();
                        } else if (fee.getFeeTypeEnum() == FeeTypeEnum.VehicleFee) {
                            // Vehicle fee: calculated based on the number of cars and motorbikes
                            long totalVehicles = apartment.getNumberOfCars() + apartment.getNumberOfMotorbikes();
                            amount = fee.getUnitPrice().doubleValue() * totalVehicles;
                        } else if (fee.getFeeTypeEnum() == FeeTypeEnum.ContributionFund) {
                            // Contribution fee: retrieved from `feeAmount` stored in InvoiceApartment
                            amount = invoiceApartment.getFeeAmounts().getOrDefault(fee.getId(), 0.0); // feeAmountMap contains {feeId -> amount}
                        } else {
                            // Default: amount is 0
                            amount = 0.0;
                        }

                        // Create FeeResponse
                        return new FeeResponse(fee.getName(), fee.getId(), fee.getFeeTypeEnum(), amount);
                    })
                    .toList();


            // Set the feeResponses to the response (Assuming there's a setter for fees)
            response.setFeeList(feeResponses);
        });

        return invoiceApartmentResponseList;
    }

    @Transactional
    public List<InvoiceApartmentResponse> updateContributionFund(Long apartmentId, String invoiceId, Map<Long, Double> feeAmounts) throws RuntimeException {
        InvoiceApartment invoiceApartment = invoiceApartmentRepository.findByInvoiceIdAndApartmentAddressNumber(invoiceId, apartmentId);
        // Update the feeAmountMap field
        if (invoiceApartment.getFeeAmounts() == null) {
            invoiceApartment.setFeeAmounts(new HashMap<>());
        }
        invoiceApartment.getFeeAmounts().putAll(feeAmounts);
        // Save the updated entity
        invoiceApartmentRepository.save(invoiceApartment);
        return fetchAllInvoicesByApartmentId(apartmentId);
    }

    @Transactional
    public InvoiceApartment updateInvoiceApartment(Long id) throws RuntimeException {
        InvoiceApartment invoiceApartment = invoiceApartmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found id " + id));
        invoiceApartment.setPaymentStatus(PaymentEnum.Paid);
        return invoiceApartmentRepository.save(invoiceApartment);
    }

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) throws RuntimeException {
        Invoice invoice = new Invoice();
        //Check if the invoice exists or not
        if (invoiceRepository.findById(request.getInvoiceId()).isPresent()) {
            Invoice response = invoiceRepository.findById(request.getInvoiceId()).get();
            if(response.getIsActive()==1) throw new RuntimeException("Invoice with id = " + request.getInvoiceId() + " is already actived");
            else invoice.setIsActive(1);
        }
        // If it does not exist, create a new Invoice
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
                .isActive(invoice.getIsActive())
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
                .isActive(invoice.getIsActive())
                .id(invoice.getId())
                .name(invoice.getName())
                .description(invoice.getDescription())
                .lastUpdated(localDate)
                .feeList(feeListAfterUpdate)
                .build();
    }

    public ApiResponse<String> deleteInvoice(String id) throws RuntimeException {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice with code = " + id + " is not found"));
        //Delete all record by invoiceId in fee_invoice table
        feeInvoiceRepository.deleteByInvoiceId(id);
        //Delete all record by invoiceId in invoice_apartment table
        invoiceApartmentRepository.deleteByInvoiceId(id);
        invoice.setIsActive(0);
        invoiceRepository.save(invoice);
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("delete invoice success");
        response.setData(null);
        return response;
    }
}

