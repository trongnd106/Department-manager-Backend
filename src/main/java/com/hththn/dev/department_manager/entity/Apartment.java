package com.hththn.dev.department_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "apartments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long area;
    Integer member;
    String hostName;
    Integer isActive;
    Instant createdAt;
    Instant updatedAt;
    @PrePersist
    public void beforeCreate() {
        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
