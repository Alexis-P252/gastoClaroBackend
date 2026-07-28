package com.api1.demo.dto.response;

public record AuthResponse(String token, String tokenType) {
    public AuthResponse(String token) {
        this(token, "Bearer");
    }
}
