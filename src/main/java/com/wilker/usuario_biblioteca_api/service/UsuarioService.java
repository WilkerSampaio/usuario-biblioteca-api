package com.wilker.usuario_biblioteca_api.service;

import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.LoginRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.UsuarioRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.response.UsuarioResponseDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.entity.UsuarioEntity;
import com.wilker.usuario_biblioteca_api.infrastructure.exception.ConflictException;
import com.wilker.usuario_biblioteca_api.infrastructure.mapper.UsuarioMapperConverter;
import com.wilker.usuario_biblioteca_api.infrastructure.repository.UsuarioRepository;
import com.wilker.usuario_biblioteca_api.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapperConverter usuarioMapperConverter;
    private final JwtUtil jwtUtil;

    public UsuarioResponseDTO registraUsuario(UsuarioRequestDTO usuarioRequestDTO){
        casoEmailExiste(usuarioRequestDTO.email());
        UsuarioEntity usuarioEntity = new UsuarioEntity();

        usuarioEntity.setNome(usuarioRequestDTO.nome());
        usuarioEntity.setEmail(usuarioRequestDTO.email());
        usuarioEntity.setSenha(passwordEncoder.encode(usuarioRequestDTO.senha()));

        return usuarioMapperConverter.paraUsuarioResponseDTO(usuarioRepository.save(usuarioEntity));
    }

    public boolean verificaSeExisteEmail (String email){
        return usuarioRepository.existsByEmail(email);
    }
    public void casoEmailExiste(String email){
        if(verificaSeExisteEmail(email)){
            throw new ConflictException("Email já cadastrado!");
        }
    }

    public String autenticaUsuario(LoginRequestDTO loginRequestDTO){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.senha()));

                return "Bearer " + jwtUtil.generateToken(authentication.getName());
        } catch (BadCredentialsException | UsernameNotFoundException | AuthorizationDeniedException e){

            throw new UsernameNotFoundException("Credenciais inválidas. Verifique seu email e senha", e.getCause());
        }

    }
}
