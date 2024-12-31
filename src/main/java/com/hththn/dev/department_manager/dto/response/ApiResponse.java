package com.hththn.dev.department_manager.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
//https://chatgpt.com/c/671cfa55-d358-800e-8738-5e527bf6f672
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
// formatted response entity
public class ApiResponse<T> {
    int code;
    String message;
    T data;
}
