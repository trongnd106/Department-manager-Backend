package com.hththn.dev.department_manager.dto.response;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

//https://chatgpt.com/c/671cfa55-d358-800e-8738-5e527bf6f672
//Format the content of the response body before it is sent to the client
@ControllerAdvice
public class FormatApiResponse implements ResponseBodyAdvice<Object> {

    //This method determines whether beforeBodyWrite is excuted or not
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    //This method allows you to intervene in the content of the response body
    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        ApiResponse<Object> res = new ApiResponse<Object>();
        res.setCode(status);

        // If body is ApiResponse, do nothing
        if (body instanceof ApiResponse) {
            return body;
        }

        // Check error (status >= 400)
        if (status >= 400) {
            String errorMessage;
            // If body is a Map, maybe there is a system error (404,...)
            if (body instanceof Map) {
                Map<String, Object> errorBody = (Map<String, Object>) body;
                errorMessage = (String) errorBody.getOrDefault("error", "An error has occurred");
                res.setData(errorBody);
            } else if (body instanceof String) {
                // If body is a String, assume it is an error message
                errorMessage = (String) body;
            } else {
                // default
                errorMessage = "An error has occurred";
            }
            res.setMessage(errorMessage);
            res.setData(null);
        } else {
            // Success (status < 400)
            res.setData(body);
            res.setMessage("CALL API SUCCESS");
        }


        return res;
    }

}

