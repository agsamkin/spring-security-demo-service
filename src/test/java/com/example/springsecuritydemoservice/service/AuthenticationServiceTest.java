package com.example.springsecuritydemoservice.service;

import com.example.springsecuritydemoservice.dto.auth.AuthenticationRequest;
import com.example.springsecuritydemoservice.dto.auth.AuthenticationResponse;
import com.example.springsecuritydemoservice.dto.auth.RegisterRequest;
import com.example.springsecuritydemoservice.model.Role;
import com.example.springsecuritydemoservice.model.User;
import com.example.springsecuritydemoservice.repository.UserRepository;
import com.example.springsecuritydemoservice.service.impl.AuthenticationServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

import java.util.Optional;

import static com.example.springsecuritydemoservice.util.TestUtil.SERVLET_OUTPUT_STREAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Captor
    private ArgumentCaptor<User> argumentUserCaptor;

    @Captor
    private ArgumentCaptor<UsernamePasswordAuthenticationToken> argumentUsernamePasswordAuthenticationTokenCaptor;

    @Captor
    private ArgumentCaptor<String> argumentUsernameCaptor;

    private final static String[] IGNORING_USER_FIELDS =
            {"id", "createdAt", "enabled", "accountNonExpired",
                    "credentialsNonExpired", "authorities", "accountNonLocked"};

    private static final String BEARER_PREFIX = "Bearer";

    @DisplayName("Register user is OK")
    @Test
    void registerUserIsOk() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("foo")
                .lastName("bar")
                .username("foobar@mail.ru")
                .password("123").build();
        User expectedUser = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER).build();
        AuthenticationResponse expectedAuthenticationResponse = AuthenticationResponse.builder()
                .accessToken("abc123")
                .refreshToken("def456").build();

        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(expectedAuthenticationResponse.getAccessToken());
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(expectedAuthenticationResponse.getRefreshToken());

        AuthenticationResponse actualAuthenticationResponse = authenticationService.register(registerRequest);

        verify(userRepository).save(argumentUserCaptor.capture());
        User actualUser = argumentUserCaptor.getValue();
        assertThat(actualUser)
                .usingRecursiveComparison()
                .ignoringFields(IGNORING_USER_FIELDS)
                .isEqualTo(expectedUser);
        assertThat(actualAuthenticationResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthenticationResponse);
    }

    @DisplayName("Authenticate user is OK")
    @Test
    void authenticateUserIsOk() {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .username("foobar@mail.ru")
                .password("123")
                .build();
        UsernamePasswordAuthenticationToken expectedUsernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("bar")
                .username("foobar@mail.ru")
                .password(passwordEncoder.encode("123"))
                .role(Role.USER).build();
        AuthenticationResponse expectedAuthenticationResponse = AuthenticationResponse.builder()
                .accessToken("abc123")
                .refreshToken("def456").build();

        when(userRepository.findByUsername(eq(authenticationRequest.getUsername()))).thenReturn(Optional.of(expectedUser));
        when(jwtService.generateToken(any(User.class))).thenReturn(expectedAuthenticationResponse.getAccessToken());
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(expectedAuthenticationResponse.getRefreshToken());

        AuthenticationResponse actualAuthenticationResponse = authenticationService.authenticate(authenticationRequest);

        verify(authenticationManager).authenticate(argumentUsernamePasswordAuthenticationTokenCaptor.capture());
        UsernamePasswordAuthenticationToken actualUsernamePasswordAuthenticationToken
                = argumentUsernamePasswordAuthenticationTokenCaptor.getValue();
        assertThat(actualUsernamePasswordAuthenticationToken)
                .usingRecursiveComparison()
                .isEqualTo(expectedUsernamePasswordAuthenticationToken);
        verify(userRepository).findByUsername(argumentUsernameCaptor.capture());
        String actualUsername = argumentUsernameCaptor.getValue();
        assertThat(actualUsername).isEqualTo(authenticationRequest.getUsername());
        assertThat(actualAuthenticationResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthenticationResponse);
    }

    @DisplayName("Authenticate user is fails")
    @Test
    void authenticateUserIsFails() {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .username("foobar@mail.ru")
                .password("123")
                .build();
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("bar")
                .username("foobar@mail.ru")
                .password(passwordEncoder.encode("123"))
                .role(Role.USER).build();

        when(userRepository.findByUsername(eq(expectedUser.getUsername()))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate(authenticationRequest))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found by username: " + authenticationRequest.getUsername());
        verify(userRepository).findByUsername(authenticationRequest.getUsername());
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("Refresh token is OK")
    @Test
    void refreshTokenIsOk() throws IOException {
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("bar")
                .username("foobar@mail.ru")
                .password(passwordEncoder.encode("123"))
                .role(Role.USER).build();

        AuthenticationResponse expectedAuthenticationResponse = AuthenticationResponse.builder()
                .accessToken("abc1234")
                .refreshToken("def456").build();

        when(httpServletRequest.getHeader(eq(HttpHeaders.AUTHORIZATION))).thenReturn(
                BEARER_PREFIX + expectedAuthenticationResponse.getRefreshToken());
        when(jwtService.extractUsername(eq(expectedAuthenticationResponse.getRefreshToken()))).thenReturn(
                expectedUser.getUsername());
        when(userRepository.findByUsername(eq(expectedUser.getUsername()))).thenReturn(Optional.of(expectedUser));
        when(jwtService.isTokenValid(eq(expectedAuthenticationResponse.getRefreshToken()), any(User.class))).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn(expectedAuthenticationResponse.getAccessToken());
        when(httpServletResponse.getOutputStream()).thenReturn(SERVLET_OUTPUT_STREAM);

        authenticationService.refreshToken(httpServletRequest, httpServletResponse);

        verify(userRepository).findByUsername(argumentUsernameCaptor.capture());
        String actualUsername = argumentUsernameCaptor.getValue();
        assertThat(actualUsername).isEqualTo(expectedUser.getUsername());
        verify(jwtService).generateToken(any(User.class));
    }

    @DisplayName("Refresh token is fails")
    @Test
    void refreshTokenIsFails() throws IOException {
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("bar")
                .username("foobar@mail.ru")
                .password(passwordEncoder.encode("123"))
                .role(Role.USER).build();

        AuthenticationResponse expectedAuthenticationResponse = AuthenticationResponse.builder()
                .accessToken("abc1234")
                .refreshToken("def456").build();

        when(httpServletRequest.getHeader(eq(HttpHeaders.AUTHORIZATION))).thenReturn(
                BEARER_PREFIX + expectedAuthenticationResponse.getRefreshToken());
        when(jwtService.extractUsername(eq(expectedAuthenticationResponse.getRefreshToken()))).thenReturn(
                expectedUser.getUsername());
        when(userRepository.findByUsername(eq(expectedUser.getUsername()))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.refreshToken(httpServletRequest, httpServletResponse))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found by username: " + expectedUser.getUsername());
        verify(userRepository).findByUsername(expectedUser.getUsername());
        verifyNoMoreInteractions(userRepository);
    }

}