package com.api1.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public String generateToken(String username) {
        return "token_" + username + "_" + new Date().getTime();
    }

    public boolean validateToken(String token) {
        return token != null && !token.isEmpty();
    }

    public String getUsernameFromToken(String token) {
        return token.split("_")[1];
    }
}
