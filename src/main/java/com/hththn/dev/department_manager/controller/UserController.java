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

    //fetch all users
    @Operation(summary = "Get list of users", description = "abcabc")
    @GetMapping()
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = this.userService.fetchAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    //fetch user by id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id){
        User fetchUser = this.userService.fetchUserById(id);
        // return ResponseEntity.ok(fetchUser);
        return ResponseEntity.status(HttpStatus.OK).body(fetchUser);
    }

    //Create new user
    @PostMapping
    public ResponseEntity<User> createNewUser(@RequestBody User apiUser) {
        String hashPassword = this.passwordEncoder.encode(apiUser.getPassword());
        apiUser.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(apiUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    //Delete user by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok("deleted successfully");
    }

    //Update user
    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User apiUser = this.userService.handleUpdateUser(user);
        return ResponseEntity.ok(apiUser);
    }

}
