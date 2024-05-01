package com.example.springsecuritydemoservice.util;

import com.example.springsecuritydemoservice.model.Role;
import com.example.springsecuritydemoservice.model.User;

import jakarta.servlet.ServletOutputStream;

import jakarta.servlet.WriteListener;

import java.io.IOException;

public class TestUtil {
    public final static String[] IGNORING_USER_FIELDS =
            {"id", "createdAt", "enabled", "accountNonExpired",
                    "credentialsNonExpired", "authorities", "accountNonLocked"};

    public final static User ADMIN = User.builder()
            .id(100L)
            .firstName("admin")
            .lastName("admin")
            .username("admin@mail.ru")
            .password("admin")
            .role(Role.ADMIN).build();
    public final static User NON_ADMIN = User.builder()
            .id(100L)
            .firstName("nonAdmin")
            .lastName("nonAdmin")
            .username("nonAdmin@mail.ru")
            .password("nonAdmin")
            .role(Role.USER).build();

    public final static ServletOutputStream SERVLET_OUTPUT_STREAM = new ServletOutputStream() {
        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        @Override
        public void write(int b) throws IOException {

        }
    };
}
