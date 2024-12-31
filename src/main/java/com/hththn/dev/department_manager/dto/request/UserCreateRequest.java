package com.hththn.dev.department_manager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    @NotBlank(message = "name can't be blank")
    String name;
    @NotBlank(message = "username can't be blank")
    @Email(message = "username must be a valid email")
    String username;
    @NotBlank(message = "password can't be blank")
    @Size(min = 6, message = "password must have at least 6 characters")
    String password;

}
