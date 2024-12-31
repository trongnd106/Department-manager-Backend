package com.hththn.dev.department_manager.dto.request;

import com.hththn.dev.department_manager.constant.GenderEnum;
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
    GenderEnum gender;
    String cic;
    String status;
    Long addressNumber;
}
