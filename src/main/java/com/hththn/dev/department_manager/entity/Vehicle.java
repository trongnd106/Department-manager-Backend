package com.hththn.dev.department_manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hththn.dev.department_manager.constant.VehicleEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vehicle {
    @Id
    Long id;

    VehicleEnum category;
    @ManyToOne
    @JsonIgnore
    Apartment apartment;

    Instant createdAt;

    @PrePersist
    public void beforeCreate() {
        this.createdAt = Instant.now();
    }

    @Transient
    Long apartmentId;

    @PostLoad
    public void onLoad() {
        this.apartmentId = apartment != null ? apartment.getAddressNumber() : null;
    }

}
