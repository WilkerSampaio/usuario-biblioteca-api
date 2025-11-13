package com.wilker.usuario_biblioteca_api.service;

import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.LoginRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.UsuarioRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.response.UsuarioResponseDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.entity.UsuarioEntity;
import com.wilker.usuario_biblioteca_api.infrastructure.enums.RoleEnum;
import com.wilker.usuario_biblioteca_api.infrastructure.exception.ConflictException;
import com.wilker.usuario_biblioteca_api.infrastructure.exception.ResourceNotFoundException;
import com.wilker.usuario_biblioteca_api.infrastructure.mapper.UsuarioMapperConverter;
import com.wilker.usuario_biblioteca_api.infrastructure.mapper.UsuarioMapperUpdate;
import com.wilker.usuario_biblioteca_api.infrastructure.repository.UsuarioRepository;
import com.wilker.usuario_biblioteca_api.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapperConverter usuarioMapperConverter;
    private final JwtUtil jwtUtil;
    private final UsuarioMapperUpdate usuarioMapperUpdate;

    // Registro de usuário
    public UsuarioResponseDTO registraUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        casoEmailExiste(usuarioRequestDTO.email());

        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setNome(usuarioRequestDTO.nome());
        usuarioEntity.setEmail(usuarioRequestDTO.email());
        usuarioEntity.setSenha(passwordEncoder.encode(usuarioRequestDTO.senha()));
        usuarioEntity.setRoles(List.of(RoleEnum.USER)); // Corrigido para lista

        return usuarioMapperConverter.paraUsuarioResponseDTO(usuarioRepository.save(usuarioEntity));
    }

    // Verifica se email já existe
    public boolean verificaSeExisteEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public void casoEmailExiste(String email) {
        if (verificaSeExisteEmail(email)) {
            throw new ConflictException("Email já cadastrado!");
        }
    }

    // Autenticação de usuário
    public String autenticaUsuario(LoginRequestDTO loginRequestDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.senha()));

            // Busca o usuário completo para gerar token com roles
            UsuarioEntity usuarioEntity = usuarioRepository.findByEmail(loginRequestDTO.email())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            return "Bearer " + jwtUtil.generateToken(usuarioEntity);

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new UsernameNotFoundException("Credenciais inválidas. Verifique seu email e senha", e.getCause());
        }
    }

    // Busca usuário pelo email
    public UsuarioResponseDTO buscaUsuarioPeloEmail(String token) {
        String email = jwtUtil.extractUsername(token.substring(7));

        UsuarioEntity usuarioEntity = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado"));
        return usuarioMapperConverter.paraUsuarioResponseDTO(usuarioEntity);
    }

    // Atualiza usuário
    public UsuarioResponseDTO atualizaUsuario(UsuarioRequestDTO usuarioRequestDTO, String token) {
        String email = jwtUtil.extractUsername(token.substring(7));

        UsuarioEntity usuarioEntity = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado"));

        usuarioMapperUpdate.updateUsuario(usuarioRequestDTO, usuarioEntity);

        return usuarioMapperConverter.paraUsuarioResponseDTO(usuarioRepository.save(usuarioEntity));
    }

    // Deleta usuário pelo próprio email
    public void deletaUsuario(String email) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado"));
        usuarioRepository.delete(usuario);
    }

    // Lista todos usuários (ADMIN)
    public List<UsuarioResponseDTO> buscaTodosUsuarios() {
        List<UsuarioEntity> usuarioEntityList = usuarioRepository.findAll();
        if (usuarioEntityList.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum usuário foi encontrado");
        }
        return usuarioMapperConverter.paraUsuarioResponseDTOList(usuarioEntityList);
    }

    // Deleta todos usuários (ADMIN)
    public void deletaTodosUsuarios() {
        List<UsuarioEntity> usuarioEntityList = usuarioRepository.findAll();
        if (usuarioEntityList.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum usuário cadastrado");
        }
        usuarioRepository.deleteAll();
    }
}
