package com.cuit.blog.security;

import com.cuit.blog.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

// Handles creation and validation of JWT tokens.
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private Key signingKey;

    @PostConstruct
    void init() {
        if (!StringUtils.hasText(jwtProperties.getSecret())) {
            throw new IllegalStateException("JWT secret must not be empty");
        }
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // Generates an access token for the given user.
    public String generateToken(User user) {
        return generateToken(Map.of(
                "userId", user.getId(),
                "role", user.getRole()
        ), user.getUsername());
    }

    private String generateToken(Map<String, Object> extraClaims, String subject) {
        Date issuedAt = new Date();
        Date expirationDate = new Date(issuedAt.getTime() + jwtProperties.getExpiration());
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extracts the username (subject) from a token.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extracts the user identifier from a token.
    public Long extractUserId(String token) {
        Number userId = extractClaim(token, claims -> claims.get("userId", Number.class));
        return userId != null ? userId.longValue() : null;
    }

    // Extracts the role from a token.
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extracts a specific claim from a token.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = parseClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validates a token against the provided user details.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // Returns token validity duration, helpful for clients when describing expiry.
    public long getExpiration() {
        return jwtProperties.getExpiration();
    }

    public String getTokenPrefixWithSpace() {
        return jwtProperties.getTokenPrefixWithSpace();
    }

    public String getHeader() {
        return jwtProperties.getHeader();
    }
}
