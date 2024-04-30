package com.example.springsecuritydemoservice.controller;

import com.example.springsecuritydemoservice.dto.UserDto;
import com.example.springsecuritydemoservice.dto.auth.ChangePasswordRequest;
import com.example.springsecuritydemoservice.model.User;
import com.example.springsecuritydemoservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.springsecuritydemoservice.controller.UserController.USER_CONTROLLER_PATH;

@Tag(name = "user-controller", description = "User crud")
@RequiredArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {

    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";
    public static final String CHANGE_PASSWORD = "/{id}/change-password";

    private static final String HAS_ROLE_ADMIN = "hasRole('ADMIN')";
    private static final String ONLY_OWNER_BY_ID_OR_HAS_ROLE_ADMIN = """
            @userService.getUserById(#id).getUsername() == authentication.getName() or hasRole('ADMIN')
        """;

    private final UserService userService;

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found"),
            @ApiResponse(responseCode = "404", description = "User with this id wasn`t found")
    })
    @PreAuthorize(ONLY_OWNER_BY_ID_OR_HAS_ROLE_ADMIN)
    @GetMapping(ID)
    public User getById(@PathVariable("id") long id) {
        return userService.getUserById(id);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200")
    @PreAuthorize(HAS_ROLE_ADMIN)
    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User has been updated"),
            @ApiResponse(responseCode = "404", description = "User with this id wasn`t found")
    })
    @PreAuthorize(ONLY_OWNER_BY_ID_OR_HAS_ROLE_ADMIN)
    @PutMapping(ID)
    public User update(@PathVariable("id") long id,
                       @RequestBody @Valid UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User has been deleted"),
            @ApiResponse(responseCode = "404", description = "User with this id wasn`t found")
    })
    @PreAuthorize(ONLY_OWNER_BY_ID_OR_HAS_ROLE_ADMIN)
    @DeleteMapping(ID)
    public void delete(@PathVariable("id") long id) {
        userService.deleteUser(id);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Change password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password has been changed")
    })
    @PreAuthorize(ONLY_OWNER_BY_ID_OR_HAS_ROLE_ADMIN)
    @PatchMapping(CHANGE_PASSWORD)
    public void changePassword(@PathVariable("id") long id, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
    }

}
