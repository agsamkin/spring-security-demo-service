package com.example.springsecuritydemoservice.service.impl;

import com.example.springsecuritydemoservice.dto.UserDto;
import com.example.springsecuritydemoservice.exception.UserNotFoundException;
import com.example.springsecuritydemoservice.model.User;
import com.example.springsecuritydemoservice.repository.UserRepository;
import com.example.springsecuritydemoservice.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found by username: " + username));
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + id));
    }

    @Override
    public User createUser(UserDto userDto) {
        User newUser = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword())).build();
        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(u -> {
                    u.setUsername(userDto.getUsername());
                    u.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    return userRepository.save(u);
                })
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + id));
        userRepository.delete(user);
    }

}
