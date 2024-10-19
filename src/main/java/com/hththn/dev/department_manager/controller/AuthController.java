package com.hththn.dev.department_manager.controller;

import com.hththn.dev.department_manager.entity.User;
import com.hththn.dev.department_manager.exception.UserInfoException;
import com.hththn.dev.department_manager.service.AuthService;
import com.hththn.dev.department_manager.service.SecurityUtil;
import com.hththn.dev.department_manager.dto.response.ResLoginDTO;
import com.hththn.dev.department_manager.dto.request.UserLoginDTO;
import com.hththn.dev.department_manager.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class  AuthController {
    private final AuthService authService;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@RequestBody UserLoginDTO loginDto) {
        ResLoginDTO res = this.authService.getLogin(loginDto);

        // create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);

        // update refresh token for user
        this.userService.updateUserToken(refresh_token, loginDto.getUsername());

        ResponseCookie resCookies = this.securityUtil.createCookie(refresh_token);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @GetMapping("/account")
    public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
        ResLoginDTO.UserLogin userLogin= this.authService.getUserLogin();
        return ResponseEntity.ok().body(userLogin);
    }

    // When access token expired, frontend calls this endpoint to get a new access token and refresh token
    @GetMapping("/refresh")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token) throws Exception {

        // get refreshed user
        ResLoginDTO res = this.securityUtil.getRefreshedUser(refresh_token);

        // set cookies
        ResponseCookie resCookies = this.securityUtil.createCookie(refresh_token);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() throws Exception {
        ResponseCookie deleteSpringCookie = this.authService.handleLogout();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

}
