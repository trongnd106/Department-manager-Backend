package com.hththn.dev.department_manager.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

//Used for creating and updating request
@Data
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class InvoiceRequest {
    @NotNull
    String invoiceId;
    String name;
    @Column(columnDefinition = "MEDIUMTEXT")
    String description;
    List<Long> feeIds;
}