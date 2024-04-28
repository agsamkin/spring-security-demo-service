package com.example.springsecuritydemoservice.service;

import com.example.springsecuritydemoservice.dto.UserDto;
import com.example.springsecuritydemoservice.model.User;

import java.util.List;

public interface UserService {
    User getUserByUsername(String username);

    List<User> getAll();
    User getUserById(Long id);

    User createUser(UserDto userDto);
    User updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
}
