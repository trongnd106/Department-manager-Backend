package com.hththn.dev.department_manager.dto.response;

import com.hththn.dev.department_manager.constant.FeeTypeEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class FeeResponse {
    String name;
    FeeTypeEnum feeType;
    double amount;
}
