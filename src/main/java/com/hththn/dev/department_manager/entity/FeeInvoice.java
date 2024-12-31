package com.hththn.dev.department_manager.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "fee_invoice")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeeInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fee_id")
    Fee fee;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    Invoice invoice;



}
