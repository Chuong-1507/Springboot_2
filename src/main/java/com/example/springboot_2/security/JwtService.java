package com.example.springboot_2.security;

import com.example.springboot_2.model.User.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import org.springframework.util.CollectionUtils;
import java.util.Date;
import java.util.StringJoiner;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secretString;

    private SecretKey secretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 15 ; // 15 mins

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("scope",buildScope(user))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> stringJoiner.add(role.name()));

        return stringJoiner.toString();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            System.out.println("DEBUG JWT ERROR: "
                    + ex.getClass().getSimpleName()
                    + " - " + ex.getMessage());
            return false;
        }
    }
}