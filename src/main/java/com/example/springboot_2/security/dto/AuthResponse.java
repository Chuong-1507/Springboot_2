package com.example.springboot_2.dto.security;

public class AuthResponse {
    private String token;
    public AuthResponse(String token){
        this.token=token;
    }
    public String getToken(){
        return token;
    }
}
