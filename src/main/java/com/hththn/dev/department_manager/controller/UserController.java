package com.hththn.dev.department_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.hththn.dev.department_manager.entity.User;
import com.hththn.dev.department_manager.service.UserService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // fetch all users
    @Operation(summary = "Get list of users", description = "abcabc")
    @GetMapping()
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = this.userService.fetchAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    //Create new user
    @PostMapping
    public ResponseEntity<User> createNewUser(@RequestBody User apiUser) {
        String hashPassword = this.passwordEncoder.encode(apiUser.getPassword());
        apiUser.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(apiUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


}
