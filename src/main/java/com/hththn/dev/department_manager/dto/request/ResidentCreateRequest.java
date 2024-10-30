package com.hththn.dev.department_manager.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResidentCreateRequest {
    Long id;
    String name;
    LocalDate dob;
    String status;
    String addressNumber;
}
