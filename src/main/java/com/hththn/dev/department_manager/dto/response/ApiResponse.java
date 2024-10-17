package com.hththn.dev.department_manager.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// formatted response entity
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
}
