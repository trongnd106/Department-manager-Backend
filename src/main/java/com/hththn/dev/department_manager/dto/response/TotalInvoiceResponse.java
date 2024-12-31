package com.hththn.dev.department_manager.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class TotalInvoiceResponse {
    String id;
    String name;
    LocalDate createDate;
    double totalAmount;
    double paidAmount;
    double contributionAmount;
}
