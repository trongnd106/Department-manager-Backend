package com.hththn.dev.department_manager.controller;

import com.hththn.dev.department_manager.dto.request.UserLoginDTO;
import com.hththn.dev.department_manager.dto.request.UserSsoDTO;
import com.hththn.dev.department_manager.dto.response.ApiResponse;
import com.hththn.dev.department_manager.dto.response.ResLoginDTO;
import com.hththn.dev.department_manager.entity.User;
import com.hththn.dev.department_manager.repository.UserRepository;
import com.hththn.dev.department_manager.service.AuthService;
import com.hththn.dev.department_manager.service.SecurityUtil;
import com.nimbusds.oauth2.sdk.token.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OAuth2Controller {
    AuthService authService;
    SecurityUtil securityUtil;
    @GetMapping("/auth/social-login")
    public ResponseEntity<ApiResponse<String>> socialAuth(@RequestParam("login-type") String loginType) {
        loginType = loginType.trim().toLowerCase();
        String url = authService.generateAuthUrl(loginType);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setCode(200);
        apiResponse.setMessage("Success");
        apiResponse.setData(url);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/auth/social/callback")
    public ResponseEntity<ResLoginDTO> callback(
            @RequestParam("code") String code,
            @RequestParam("login_type") String loginType
    ) throws Exception {
        Map<String,Object> userInfo = authService.authenticatedAndFetchFrofile(code,loginType);
        if(userInfo == null){
            return ResponseEntity.badRequest().body(null);
        }
        String accountId = "";
        String name = "";
        String email = "";

        if(loginType.trim().equals("google")){
            accountId =(String) Objects.requireNonNullElse(userInfo.get("sub"),"");
            name =(String) Objects.requireNonNullElse(userInfo.get("name"),"");
            email =(String) Objects.requireNonNullElse(userInfo.get("email"),"");
        } else if(loginType.trim().equals("facebook")){
            // todo: continue...
        }

        UserSsoDTO userSsoDTO = UserSsoDTO.builder()
                .email(email)
                .password("")
                .name(name)
                .build();

        if(loginType.trim().equals("google")){
            userSsoDTO.setGoogleAccountId(accountId);
            userSsoDTO.setFacebookAccountId("");
        } else if(loginType.trim().equals("facebook")){
            // userLoginDTO.setFacebookAccountId(accountID);
            // userLoginDTO.setGoogleAccountId("");
        }

        return this.login(userSsoDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(
            @Valid @RequestBody UserSsoDTO userSsoDTO
    ) throws Exception {
        String token = authService.googleLogin(userSsoDTO);

        ResLoginDTO.UserLogin user = ResLoginDTO.UserLogin.builder()
                .email(userSsoDTO.getEmail())
                .name(userSsoDTO.getName())
                .build();

        ResLoginDTO resLoginDTO = this.securityUtil.getRefreshedUser(token);

        return ResponseEntity.status(HttpStatus.OK).body(resLoginDTO);
    }
}
