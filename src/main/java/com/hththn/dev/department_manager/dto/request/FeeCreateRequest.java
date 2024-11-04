package com.hththn.dev.department_manager.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeeCreateRequest {
    String feeCode;
    String name;
    String type;

}
