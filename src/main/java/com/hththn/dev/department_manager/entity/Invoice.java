package com.hththn.dev.department_manager.entity;

import com.hththn.dev.department_manager.constant.PaymentEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @Id
    Long id;


    @ManyToOne
    @JoinColumn(name = "apartment_id")
    Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "fund_id")
    Fund fund;

    PaymentEnum paymentType;

    Instant submittedDate;
    @PreUpdate
    public void beforeUpdate() {
        this.submittedDate = Instant.now();
    }

}
