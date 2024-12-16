package com.hththn.dev.department_manager.dto.response;

import com.hththn.dev.department_manager.constant.PaymentEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

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
    PaymentEnum paymentStatus;
}
