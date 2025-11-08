package com.wilker.usuario_biblioteca_api.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

// Serviço utilitário responsável por todas as operações de JWT: geração, extração e validação de tokens.
@Service
public class JwtUtil {

    // Injeta a chave secreta de dentro do arquivo de propriedades da aplicação (ex: application.properties/yml).
    @Value("${chave.secreta}")
    private String secretKey;

    // Metodo auxiliar para converter a chave secreta Base64 (string) em um objeto SecretKey.
    // Isso é essencial para que o algoritmo HMAC (HMAC-SHA) possa assinar e verificar o token.
    private SecretKey getSecretKey(){
        byte[] key = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    // Gera um novo token JWT para um usuário específico.
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Define o nome de usuário como o principal identificado (Subject) no token.
                .setIssuedAt(new Date()) // Marca o momento exato em que o token foi criado.
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Define a expiração para 1 hora (60 minutos * 60 segundos * 1000 milissegundos).
                .signWith(getSecretKey()) // Assina o token usando a chave secreta para garantir sua autenticidade.
                .compact(); // Constrói o token e o serializa para a representação final (string).
    }

    // Analisa e valida a assinatura do token, retornando todas as claims (payload) contidas nele.
    // Uma falha na validação ou expiração resultará em uma exceção.
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSecretKey()) // Especifica a chave para verificar a assinatura.
                .build()
                .parseClaimsJws(token) // Processa o token JWS (JSON Web Signature).
                .getBody(); // Retorna o corpo do token, que são as Claims.
    }

    // Extrai o nome de usuário (Subject) diretamente das claims do token.
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Verifica se a data de expiração (Expiration - 'exp') do token é anterior ao momento atual.
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Realiza a validação completa do token: verifica se o nome de usuário extraído confere com o esperado
    // e se o token ainda não expirou.
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}