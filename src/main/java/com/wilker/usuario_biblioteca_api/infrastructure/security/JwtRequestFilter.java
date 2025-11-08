package com.wilker.usuario_biblioteca_api.infrastructure.security;


import com.wilker.usuario_biblioteca_api.infrastructure.exception.ResourceNotFoundException;
import com.wilker.usuario_biblioteca_api.infrastructure.exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Define a classe JwtRequestFilter, que garante que o filtro seja executado apenas uma vez por requisição HTTP.
public class JwtRequestFilter extends OncePerRequestFilter {

    // Dependência: Utilitário para criação, validação e extração de informações do JWT.
    private final JwtUtil jwtUtil;
    // Dependência: Serviço padrão do Spring Security para carregar os detalhes do usuário (UserDetails) pelo nome de usuário.
    private final UserDetailsService userDetailsService;

    // Construtor que recebe e inicializa as dependências necessárias para o filtro.
    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Metodo principal do filtro, executado a cada requisição.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

        try {
            // 1. Tenta obter o valor completo do cabeçalho HTTP "Authorization".
            final String authorizationHeader = request.getHeader("Authorization");

            // 2. Verifica se o cabeçalho "Authorization" existe e se começa com o prefixo "Bearer ".
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

                // 3. Extrai o token JWT, removendo o prefixo "Bearer " (que tem 7 caracteres).
                final String token = authorizationHeader.substring(7);
                // 4. Extrai o nome de usuário (subject) do token JWT.
                final String username = jwtUtil.extractUsername(token);

                // 5. Se o nome de usuário for válido E se o usuário ainda não estiver autenticado no contexto de segurança do Spring.
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 6. Carrega os detalhes do usuário (permissões, senha, etc.) pelo nome de usuário.
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 7. Valida o token JWT em relação ao usuário carregado (verifica expiração e integridade).
                    if (jwtUtil.validateToken(token, username)) {

                        // 8. Cria um objeto de autenticação para o Spring Security, incluindo as autoridades (papéis/perfis) do usuário.
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        // 9. Define a autenticação no SecurityContextHolder, indicando que o usuário está autenticado para esta requisição.
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

            // 10. Permite que a requisição siga para o próximo filtro na cadeia (ou para o endpoint de destino).
            chain.doFilter(request, response);

            // Captura exceções específicas de JWT (token expirado ou malformado).
        }catch (ExpiredJwtException | MalformedJwtException e){
            // Lança uma exceção de Não Autorizado (401), indicando problema com o token.
            throw new UnauthorizedException("Erro: token inválido ou expirado.", e.getCause());
            // Captura exceções genéricas de Servlet ou I/O.
        } catch (ServletException | IOException e) {
            // Lança uma exceção de Recurso Não Encontrado (ou similar, dependendo da necessidade), indicando falha na execução do filtro.
            throw new ResourceNotFoundException("Erro ao realizar autenticação/processamento do filtro: " + e);
        }
    }
}