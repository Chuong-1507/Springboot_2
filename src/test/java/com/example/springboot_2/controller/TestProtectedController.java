package com.example.springboot_2.controller;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@TestComponent
public class TestProtectedController {

    @GetMapping("/api/test/protected")
    public String protectedEndpoint() {
        return "OK";
    }
}