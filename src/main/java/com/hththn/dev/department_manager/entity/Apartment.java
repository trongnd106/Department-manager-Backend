package com.hththn.dev.department_manager.entity;

import com.hththn.dev.department_manager.constant.ApartmentEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "apartments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Apartment {
    @Id
    Long addressNumber;
    double area;
    @Enumerated(EnumType.STRING)
    ApartmentEnum status;
    Instant createdAt;
    Instant updatedAt;

    @OneToMany()
    List<Resident> residentList;

    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")  //The name of the foreign key column in the apartments table refers to the id in the residents table.
    Resident owner;
    @PrePersist
    public void beforeCreate() {
        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
