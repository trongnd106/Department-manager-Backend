package com.hththn.dev.department_manager.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

//Return access token
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
//@Builder
public class ResLoginDTO {
    String accessToken;
    UserLogin user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserLogin {
        long id;
        String email;
        String name;
    }

}

