package com.example.springsecuritydemoservice.service;

import com.example.springsecuritydemoservice.dto.auth.AuthenticationRequest;
import com.example.springsecuritydemoservice.dto.auth.AuthenticationResponse;

import com.example.springsecuritydemoservice.dto.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
