package com.example.springsecuritydemoservice.controller;

import com.example.springsecuritydemoservice.dto.auth.AuthenticationRequest;
import com.example.springsecuritydemoservice.dto.auth.AuthenticationResponse;
import com.example.springsecuritydemoservice.dto.auth.RegisterRequest;

import com.example.springsecuritydemoservice.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.example.springsecuritydemoservice.controller.AuthenticationController.AUTH_CONTROLLER_PATH;

@Tag(name = "authentication-controller", description = "Authentication controller")
@RequiredArgsConstructor
@RestController
@RequestMapping("${base-url}" + AUTH_CONTROLLER_PATH)
public class AuthenticationController {

    public static final String AUTH_CONTROLLER_PATH = "/auth";

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register")
    @ApiResponse(responseCode = "200")
    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody RegisterRequest request) {
        return authenticationService.register(request);
    }

    @Operation(summary = "Authenticate")
    @ApiResponse(responseCode = "200")
    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
        return authenticationService.authenticate(request);
    }

    @Operation(summary = "Refresh token")
    @ApiResponse(responseCode = "200")
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

}
