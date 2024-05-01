package com.example.springsecuritydemoservice.controller;

import com.example.springsecuritydemoservice.dto.auth.AuthenticationRequest;
import com.example.springsecuritydemoservice.dto.auth.AuthenticationResponse;
import com.example.springsecuritydemoservice.dto.auth.RegisterRequest;

import com.example.springsecuritydemoservice.service.impl.AuthenticationServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;

import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AuthenticationControllerTest {

    public final static String BASE_URL = "/api/v1";
    public static final String AUTH_CONTROLLER_PATH = "/auth";

    @MockBean
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Register user is OK")
    @Test
    void registerIsOk() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("foo")
                .lastName("bar")
                .username("foobar@mail.ru")
                .password("123").build();
        AuthenticationResponse expectedAuthenticationResponse
                = AuthenticationResponse.builder()
                .accessToken("abc123")
                .refreshToken("def456").build();
        when(authenticationService.register(eq(registerRequest))).thenReturn(expectedAuthenticationResponse);

        mockMvc.perform(post(BASE_URL + AUTH_CONTROLLER_PATH + "/register")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(expectedAuthenticationResponse.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(expectedAuthenticationResponse.getRefreshToken()));

        verify(authenticationService).register(eq(registerRequest));
    }

    @DisplayName("Authenticate user is OK")
    @Test
    void authenticateUserIsOk() throws Exception {
        AuthenticationRequest authenticationRequest
                = AuthenticationRequest.builder().username("foobar@mail.ru").password("123").build();
        AuthenticationResponse expectedAuthenticationResponse
                = AuthenticationResponse.builder()
                .accessToken("abc123")
                .refreshToken("def456").build();
        when(authenticationService.authenticate(eq(authenticationRequest)))
                .thenReturn(expectedAuthenticationResponse);

        mockMvc.perform(post(BASE_URL + AUTH_CONTROLLER_PATH + "/authenticate")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(expectedAuthenticationResponse.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(expectedAuthenticationResponse.getRefreshToken()));

        verify(authenticationService).authenticate(eq(authenticationRequest));
    }

    @DisplayName("Authenticate user is fails")
    @Test
    void authenticateUserIsFails() throws Exception {
        AuthenticationRequest authenticationRequest
                = AuthenticationRequest.builder().username("foobar@mail.ru").password("123").build();
        when(authenticationService.authenticate(eq(authenticationRequest)))
                .thenThrow(
                        new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post(BASE_URL + AUTH_CONTROLLER_PATH + "/authenticate")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
                .andExpect(result -> assertEquals("Bad credentials", result.getResolvedException().getMessage()));

        verify(authenticationService).authenticate(eq(authenticationRequest));
    }

    @DisplayName("Refresh token is OK")
    @Test
    void refreshTokenIsOk() throws Exception {
        doNothing().when(authenticationService).refreshToken(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class)
        );

        mockMvc.perform(post(BASE_URL + AUTH_CONTROLLER_PATH + "/refresh-token"))
                .andExpect(status().isOk());

        verify(authenticationService).refreshToken(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class));

    }

    @DisplayName("Refresh token is fails")
    @Test
    void refreshTokenIsFails() throws Exception {
        doThrow(new ExpiredJwtException(
                new DefaultHeader(Map.of("Authorization", "Bearer 123")),
                new DefaultClaims(Map.of()), "JWT expired")
        ).when(authenticationService).refreshToken(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class)
        );

        mockMvc.perform(post(BASE_URL + AUTH_CONTROLLER_PATH + "/refresh-token"))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals("JWT expired", result.getResolvedException().getMessage()));

        verify(authenticationService).refreshToken(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class));

    }

}