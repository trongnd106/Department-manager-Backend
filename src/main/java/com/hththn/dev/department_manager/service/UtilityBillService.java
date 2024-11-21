package com.hththn.dev.department_manager.service;

import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.entity.Apartment;
import com.hththn.dev.department_manager.entity.Fee;
import com.hththn.dev.department_manager.entity.UtilityBill;
import com.hththn.dev.department_manager.repository.ApartmentRepository;
import com.hththn.dev.department_manager.repository.UtilityBillRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UtilityBillService {
    UtilityBillRepository utilityBillRepository;
    ApartmentRepository apartmentRepository;

    public List<UtilityBill> importExcel(MultipartFile file) {
        List<UtilityBill> utilityBills = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // Skip the header row
                if (row.getRowNum() == 0) continue;

                Long apartmentId = (long) row.getCell(0).getNumericCellValue();
                double electricity = row.getCell(1).getNumericCellValue();
                double water = row.getCell(2).getNumericCellValue();
                double internet = row.getCell(3).getNumericCellValue();

                Apartment apartment = apartmentRepository.findById(apartmentId)
                        .orElseThrow(() -> new RuntimeException("Apartment not found: " + apartmentId));

                UtilityBill utilityBill = UtilityBill.builder()
                        .apartment(apartment)
                        .electricity(electricity)
                        .water(water)
                        .internet(internet)
                        .build();

                utilityBills.add(utilityBill);
            }

            // Save to database
            utilityBillRepository.saveAll(utilityBills);
        } catch (Exception e) {
            throw new RuntimeException("Error while reading Excel file", e);
        }
        return utilityBills;
    }

    public PaginatedResponse<UtilityBill> fetchUtilityBills(Specification<UtilityBill> spec, Pageable pageable) {
        Page<UtilityBill> pageUtilityBill = utilityBillRepository.findAll(spec, pageable);
        PaginatedResponse<UtilityBill> page = new PaginatedResponse<>();
        page.setPageSize(pageable.getPageSize());
        page.setCurPage(pageable.getPageNumber()+1);
        page.setTotalPages(pageUtilityBill.getTotalPages());
        page.setTotalElements(pageUtilityBill.getNumberOfElements());
        page.setResult(pageUtilityBill.getContent());
        return page;
    }

}
