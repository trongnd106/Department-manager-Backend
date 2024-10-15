package com.hththn.dev.department_manager.controller;

import com.hththn.dev.department_manager.dto.response.UserResponse;
import com.hththn.dev.department_manager.exception.UserInfoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.hththn.dev.department_manager.entity.User;
import com.hththn.dev.department_manager.service.UserService;

import java.util.ArrayList;
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
    public ResponseEntity<List<UserResponse>> getAllUser() {
        List<UserResponse> userResponses = this.userService.fetchAllUserResponse();
        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }

    //fetch user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") long id) throws Exception {
        User fetchUser = this.userService.fetchUserById(id);
        UserResponse userResponse = this.userService.UserToUserResponse(fetchUser);
        if(fetchUser == null){
            throw new UserInfoException("Id "+id+" is not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    //Create new user
    @PostMapping("/register")
    public ResponseEntity<User> createNewUser(@RequestBody User apiUser) throws Exception {
        boolean isEmailExist = this.userService.isEmailExist(apiUser.getEmail());
        if (isEmailExist) {
            throw new UserInfoException(
                    "Email " + apiUser.getEmail() + " already existed, please choose another one.");
        }
        String hashPassword = this.passwordEncoder.encode(apiUser.getPassword());
        apiUser.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(apiUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    //Delete user by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws Exception {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new UserInfoException("User with id = " + id + " is not found");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok("deleted successfully");
    }

    //Update user
    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) throws Exception {
        User apiUser = this.userService.handleUpdateUser(user);
        if (apiUser == null) {
            throw new UserInfoException("User with id = " + user.getId() + " is not found");
        }
        return ResponseEntity.ok(apiUser);
    }

}
