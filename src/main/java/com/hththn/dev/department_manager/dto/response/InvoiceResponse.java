package com.hththn.dev.department_manager.dto.response;

import com.hththn.dev.department_manager.entity.Fee;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class InvoiceResponse {
    int isActive;
    String id;
    String name;
    String description;
    LocalDate lastUpdated;
    List<Fee> feeList;
}


