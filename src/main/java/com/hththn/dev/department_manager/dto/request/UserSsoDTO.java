package com.hththn.dev.department_manager.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
@Builder
public class UserSsoDTO {
    String googleAccountId;
    String facebookAccountId;
    String email;
    String name;
    String password;
}
