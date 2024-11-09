package com.hththn.dev.department_manager.service;

import com.hththn.dev.department_manager.dto.request.FeeCreateRequest;
import com.hththn.dev.department_manager.dto.response.ApiResponse;
import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.entity.Fee;
import com.hththn.dev.department_manager.entity.Resident;
import com.hththn.dev.department_manager.exception.UserInfoException;
import com.hththn.dev.department_manager.repository.FeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class FeeService {
    private final FeeRepository feeRepository;

    public PaginatedResponse<Fee> fetchAllFees(Specification<Fee> spec, Pageable pageable) {
        Page<Fee> pageFee = feeRepository.findAll(spec, pageable);
        PaginatedResponse<Fee> page = new PaginatedResponse<>();
        page.setPageSize(pageable.getPageSize());
        page.setCurPage(pageable.getPageNumber()+1);
        page.setTotalPages(pageFee.getTotalPages());
        page.setTotalElements(pageFee.getNumberOfElements());
        page.setResult(pageFee.getContent());
        return page;
    }

    public Fee fetchFeeById (Long id) throws Exception {
        return feeRepository.findById(id).orElseThrow(() -> new UserInfoException("Fee with code = " + id + " is not found"));
    }

    public Fee createFee (FeeCreateRequest feeCreateRequest) throws Exception {
        Fee fee = new Fee();
        fee.setName(feeCreateRequest.getName());
        fee.setDescription(feeCreateRequest.getDescription());
        fee.setFeeTypeEnum(feeCreateRequest.getFeeTypeEnum());
        fee.setUnitPrice(feeCreateRequest.getUnitPrice());
        return this.feeRepository.save(fee);
    }

    public Fee updateFee (Fee fee) throws Exception {
        Fee oldFee = this.fetchFeeById(fee.getId());
        if(oldFee != null) {
            if(fee.getName() != null) oldFee.setName(fee.getName());
            if(fee.getDescription() != null) oldFee.setDescription(fee.getDescription());
            if(fee.getFeeTypeEnum() != null) oldFee.setFeeTypeEnum(fee.getFeeTypeEnum());
            if(fee.getUnitPrice() != null) oldFee.setUnitPrice(fee.getUnitPrice());
        } else {
            throw new Exception("Fee with code = " + fee.getId() + " is not found");
        }
        return this.feeRepository.save(oldFee);
    }

    //No exception handling is needed in this method
    public ApiResponse<String> deleteFee(Long id) throws Exception {
       Fee fee = this.fetchFeeById(id);
       this.feeRepository.delete(fee);

       ApiResponse<String> response = new ApiResponse<>();
       response.setCode(HttpStatus.OK.value());
       response.setMessage("delete fee success");
       response.setData(null);
       return response;
    }
}

