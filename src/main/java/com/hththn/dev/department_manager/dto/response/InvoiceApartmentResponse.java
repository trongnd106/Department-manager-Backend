package com.hththn.dev.department_manager.dto.response;

import com.hththn.dev.department_manager.constant.PaymentEnum;
import com.hththn.dev.department_manager.entity.Fee;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class InvoiceApartmentResponse {
    String id;
    String name;
    String description;
    Instant updatedAt;
    LocalDate createdAt;
    PaymentEnum paymentStatus;
    List<FeeResponse> feeList;
}
