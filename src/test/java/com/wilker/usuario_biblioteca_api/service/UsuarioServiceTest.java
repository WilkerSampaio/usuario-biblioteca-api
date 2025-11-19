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
import com.wilker.usuario_biblioteca_api.infrastructure.request.UsuarioRequestDTOFixture;
import com.wilker.usuario_biblioteca_api.infrastructure.response.UsuarioResponseDTOFixture;
import com.wilker.usuario_biblioteca_api.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    UsuarioService usuarioService;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    UsuarioMapperConverter usuarioMapperConverter;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    UsuarioMapperUpdate usuarioMapperUpdate;

    LoginRequestDTO loginRequestDTO;
    UsuarioEntity usuarioEntity;
    UsuarioRequestDTO usuarioRequestDTO;
    UsuarioResponseDTO usuarioResponseDTO;
    List<UsuarioEntity> usuarioEntityList;
    List<UsuarioResponseDTO> usuarioResponseDTOList;
    UsuarioResponseDTO usuarioResponseDTOAtualizado;
    UsuarioRequestDTO usuarioRequestDTOParaAtualizar;

    private final String tokenPuro = "token-jwt-gerado";
    private final String tokenCompleto = "Bearer " + tokenPuro;

    @BeforeEach
    void setup(){

        List<RoleEnum> roles = List.of(RoleEnum.USER, RoleEnum.ADMIN);
        usuarioEntity = UsuarioEntity.builder()
                .id(1L)
                .nome("Usuario Teste")
                .email("usuarioteste@gmail.com")
                .senha("senha-hash-teste") // Senha Hasheada
                .roles(roles)
                .build();

        usuarioRequestDTO = UsuarioRequestDTOFixture.build(
                "Usuario Teste",
                "usuarioteste@gmail.com",
                "usuario123",
                List.of(roles.get(0)));


        usuarioResponseDTO = UsuarioResponseDTOFixture.build(
                1L,
                "Usuario Teste",
                "usuarioteste@gmail.com",
                List.of(roles.get(0)));

        loginRequestDTO = LoginRequestDTO.builder()
                .email("usuarioteste@gmail.com")
                .senha("usuario123")
                .build();

        usuarioRequestDTOParaAtualizar = UsuarioRequestDTOFixture.build(
                "Wilker Teste",
                null,
                null,
                new ArrayList<>(List.of(roles.get(0))));

        usuarioResponseDTOAtualizado = UsuarioResponseDTOFixture.build(
                1L,
                "Wilker Teste",
                "usuarioteste@gmail.com",
                List.of(roles.get(0)));

        usuarioEntityList = List.of(usuarioEntity);
        usuarioResponseDTOList = List.of(usuarioResponseDTO);
    }

    @Test
    void deveRegistrarUsuarioComSucesso() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioMapperConverter.paraUsuarioEntity(usuarioRequestDTO)).thenReturn(usuarioEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("senha-hash-teste");
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuarioEntity);
        when(usuarioMapperConverter.paraUsuarioResponseDTO(usuarioEntity)).thenReturn(usuarioResponseDTO);

        UsuarioResponseDTO response = usuarioService.registraUsuario(usuarioRequestDTO);

        assertEquals(usuarioResponseDTO, response);

        verify(usuarioRepository).existsByEmail(usuarioRequestDTO.email());
        verify(usuarioMapperConverter).paraUsuarioEntity(usuarioRequestDTO);
        verify(passwordEncoder).encode(usuarioRequestDTO.senha());
        verify(usuarioRepository).save(any(UsuarioEntity.class));
        verify(usuarioMapperConverter).paraUsuarioResponseDTO(usuarioEntity);

        verifyNoMoreInteractions(usuarioRepository, passwordEncoder, usuarioMapperConverter);
    }
    @Test
    void deveLancarExcecaoCasoCredencialInvalida(){
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Credenciais inválidas. Verifique seu email e senha"));

        BadCredentialsException e = assertThrows(BadCredentialsException.class, ()-> usuarioService.autenticaUsuario(loginRequestDTO));

        assertThat(e.getMessage(), is("Credenciais inválidas. Verifique seu email e senha"));

      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

      verifyNoMoreInteractions(authenticationManager);
      verifyNoInteractions(usuarioRepository, jwtUtil);



    }

    @Test
    void deveLancarExcecaoAoRegistrarEmailExistente(){
        when(usuarioRepository.existsByEmail(usuarioRequestDTO.email())).thenReturn(true);

        ConflictException e = assertThrows(ConflictException.class, () -> usuarioService.registraUsuario(usuarioRequestDTO));

        assertThat(e.getMessage(), is("Email já cadastrado!"));

        verify(usuarioRepository).existsByEmail(usuarioRequestDTO.email());

        verifyNoMoreInteractions(usuarioRepository, passwordEncoder, usuarioMapperConverter);
    }

    @Test
    void deveRetornarTrueCasoEmailExiste() {
        String emailExistente = usuarioRequestDTO.email();

        when(usuarioRepository.existsByEmail(emailExistente)).thenReturn(true);

        boolean existe = usuarioService.verificaSeExisteEmail(emailExistente);

        assertThat(existe, is(true));
        verify(usuarioRepository).existsByEmail(emailExistente);
    }

    @Test
    void deveAutenticarUsuarioComSucesso(){

        //Mockar manualmente por que essa classe não faz parte do service
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(usuarioRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(usuarioEntity));

        when(jwtUtil.generateToken(usuarioEntity)).thenReturn(tokenPuro);

        String token = usuarioService.autenticaUsuario(loginRequestDTO);

        assertEquals(tokenCompleto, token);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.senha()));
        verify(usuarioRepository).findByEmail(loginRequestDTO.email());
        verify(jwtUtil).generateToken(usuarioEntity);

        verifyNoMoreInteractions(authenticationManager, usuarioRepository, jwtUtil);
    }

    @Test
    void deveBuscarUsuarioPeloEmailComSucesso(){
        String email = usuarioResponseDTO.email();

        when(jwtUtil.extractUsername(tokenPuro)).thenReturn(email);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioMapperConverter.paraUsuarioResponseDTO(usuarioEntity)).thenReturn(usuarioResponseDTO);

        UsuarioResponseDTO response = usuarioService.buscaUsuarioPeloEmail(tokenCompleto);

        assertEquals(usuarioResponseDTO, response);

        verify(jwtUtil).extractUsername(tokenPuro);
        verify(usuarioRepository).findByEmail(email);
        verify(usuarioMapperConverter).paraUsuarioResponseDTO(usuarioEntity);

        verifyNoMoreInteractions(jwtUtil, usuarioRepository, usuarioMapperConverter);
    }

    @Test
    void deveLancarExcecaoCasoEmailNaoExiste(){
        String emailInexistente = "naoExiste@gmail.com";

        when(jwtUtil.extractUsername(tokenPuro)).thenReturn(emailInexistente);
        when(usuarioRepository.findByEmail(emailInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, ()-> usuarioService.buscaUsuarioPeloEmail(tokenCompleto));

        assertThat(e.getMessage(), is("Email não encontrado"));

        verify(jwtUtil).extractUsername(tokenPuro);
        verify(usuarioRepository).findByEmail(emailInexistente);
        verifyNoMoreInteractions(jwtUtil, usuarioRepository);
        verifyNoInteractions(usuarioMapperConverter);
    }

    @Test
    void deveAtualizarUsuarioComSucesso(){
        String email = usuarioResponseDTO.email();

        when(jwtUtil.extractUsername(tokenPuro)).thenReturn(email);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioMapperUpdate.updateUsuario(usuarioRequestDTOParaAtualizar, usuarioEntity)).thenReturn(usuarioEntity);
        when(usuarioRepository.save(usuarioEntity)).thenReturn(usuarioEntity);
        when(usuarioMapperConverter.paraUsuarioResponseDTO(usuarioEntity)).thenReturn(usuarioResponseDTOAtualizado);

        UsuarioResponseDTO response = usuarioService.atualizaUsuario(usuarioRequestDTOParaAtualizar, tokenCompleto);

        assertEquals(usuarioResponseDTOAtualizado, response);

        verify(jwtUtil).extractUsername(tokenPuro);
        verify(usuarioRepository).findByEmail(email);
        verify(usuarioMapperUpdate).updateUsuario(usuarioRequestDTOParaAtualizar, usuarioEntity);
        verify(usuarioRepository).save(usuarioEntity);
        verify(usuarioMapperConverter).paraUsuarioResponseDTO(usuarioEntity);

        verifyNoMoreInteractions(jwtUtil, usuarioRepository, usuarioMapperUpdate, usuarioMapperConverter);

    }

    @Test
   void deveDeletarUsuarioPeloEmail(){
        String email = usuarioResponseDTO.email();

        when(jwtUtil.extractUsername(tokenPuro)).thenReturn(email);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEntity));
        doNothing().when(usuarioRepository).delete(usuarioEntity);

        usuarioService.deletaUsuario(tokenCompleto);

        verify(jwtUtil).extractUsername(tokenPuro);
        verify(usuarioRepository).findByEmail(email);
        verify(usuarioRepository).delete(usuarioEntity);

        verifyNoMoreInteractions(jwtUtil, usuarioRepository);
    }

    @Test
    void deveBuscarTodosUsuarioComSuceso(){
        when(usuarioRepository.findAll()).thenReturn(usuarioEntityList);
        when(usuarioMapperConverter.paraUsuarioResponseDTOList(usuarioEntityList)).thenReturn(usuarioResponseDTOList);

        List<UsuarioResponseDTO> responseDTOList = usuarioService.buscaTodosUsuarios();

        assertEquals(usuarioResponseDTOList ,responseDTOList);

        verify(usuarioRepository).findAll();
        verify(usuarioMapperConverter).paraUsuarioResponseDTOList(usuarioEntityList);

        verifyNoMoreInteractions(usuarioRepository, usuarioMapperConverter);

    }

    @Test
    void deveLancarExcecaoCasoNenhumEmailEncontrado(){
        List<UsuarioEntity> listaVaziaEntity = List.of();

        when(usuarioRepository.findAll()).thenReturn(listaVaziaEntity);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, ()-> usuarioService.buscaTodosUsuarios());

        assertThat(e.getMessage(), is("Nenhum usuário foi encontrado"));

        verifyNoMoreInteractions(usuarioRepository);
        verifyNoInteractions(usuarioMapperConverter);
    }

    @Test
    void deveDeletarTodosUsuariosComSucesso(){
        when(usuarioRepository.findAll()).thenReturn(usuarioEntityList);
        doNothing().when(usuarioRepository).deleteAll();

        usuarioService.deletaTodosUsuarios();

        verify(usuarioRepository).findAll();
        verify(usuarioRepository).deleteAll();

        verifyNoMoreInteractions(usuarioRepository);

    }

}
