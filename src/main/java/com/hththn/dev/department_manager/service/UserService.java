package com.hththn.dev.department_manager.service;


import com.hththn.dev.department_manager.dto.request.UserCreateRequest;
import com.hththn.dev.department_manager.dto.response.UserResponse;
import com.hththn.dev.department_manager.entity.User;
import com.hththn.dev.department_manager.exception.UserInfoException;
import com.hththn.dev.department_manager.mapper.UserMapper;
import com.hththn.dev.department_manager.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    //Logic fetch all user
    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public List<UserResponse> fetchAllUserResponse() {
        List<User> users = this.userRepository.findAll();
        return this.userMapper.toUserResponseList(users);
    }

    //fetch user by id
    public User fetchUserById(long id) throws UserInfoException {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new UserInfoException("User with id " + id + " is not found"));
        if (user.getIsActive() == 0) {
            throw new UserInfoException("User with id " + id + " is not active");
        }
        return user;
    }

    //Logic get user by email
    public User getUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    //Logic delete user by id
    public void deleteUser(long id) throws UserInfoException {
        User currentUser = this.fetchUserById(id);
        currentUser.setIsActive(0);
        this.userRepository.save(currentUser);
    }

    //Logic update user
    public User updateUser(User reqUser) throws UserInfoException {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setEmail(reqUser.getEmail());
            currentUser.setName(reqUser.getName());
            String hashPassword = this.passwordEncoder.encode(reqUser.getPassword());
            currentUser.setPassword(hashPassword);
            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    //Check existed email
    public boolean isEmailExist(String email) {
        return this.userRepository.findByEmail(email) != null;
    }

    //Logic create user
    public User createUser(UserCreateRequest userCreateRequest) throws UserInfoException {
        if (isEmailExist(userCreateRequest.getUsername())) {
            User existingUser = getUserByUsername(userCreateRequest.getUsername());
            // If user existed and isActive = 1, throw exception
            if (existingUser.getIsActive() == 1) {
                throw new UserInfoException("User with email " + userCreateRequest.getUsername() + " already exists");
            }
            // If user existed but isActive = 0, set isActive = 1 and return updated user
            existingUser.setIsActive(1);
            return this.userRepository.save(existingUser);
        }

        // If email is not found, create a new user
        User user = new User();
        user.setName(userCreateRequest.getName());
        String hashPassword = this.passwordEncoder.encode(userCreateRequest.getPassword());
        user.setPassword(hashPassword);
        user.setEmail(userCreateRequest.getUsername());
        return this.userRepository.save(user);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.getUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
    public UserResponse UserToUserResponse(User user) {
        return this.userMapper.toUserResponse(user);
    }
}
