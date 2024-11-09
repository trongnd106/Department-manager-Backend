package com.hththn.dev.department_manager.service;

import com.hththn.dev.department_manager.constant.ResidentEnum;
import com.hththn.dev.department_manager.dto.request.ResidentCreateRequest;
import com.hththn.dev.department_manager.dto.response.ApiResponse;
import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.dto.response.UserResponse;
import com.hththn.dev.department_manager.entity.Apartment;
import com.hththn.dev.department_manager.entity.Resident;
import com.hththn.dev.department_manager.entity.User;
import com.hththn.dev.department_manager.exception.UserInfoException;
import com.hththn.dev.department_manager.repository.ResidentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ResidentService {
    private final ResidentRepository residentRepository;

    public PaginatedResponse<Resident> fetchAllResidents(Specification<Resident> spec, Pageable pageable) {
        Page<Resident> pageResident = this.residentRepository.findAll(spec, pageable);
        PaginatedResponse<Resident> page = new PaginatedResponse<>();
        page.setPageSize(pageable.getPageSize());
        page.setCurPage(pageable.getPageNumber()+1);
        page.setTotalPages(pageResident.getTotalPages());
        page.setTotalElements(pageResident.getNumberOfElements());
        page.setResult(pageResident.getContent());
        return page;
    }

    public Resident fetchResidentById(Long id) throws Exception {
        return this.residentRepository.findById(id).orElseThrow(()-> new UserInfoException("Resident with id = "+id+ " is not found"));
    }

    public Resident createResident(ResidentCreateRequest resident) throws Exception {
        Resident resident1 = new Resident();
        resident1.setId(resident.getId());
        resident1.setName(resident.getName());
        resident1.setDob(resident.getDob());
        resident1.setStatus(ResidentEnum.fromString(resident.getStatus()));
        resident1.setAddressNumber(resident.getAddressNumber());
        return this.residentRepository.save(resident1);
    }

    public Resident updateResident(Resident resident) throws Exception {
        Resident oldResident = this.fetchResidentById(resident.getId());
        if(resident.getName()!=null) oldResident.setName(resident.getName());
        if(resident.getAddressNumber()!=null) oldResident.setAddressNumber(resident.getAddressNumber());
        if(resident.getDob()!=null) oldResident.setDob(resident.getDob());
        if(resident.getStatus()!=null) oldResident.setStatus(resident.getStatus());
        return this.residentRepository.save(oldResident);
    }

    public ApiResponse<String> deleteResident(Long id) throws Exception {
        Resident resident = this.fetchResidentById(id);
        this.residentRepository.delete(resident);
        ApiResponse<String> response = new ApiResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("delete resident success");
        response.setData(null);
        return response;
    }

}
