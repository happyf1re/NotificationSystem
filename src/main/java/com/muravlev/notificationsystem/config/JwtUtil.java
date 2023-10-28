package com.muravlev.notificationsystem.config;

import com.muravlev.notificationsystem.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String JWT_SECRET = "9fd9fcf89321221ca3595a41d185be56c27d296bf35d44200661b67890cca78c41f518775a95b32ebce4b762493e0770791b326f05f12171ceec4b157253ab10f";
    private final int JWT_EXPIRATION = 3600000; // Токен действителен 1 час

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public Integer getUserIdFromToken(String token) {
        return Integer.parseInt(Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody().getSubject());
    }
}
