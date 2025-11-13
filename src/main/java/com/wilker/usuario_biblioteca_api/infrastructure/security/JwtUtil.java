package com.wilker.usuario_biblioteca_api.infrastructure.security;

import com.wilker.usuario_biblioteca_api.infrastructure.entity.UsuarioEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtUtil {

    @Value("${chave.secreta}")
    private String secretKey;

    private SecretKey getSecretKey() {
        byte[] key = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    // Metodo principal que gera o token a partir do usuário
    public String generateToken(UsuarioEntity usuario) {
        List<String> roles = usuario.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(usuario.getUsername()) // usa o email como subject
                .claim("nome", usuario.getNome())   // adiciona o nome do usuário como claim opcional
                .claim("roles", roles)              // adiciona as permissões no token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .signWith(getSecretKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaims(token).get("roles", List.class);
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }
}
