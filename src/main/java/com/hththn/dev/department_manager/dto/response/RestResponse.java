package com.hththn.dev.department_manager.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// formatted response entity
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;
}
