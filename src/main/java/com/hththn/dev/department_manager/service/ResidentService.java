package com.hththn.dev.department_manager.service;

import com.hththn.dev.department_manager.constant.ResidentEnum;
import com.hththn.dev.department_manager.dto.request.ApartmentUpdateRequest;
import com.hththn.dev.department_manager.dto.request.ResidentCreateRequest;
import com.hththn.dev.department_manager.dto.request.ResidentUpdateRequest;
import com.hththn.dev.department_manager.dto.response.ApiResponse;
import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.dto.response.UserResponse;
import com.hththn.dev.department_manager.entity.Apartment;
import com.hththn.dev.department_manager.entity.Resident;
import com.hththn.dev.department_manager.entity.User;
import com.hththn.dev.department_manager.exception.UserInfoException;
import com.hththn.dev.department_manager.repository.ApartmentRepository;
import com.hththn.dev.department_manager.repository.ResidentRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResidentService {
    ResidentRepository residentRepository;
    ApartmentRepository apartmentRepository;

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

    @Transactional
    public Resident fetchResidentById(Long id) throws RuntimeException {
        return this.residentRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Resident with id = "+id+ " is not found"));
    }

    @Transactional
    public Resident createResident(ResidentCreateRequest resident) throws RuntimeException {
        if (this.residentRepository.findById(resident.getId()).isPresent()) {
            throw new RuntimeException("Resident with id = " + resident.getId() + " already exists");
        }

        if(resident.getAddressNumber() != null) {
            Apartment apartment = apartmentRepository.findById(resident.getAddressNumber())
                    .orElseThrow(() -> new RuntimeException("Apartment with id = " + resident.getAddressNumber() + " does not exist"));
            List<Resident> residentList = apartment.getResidentList();

            Resident resident1 = Resident.builder()
                    .id(resident.getId())
                    .name(resident.getName())
                    .dob(resident.getDob())
                    .gender(resident.getGender())
                    .cic(resident.getCic())
                    .status(ResidentEnum.fromString(resident.getStatus()))
                    .build();
            // Luu 2 chieu de dong bo, nhung createdAt dang bi null
            residentList.add(resident1);
            apartment.setResidentList(residentList);
            apartmentRepository.save(apartment);
            resident1.setApartment(apartment);

            return this.residentRepository.save(resident1);
        }
        else {
            Resident resident1 = Resident.builder()
                    .id(resident.getId())
                    .name(resident.getName())
                    .dob(resident.getDob())
                    .gender(resident.getGender())
                    .cic(resident.getCic())
                    .status(ResidentEnum.fromString(resident.getStatus()))
                    .apartment(null)
                    .build();
            return this.residentRepository.save(resident1);
        }
    }

    @Transactional
    public Resident updateResident(ResidentUpdateRequest resident) throws Exception {
        Resident oldResident = this.fetchResidentById(resident.getId());
        if (oldResident != null) {
            if (resident.getName() != null) oldResident.setName(resident.getName());
            if (resident.getDob() != null) oldResident.setDob(resident.getDob());
            if (resident.getStatus() != null) {
                oldResident.setStatus(ResidentEnum.fromString(resident.getStatus()));
            }
            if (resident.getGender() != null) {
                oldResident.setGender(resident.getGender());
            }
            if (resident.getCic() != null) {
                oldResident.setCic(resident.getCic());
            }
            if (resident.getAddressNumber() != null) {
                Apartment newApartment = apartmentRepository.findById(resident.getAddressNumber())
                        .orElseThrow(() -> new RuntimeException("Apartment with address number " + resident.getAddressNumber() + " not found"));
                List<Resident> residentList = newApartment.getResidentList();
                residentList.add(oldResident);
                newApartment.setResidentList(residentList);
                apartmentRepository.save(newApartment);
                oldResident.setApartment(newApartment);
            }
        } else {
            throw new Exception("Resident with id = " + resident.getId() + " is not found");
        }
        return this.residentRepository.save(oldResident);
    }

    @Transactional
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
