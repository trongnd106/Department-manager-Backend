package com.hththn.dev.department_manager.service;


import com.hththn.dev.department_manager.dto.response.UserResponse;
import com.hththn.dev.department_manager.entity.User;
import com.hththn.dev.department_manager.mapper.UserMapper;
import com.hththn.dev.department_manager.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    //Logic create user
    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    //Logic fetch all user
    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public List<UserResponse> fetchAllUserResponse() {
        List<User> users = this.fetchAllUser();

        return this.userMapper.toUserResponseList(users);
    }

    //fetch user by id
    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    //Logic get user by email
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    //Logic delete user by id
    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    //Logic update user
    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setEmail(reqUser.getEmail());
            currentUser.setName(reqUser.getName());
            currentUser.setPassword(reqUser.getPassword());
            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    //Check existed email
    public boolean isEmailExist(String email) {
        if(this.userRepository.findByEmail(email) != null) { return true; }
        return false;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public UserResponse UserToUserResponse(User user) { return this.userMapper.toUserResponse(user);}

}
