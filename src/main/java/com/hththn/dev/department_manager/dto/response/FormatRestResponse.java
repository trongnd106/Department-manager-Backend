package com.hththn.dev.department_manager.dto.response;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

//Format the content of the response body before it is sent to the client
@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

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

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(status);

        if(body instanceof String){
            return body;
        }
        if (status >= 400) {
            return body;
        } else {
            res.setData(body);
            res.setMessage("CALL API SUCCESS");
        }

        return res;
    }

}

