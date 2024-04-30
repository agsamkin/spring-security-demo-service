package com.example.springsecuritydemoservice.util;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.IOException;

public class TestUtil {
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
