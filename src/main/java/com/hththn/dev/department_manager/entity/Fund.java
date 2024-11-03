package com.hththn.dev.department_manager.entity;

import com.hththn.dev.department_manager.constant.FundEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "funds")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Fund {
    @Id
    Long id;

    String name;
    @Enumerated(EnumType.STRING)
    FundEnum fund;
    double unitPrice;

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
