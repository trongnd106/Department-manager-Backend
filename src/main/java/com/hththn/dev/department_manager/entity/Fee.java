package com.hththn.dev.department_manager.entity;

import com.hththn.dev.department_manager.constant.FeeTypeEnum;
import com.hththn.dev.department_manager.service.SecurityUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(name = "fees")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    FeeTypeEnum feeTypeEnum;

    BigDecimal unitPrice;

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
