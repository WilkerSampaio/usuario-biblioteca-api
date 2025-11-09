package com.wilker.usuario_biblioteca_api.infrastructure.security;


import com.wilker.usuario_biblioteca_api.infrastructure.entity.UsuarioEntity;
import com.wilker.usuario_biblioteca_api.infrastructure.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Serviço do Spring Security responsável por carregar os dados de autenticação do usuário.
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repositório injetado para acessar os dados persistidos de usuário no banco de dados.
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Implementação obrigatória da interface UserDetailsService.
    // Este Metodo é chamado pelo Spring Security durante o processo de autenticação (ex: no login ou na validação do JWT).
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Busca o usuário no banco de dados usando o e-mail fornecido.
        // O e-mail serve como o "nome de usuário" para o Spring Security.
        UsuarioEntity usuarioEntity = usuarioRepository.findByEmail(email)
                // Se o usuário não for encontrado (Optional vazio), lança uma exceção padrão do Spring Security.
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // 2. Converte a entidade de domínio (UsuarioEntity) para o objeto UserDetails do Spring Security.
        return org.springframework.security.core.userdetails.User
                .withUsername(usuarioEntity.getEmail()) // Define o identificador principal (o e-mail).
                .password(usuarioEntity.getSenha()) // Define a senha (que será comparada com a senha criptografada fornecida no login).
                .authorities(usuarioEntity.getAuthorities()) //Define as permissões do usuario
                .build(); // Constrói o objeto final que contém as informações de autenticação (o Authority está vazio neste caso).
    }
}