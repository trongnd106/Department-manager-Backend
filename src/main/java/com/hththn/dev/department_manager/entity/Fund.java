package com.hththn.dev.department_manager.entity;

import com.hththn.dev.department_manager.service.SecurityUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(name = "funds")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Fund {
    @Id
    @Column(nullable = false)
    String fundCode;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String type;
    @Column(nullable = false)
    BigDecimal unitPrice;
    LocalDate endDate;

    Instant createdAt;
    Instant updatedAt;

    String createdBy;
    String updatedBy;

    @PrePersist
    public void beforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("");
        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void beforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("");
        this.updatedAt = Instant.now();
    }
}
