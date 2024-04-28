package com.example.springsecuritydemoservice.controller;

import com.example.springsecuritydemoservice.dto.UserDto;
import com.example.springsecuritydemoservice.model.User;
import com.example.springsecuritydemoservice.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.springsecuritydemoservice.controller.UserController.USER_CONTROLLER_PATH;

@RequiredArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {

    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getUsername() == authentication.getName()
        """;
    private static final String HAS_ROLE_ADMIN = "hasRole('ADMIN')";
    private static final String ONLY_OWNER_BY_ID_OR_HAS_ROLE_ADMIN = """
            @userRepository.findById(#id).get().getUsername() == authentication.getName() or hasRole('ADMIN')
        """;

    private final UserService userService;

    @PreAuthorize(ONLY_OWNER_BY_ID_OR_HAS_ROLE_ADMIN)
    @GetMapping(ID)
    public User getById(@PathVariable("id") long id) {
        return userService.getUserById(id);
    }

    @PreAuthorize(HAS_ROLE_ADMIN)
    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @PreAuthorize(HAS_ROLE_ADMIN)
    @PostMapping
    public User create(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PreAuthorize(ONLY_OWNER_BY_ID_OR_HAS_ROLE_ADMIN)
    @PutMapping(ID)
    public User update(@PathVariable("id") long id,
                       @RequestBody @Valid UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @PreAuthorize(ONLY_OWNER_BY_ID_OR_HAS_ROLE_ADMIN)
    @DeleteMapping(ID)
    public void delete(@PathVariable("id") long id) {
        userService.deleteUser(id);
    }

}
