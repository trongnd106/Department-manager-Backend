package com.hththn.dev.department_manager.dto.request;

import com.hththn.dev.department_manager.constant.FeeTypeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceCreateRequest {
    String id;
    String name;
    String description;
}