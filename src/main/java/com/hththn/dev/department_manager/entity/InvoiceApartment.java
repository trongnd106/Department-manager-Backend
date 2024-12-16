package com.hththn.dev.department_manager.entity;

import com.hththn.dev.department_manager.constant.PaymentEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "invoice_apartment")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceApartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    Apartment apartment;

    PaymentEnum paymentStatus;


}
