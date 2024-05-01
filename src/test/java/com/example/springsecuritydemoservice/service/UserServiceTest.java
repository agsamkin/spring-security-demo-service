package com.example.springsecuritydemoservice.service;

import com.example.springsecuritydemoservice.dto.UserDto;
import com.example.springsecuritydemoservice.dto.auth.ChangePasswordRequest;
import com.example.springsecuritydemoservice.exception.custom.UserNotFoundException;

import com.example.springsecuritydemoservice.model.Role;
import com.example.springsecuritydemoservice.model.User;

import com.example.springsecuritydemoservice.repository.UserRepository;

import com.example.springsecuritydemoservice.service.impl.UserServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.example.springsecuritydemoservice.util.TestUtil.IGNORING_USER_FIELDS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> argumentUserCaptor;

    @DisplayName("Get all users return all")
    @Test
    void getAllUsersReturnAll() {
        User expectedUser1 = User.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        User expectedUser2 = User.builder()
                .firstName("bar")
                .lastName("bar")
                .username("bar@mail.ru")
                .password("456")
                .role(Role.ADMIN).build();
        List<User> expectedUsers = List.of(expectedUser1, expectedUser2);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertThat(actualUsers).usingRecursiveComparison()
                .ignoringFields(IGNORING_USER_FIELDS).isEqualTo(expectedUsers);
        verify(userRepository).findAll();
    }

    @DisplayName("Get all users return 0")
    @Test
    void getAllUsersReturn0() {
        List<User> expectedUsers = List.of();
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertThat(actualUsers).usingRecursiveComparison()
                .ignoringFields(IGNORING_USER_FIELDS).isEqualTo(expectedUsers);
        verify(userRepository).findAll();
    }

    @DisplayName("Get user by id is OK")
    @Test
    void getUserByIdIsOk() {
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userRepository.findById(eq(expectedUser.getId()))).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUserById(expectedUser.getId());

        assertThat(actualUser).usingRecursiveComparison()
                .ignoringFields(IGNORING_USER_FIELDS).isEqualTo(expectedUser);
        verify(userRepository).findById(eq(expectedUser.getId()));
    }

    @DisplayName("Get user by id is fails")
    @Test
    void getUserByIdIsFails() {
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userRepository.findById(eq(expectedUser.getId()))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(expectedUser.getId()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by id: " + expectedUser.getId());

        verify(userRepository).findById(eq(expectedUser.getId()));
    }

    @DisplayName("Update user is OK")
    @Test
    void updateUserIsOk() {
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userRepository.findById(eq(expectedUser.getId()))).thenReturn(Optional.of(expectedUser));
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        UserDto fromDto = UserDto.builder()
                .firstName("bar")
                .lastName("bar")
                .username("bar@mail.ru").build();
        userService.updateUser(expectedUser.getId(), fromDto);

        verify(userRepository).findById(eq(expectedUser.getId()));
        verify(userRepository).save(argumentUserCaptor.capture());
        User actualUser = argumentUserCaptor.getValue();
        assertThat(actualUser).usingRecursiveComparison()
                .ignoringFields(IGNORING_USER_FIELDS).isEqualTo(expectedUser);
    }

    @DisplayName("Update user is fails")
    @Test
    void updateUserIsFails() {
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userRepository.findById(eq(expectedUser.getId()))).thenReturn(Optional.empty());

        UserDto fromDto = UserDto.builder()
                .firstName("bar")
                .lastName("bar")
                .username("bar@mail.ru").build();
        assertThatThrownBy(() -> userService.updateUser(expectedUser.getId(), fromDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by id: " + expectedUser.getId());

        verify(userRepository).findById(eq(expectedUser.getId()));
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("Delete user is OK")
    @Test
    void deleteUserIsOk() {
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userRepository.findById(eq(expectedUser.getId()))).thenReturn(Optional.of(expectedUser));
        doNothing().when(userRepository).delete(expectedUser);

        userService.deleteUser(expectedUser.getId());

        verify(userRepository).findById(eq(expectedUser.getId()));
        verify(userRepository).delete(argumentUserCaptor.capture());
        User actualUser = argumentUserCaptor.getValue();
        assertThat(actualUser).usingRecursiveComparison()
                .ignoringFields(IGNORING_USER_FIELDS).isEqualTo(expectedUser);
    }

    @DisplayName("Delete user is fails")
    @Test
    void deleteUserIsFails() {
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userRepository.findById(eq(expectedUser.getId()))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(expectedUser.getId()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by id: " + expectedUser.getId());

        verify(userRepository).findById(eq(expectedUser.getId()));
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("Change password is OK")
    @Test
    void changePasswordIsOk() {
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        ChangePasswordRequest changePasswordRequest =
                ChangePasswordRequest.builder()
                        .currentPassword(expectedUser.getPassword())
                        .newPassword("456")
                        .confirmationPassword("456").build();

        when(userRepository.findById(eq(expectedUser.getId()))).thenReturn(Optional.of(expectedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        userService.changePassword(expectedUser.getId(), changePasswordRequest);

        verify(userRepository).findById(eq(expectedUser.getId()));
        verify(userRepository).save(argumentUserCaptor.capture());
        User actualUser = argumentUserCaptor.getValue();
        assertThat(actualUser).usingRecursiveComparison()
                .ignoringFields(IGNORING_USER_FIELDS).isEqualTo(expectedUser);
    }

    @DisplayName("Change password is fails")
    @Test
    void changePasswordIsFails() {
        User expectedUser = User.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        ChangePasswordRequest changePasswordRequest =
                ChangePasswordRequest.builder()
                        .currentPassword(expectedUser.getPassword())
                        .newPassword("456")
                        .confirmationPassword("456").build();

        when(userRepository.findById(eq(expectedUser.getId()))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(expectedUser.getId()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by id: " + expectedUser.getId());
        verify(userRepository).findById(eq(expectedUser.getId()));
        verifyNoMoreInteractions(userRepository);
    }

}