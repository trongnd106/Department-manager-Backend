package com.hththn.dev.department_manager.service;


import com.hththn.dev.department_manager.entity.User;
import com.hththn.dev.department_manager.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    //Logic create user
    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    //Logic fetch all user
    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    //Logic get user by email
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

}
