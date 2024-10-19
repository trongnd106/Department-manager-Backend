package com.hththn.dev.department_manager.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
// formatted response entity
public class ApiResponse<T> {
    int code;
    String message;
    T data;
}
