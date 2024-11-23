package com.hththn.dev.department_manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "utility_bills")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilityBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    double electricity;
    double water;
    double internet;

    String date;

    @ManyToOne()
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
