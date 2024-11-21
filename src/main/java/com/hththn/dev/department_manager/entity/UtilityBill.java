package com.hththn.dev.department_manager.entity;

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
    Long id;

    double electricity;
    double water;
    double internet;

    @ManyToOne()
    Apartment apartment;

    Instant createdAt;
    @PrePersist
    public void beforeCreate() {
        this.createdAt = Instant.now();
    }

}
