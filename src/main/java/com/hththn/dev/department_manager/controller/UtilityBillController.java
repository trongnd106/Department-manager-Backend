package com.hththn.dev.department_manager.controller;

import com.hththn.dev.department_manager.dto.response.InvoiceResponse;
import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.entity.InvoiceApartment;
import com.hththn.dev.department_manager.entity.Resident;
import com.hththn.dev.department_manager.entity.UtilityBill;
import com.hththn.dev.department_manager.service.UtilityBillService;
import com.turkraft.springfilter.boot.Filter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/utilitybills")
@CrossOrigin(origins = "http://localhost:5173")
public class UtilityBillController {
    UtilityBillService utilityBillService;
    @PostMapping("/import")
    public ResponseEntity<?> importUtilityBills(@RequestParam("file") MultipartFile file, @RequestParam("date") String date) {
        List<UtilityBill> utilityBills = utilityBillService.importExcel(file,date);
        return ResponseEntity.ok(utilityBills);
    }

    @GetMapping
    public ResponseEntity<?> getAllUtilityBills(@Filter Specification<UtilityBill> spec, Pageable pageable) {
        PaginatedResponse<UtilityBill> responses = this.utilityBillService.fetchUtilityBills(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<UtilityBill> updateUtilityBill(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(utilityBillService.updateUtilityBill(id));
    }
}
