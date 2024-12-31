package com.hththn.dev.department_manager.entity;

import com.hththn.dev.department_manager.constant.PaymentEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Entity
@Table(name = "invoice_apartment")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class  InvoiceApartment {
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

    @ElementCollection
    @CollectionTable(name = "invoice_apartment_fees",
            joinColumns = @JoinColumn(name = "invoice_apartment_id"))
    @MapKeyColumn(name = "fee_id")
    @Column(name = "amount")
    Map<Long, Double> feeAmounts; // Key: Fee ID, Value: Amount


}
