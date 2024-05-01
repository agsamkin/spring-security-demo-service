package com.example.springsecuritydemoservice.controller;

import com.example.springsecuritydemoservice.dto.UserDto;
import com.example.springsecuritydemoservice.dto.auth.ChangePasswordRequest;
import com.example.springsecuritydemoservice.model.Role;
import com.example.springsecuritydemoservice.model.User;

import com.example.springsecuritydemoservice.service.impl.UserServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.springsecuritydemoservice.controller.AuthenticationControllerTest.BASE_URL;
import static com.example.springsecuritydemoservice.controller.UserController.CHANGE_PASSWORD;
import static com.example.springsecuritydemoservice.controller.UserController.ID;
import static com.example.springsecuritydemoservice.controller.UserController.USER_CONTROLLER_PATH;
import static com.example.springsecuritydemoservice.util.TestUtil.ADMIN;
import static com.example.springsecuritydemoservice.util.TestUtil.NON_ADMIN;

import static org.hamcrest.Matchers.hasSize;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Get user by id is OK")
    @Test
    void getByUserIdIsOk() throws Exception {
        User expectedUser = User.builder()
                .id(1L)
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userService.getUserById(eq(expectedUser.getId()))).thenReturn(expectedUser);

        mockMvc.perform(
                        get(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId())
                                .with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUser.getId()))
                .andExpect(jsonPath("$.username").value(expectedUser.getUsername()));

        verify(userService, times(2)).getUserById(eq(expectedUser.getId()));
    }

    @DisplayName("Get user by id is fails")
    @Test
    void getUserByIdIsFails() throws Exception {
        User expectedUser = User.builder()
                .id(1L)
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userService.getUserById(eq(expectedUser.getId()))).thenReturn(expectedUser);

        mockMvc.perform(
                        get(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId())
                                .with(user(NON_ADMIN)))
                .andExpect(status().isForbidden());

        verify(userService, times(1)).getUserById(eq(expectedUser.getId()));
    }

    @DisplayName("Get all users is OK")
    @Test
    void getAllUsersIsOk() throws Exception {
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

        when(userService.getAllUsers()).thenReturn(expectedUsers);

        mockMvc.perform(
                        get(BASE_URL + USER_CONTROLLER_PATH).with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));

        verify(userService).getAllUsers();
    }

    @DisplayName("Get all users is fails")
    @Test
    void getAllUsersIsFails() throws Exception {
        mockMvc.perform(
                        get(BASE_URL + USER_CONTROLLER_PATH).with(user(NON_ADMIN)))
                .andExpect(status().isForbidden());

        verifyNoMoreInteractions(userService);
    }

    @DisplayName("Update user is OK")
    @Test
    void updateUserIsOk() throws Exception {
        UserDto fromDto = UserDto.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru").build();
        User expectedUser = User.builder()
                .id(1L)
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userService.getUserById(eq(expectedUser.getId()))).thenReturn(expectedUser);
        when(userService.updateUser(eq(expectedUser.getId()), eq(fromDto))).thenReturn(expectedUser);

        mockMvc.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fromDto))
                        .with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUser.getId()))
                .andExpect(jsonPath("$.username").value(expectedUser.getUsername()));

        verify(userService).getUserById(eq(expectedUser.getId()));
        verify(userService).updateUser(eq(expectedUser.getId()), eq(fromDto));
    }

    @DisplayName("Update user is fails")
    @Test
    void updateUserIsFails() throws Exception {
        UserDto fromDto = UserDto.builder()
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru").build();
        User expectedUser = User.builder()
                .id(1L)
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userService.getUserById(eq(expectedUser.getId()))).thenReturn(expectedUser);

        mockMvc.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fromDto))
                        .with(user(NON_ADMIN)))
                .andExpect(status().isForbidden());

        verify(userService).getUserById(eq(expectedUser.getId()));
        verifyNoMoreInteractions(userService);
    }

    @DisplayName("Delete user is OK")
    @Test
    void deleteUserIsOk() throws Exception {
        User expectedUser = User.builder()
                .id(1L)
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userService.getUserById(eq(expectedUser.getId()))).thenReturn(expectedUser);
        doNothing().when(userService).deleteUser(eq(expectedUser.getId()));

        mockMvc.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId())
                        .with(user(ADMIN)))
                .andExpect(status().isOk());

        verify(userService).getUserById(eq(expectedUser.getId()));
        verify(userService).deleteUser(eq(expectedUser.getId()));
    }

    @DisplayName("Delete user is fails")
    @Test
    void deleteUserIsFails() throws Exception {
        User expectedUser = User.builder()
                .id(1L)
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();
        when(userService.getUserById(eq(expectedUser.getId()))).thenReturn(expectedUser);

        mockMvc.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId())
                        .with(user(NON_ADMIN)))
                .andExpect(status().isForbidden());

        verify(userService).getUserById(eq(expectedUser.getId()));
        verifyNoMoreInteractions(userService);
    }

    @DisplayName("Change password is OK")
    @Test
    void changePasswordIsOk() throws Exception {
        User expectedUser = User.builder()
                .id(1L)
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();

        ChangePasswordRequest changePasswordRequest =
                ChangePasswordRequest.builder()
                        .currentPassword("123")
                        .newPassword("12345")
                        .confirmationPassword("12345").build();

        when(userService.getUserById(eq(expectedUser.getId()))).thenReturn(expectedUser);
        doNothing().when(userService).changePassword(eq(expectedUser.getId()), eq(changePasswordRequest));

        mockMvc.perform(patch(BASE_URL + USER_CONTROLLER_PATH + CHANGE_PASSWORD, expectedUser.getId())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .with(user(ADMIN)))
                .andExpect(status().isOk());

        verify(userService).getUserById(eq(expectedUser.getId()));
        verify(userService).changePassword(eq(expectedUser.getId()), eq(changePasswordRequest));
    }

    @DisplayName("Change password is fails")
    @Test
    void changePasswordIsFails() throws Exception {
        User expectedUser = User.builder()
                .id(1L)
                .firstName("foo")
                .lastName("foo")
                .username("foo@mail.ru")
                .password("123")
                .role(Role.USER).build();

        ChangePasswordRequest changePasswordRequest =
                ChangePasswordRequest.builder()
                        .currentPassword("123")
                        .newPassword("12345")
                        .confirmationPassword("12345").build();

        when(userService.getUserById(eq(expectedUser.getId()))).thenReturn(expectedUser);

        mockMvc.perform(patch(BASE_URL + USER_CONTROLLER_PATH + CHANGE_PASSWORD, expectedUser.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .with(user(NON_ADMIN)))
                .andExpect(status().isForbidden());

        verify(userService).getUserById(eq(expectedUser.getId()));
        verifyNoMoreInteractions(userService);
    }

}