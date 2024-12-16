package com.hththn.dev.department_manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hththn.dev.department_manager.constant.ApartmentEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.hththn.dev.department_manager.constant.VehicleEnum;

@Entity
@Table(name = "apartments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Apartment {
    @Id
    Long addressNumber;
    double area;

    @Enumerated(EnumType.STRING)
    ApartmentEnum status;

    Instant createdAt;
    Instant updatedAt;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    List<Resident> residentList;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    List<Vehicle> vehicleList;

    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")  //The name of the foreign key column in the apartments table refers to the id in the residents table.
    Resident owner;
    Long ownerPhone;

    @JsonIgnore  //hide this field
    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)  //cascade: used for auto updating at fees and invoices table
    List<InvoiceApartment> invoiceApartments;

    @Transient
    Integer numberOfMembers;
    @Transient
    @JsonProperty
    Long numberOfMotorbikes;
    @Transient
    @JsonProperty
    Long numberOfCars;

    @PrePersist
    public void beforeCreate() {
        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = Instant.now();
    }
    @PostLoad
    public void onLoad() {
        numberOfMembers = residentList.size();
        vehicleList = Optional.ofNullable(vehicleList).orElse(Collections.emptyList());
        numberOfMotorbikes = vehicleList.stream()
                .filter(vehicle -> vehicle.getCategory() == VehicleEnum.Motorbike)
                .count();
        numberOfCars = vehicleList.stream()
                                  .filter(vehicle -> vehicle.getCategory() == VehicleEnum.Car)
                                  .count();
    }
}
