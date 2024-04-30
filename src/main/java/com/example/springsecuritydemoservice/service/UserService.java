package com.example.springsecuritydemoservice.service;

import com.example.springsecuritydemoservice.dto.UserDto;
import com.example.springsecuritydemoservice.dto.auth.ChangePasswordRequest;
import com.example.springsecuritydemoservice.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);

    User updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);

    void changePassword(Long id, ChangePasswordRequest request);
}
