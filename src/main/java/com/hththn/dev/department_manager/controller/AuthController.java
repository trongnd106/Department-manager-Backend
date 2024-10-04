package com.hththn.dev.department_manager.controller;

import com.hththn.dev.department_manager.config.SecurityUtil;
import com.hththn.dev.department_manager.dto.response.ResLoginDTO;
import com.hththn.dev.department_manager.dto.request.UserLoginDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/login")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody UserLoginDTO loginDto) {
        // Put input including username/password into Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        // authenticate user => need to define loadUserByUsername method
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Put information into securitycontext if user logins successfully. Spring has done it but we can config in here.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //create a token
        String access_token = this.securityUtil.createToken(authentication);
        ResLoginDTO res = new ResLoginDTO();
        res.setAccessToken(access_token);
        return ResponseEntity.ok().body(res);
    }

}
