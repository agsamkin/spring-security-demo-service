package com.example.springsecuritydemoservice.service.impl;

import com.example.springsecuritydemoservice.dto.auth.AuthenticationRequest;
import com.example.springsecuritydemoservice.dto.auth.AuthenticationResponse;
import com.example.springsecuritydemoservice.dto.auth.RegisterRequest;

import com.example.springsecuritydemoservice.model.Role;
import com.example.springsecuritydemoservice.model.User;

import com.example.springsecuritydemoservice.repository.UserRepository;
import com.example.springsecuritydemoservice.service.AuthenticationService;
import com.example.springsecuritydemoservice.service.JwtService;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;

@Transactional
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String BEARER_PREFIX = "Bearer";

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken).build();
    }

    @Transactional(readOnly = true)
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found by username: " + request.getUsername()));

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken).build();
    }

    @Transactional(readOnly = true)
    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            return;
        }

        String refreshToken = authHeader.substring(BEARER_PREFIX.length());
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by username: " + username));

        if (jwtService.isTokenValid(refreshToken, user)) {
            String jwtToken = jwtService.generateToken(user);
            AuthenticationResponse authResponse = AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken).build();
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        }
    }

}
