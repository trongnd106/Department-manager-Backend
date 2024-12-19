package com.hththn.dev.department_manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hththn.dev.department_manager.constant.FeeTypeEnum;
import com.hththn.dev.department_manager.service.SecurityUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.FetchType.LAZY;

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

    @JsonIgnore  //hide this field
    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL) //cascade: used for auto updating at fees and invoices table
    List<FeeInvoice> feeInvoices;

    LocalDate createdAt;
    LocalDate updatedAt;

    @PrePersist
    public void beforeCreate() {
        this.createdAt = LocalDate.now();
    }
    @PreUpdate
    public void beforeUpdate() { this.updatedAt = LocalDate.now();}
}
