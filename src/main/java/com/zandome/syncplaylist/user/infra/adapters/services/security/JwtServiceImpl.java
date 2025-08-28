package com.zandome.syncplaylist.user.infra.adapters.services.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.zandome.syncplaylist.config.JwtConfig;
import com.zandome.syncplaylist.user.domain.ports.services.JwtService;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    public JwtServiceImpl(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    @Override
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuer(jwtConfig.getIssuer())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (JwtException e) {
            log.error("Failed to extract email from token: {}", e.getMessage());
            throw new com.zandome.syncplaylist.shared.domain.exceptions.JwtException("Invalid token", e);
        }
    }

    @Override
    public boolean isTokenValid(String token, String email) {
        try {
            String tokenEmail = extractEmail(token);
            return tokenEmail.equals(email) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new com.zandome.syncplaylist.shared.domain.exceptions.JwtException("Token expired", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new com.zandome.syncplaylist.shared.domain.exceptions.JwtException("Unsupported token", e);
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            throw new com.zandome.syncplaylist.shared.domain.exceptions.JwtException("Malformed token", e);
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new com.zandome.syncplaylist.shared.domain.exceptions.JwtException("Invalid signature", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new com.zandome.syncplaylist.shared.domain.exceptions.JwtException("Empty claims", e);
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (com.zandome.syncplaylist.shared.domain.exceptions.JwtException e) {
            return true;
        }
    }
}