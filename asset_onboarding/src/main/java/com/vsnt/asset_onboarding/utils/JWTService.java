package com.vsnt.asset_onboarding.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    private String secretKey = null;

    public String generateToken(String userId , String assetId, Long validity) {
        Map<String, Object> claims
                = new HashMap<>();
        claims.put("assetId", assetId);
        claims.put("userId", userId);
        return Jwts
                .builder()
                .claims()
                .add(claims)
                .subject(userId)
                .issuer("DCB")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ validity))
                .and()
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey() {
        byte[] decode
                = Decoders.BASE64.decode(getSecretKey());

        return Keys.hmacShaKeyFor(decode);
    }


    public String getSecretKey() {
        return secretKey = "RqxPOuVfHoBA8Uq40MhJvfY6qEHOOWWvg6N9W9vt23s=";
    }

    public String extractUserId(String token) {
        return extractClaims(token, Claims::getSubject);
    }
    public String extractAssetId(String token) {
        return extractClaims(token, (claims -> claims.get("assetId").toString()));
    }
    private <T> T extractClaims(String token, Function<Claims,T> claimResolver) {
        Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token , String userId) {
        final String extracted = extractUserId(token);
        return (userId.equals(extracted) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}