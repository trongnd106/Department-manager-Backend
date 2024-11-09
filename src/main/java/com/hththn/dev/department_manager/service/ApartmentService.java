package com.hththn.dev.department_manager.service;

import com.hththn.dev.department_manager.constant.ApartmentEnum;
import com.hththn.dev.department_manager.dto.request.ApartmentCreateRequest;
import com.hththn.dev.department_manager.dto.request.ApartmentUpdateRequest;
import com.hththn.dev.department_manager.dto.response.PaginatedResponse;
import com.hththn.dev.department_manager.entity.Apartment;
import com.hththn.dev.department_manager.entity.Resident;
import com.hththn.dev.department_manager.repository.ApartmentRepository;
import com.hththn.dev.department_manager.repository.ResidentRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NamedStoredProcedureQueries;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApartmentService {
    ApartmentRepository apartmentRepository;
    ResidentRepository residentRepository;
    ResidentService residentService;

    @Transactional
    public Apartment create(ApartmentCreateRequest request) {
        var owner = residentService.fetchResidentById(request.getOwnerId());

        List<Resident> members = residentRepository.findAllById(request.getMemberIds());

        // Handle for case: found members != input member ?
        List<Long> foundMem = members.stream().map(Resident::getId).toList();
        List<Long> notFoundMem = foundMem.stream().filter(id -> !foundMem.contains(id)).toList();

        if(!notFoundMem.isEmpty())
            throw new EntityNotFoundException("Not found members " + notFoundMem);

        Apartment apartment = Apartment.builder()
                .addressNumber(request.getAddressNumber())
                .area(request.getArea())
                .owner(owner)
                .status(ApartmentEnum.fromString(request.getStatus()))
                .residentList(members)
                .build();

        owner.setAddressNumber(String.valueOf(request.getAddressNumber()));
        members.forEach(member -> member.setAddressNumber(String.valueOf(request.getAddressNumber())));

        return apartmentRepository.save(apartment);
    }

    public PaginatedResponse<Apartment> getAll(Specification<Apartment> spec, Pageable pageable){
        Page<Apartment> pageApartment = apartmentRepository.findAll(spec,pageable);
        return PaginatedResponse.<Apartment>builder()
                .pageSize(pageable.getPageSize())
                .curPage(pageable.getPageNumber()+1)
                .totalPages(pageApartment.getTotalPages())
                .totalElements(pageApartment.getNumberOfElements())
                .result(pageApartment.getContent())
                .build();
    }

    public Apartment getDetail(Long id){
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can not find apartment with address: " + id));
    }

    @Transactional
    public Apartment update(Long addressID, ApartmentUpdateRequest request){
        Apartment apartment = apartmentRepository.findById(addressID)
                .orElseThrow(() -> new EntityNotFoundException("Not found apartment " + addressID));

        // update owner + apartment status
        if (request.getOwnerId() != null) {
            Resident newOwner = residentService.fetchResidentById(request.getOwnerId());
            apartment.setOwner(newOwner);
        }
        apartment.setStatus(ApartmentEnum.valueOf(request.getStatus()));

        // get current resident list
        List<Long> currentResidentIds = apartment.getResidentList().stream()
                .map(Resident::getId)
                .toList();

        // Remove resident who not in request list
        apartment.getResidentList().forEach(resident -> {
            if (!request.getResidents().contains(resident.getId())) {
                resident.setAddressNumber(null);   // set their address = null
            }
        });

        // Add resident who not in current list
        List<Resident> residentsToAdd = request.getResidents().stream()
                .filter(residentId -> !currentResidentIds.contains(residentId))
                .map(residentService::fetchResidentById)
                .toList();
        apartment.getResidentList().addAll(residentsToAdd);

        return apartmentRepository.save(apartment);
    }
}