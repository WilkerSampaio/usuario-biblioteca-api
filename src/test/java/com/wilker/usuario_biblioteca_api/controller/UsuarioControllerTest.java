package com.wilker.usuario_biblioteca_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.LoginRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.UsuarioRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.response.UsuarioResponseDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.enums.RoleEnum;
import com.wilker.usuario_biblioteca_api.infrastructure.exception.*;
import com.wilker.usuario_biblioteca_api.infrastructure.request.UsuarioRequestDTOFixture;
import com.wilker.usuario_biblioteca_api.infrastructure.response.UsuarioResponseDTOFixture;
import com.wilker.usuario_biblioteca_api.service.UsuarioService;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private String token;
    private String tokenMalformado;
    private UsuarioRequestDTO usuarioRequestDTO;
    private UsuarioRequestDTO usuarioRequestDTOParaAtualizar;
    private UsuarioResponseDTO usuarioResponseDTO;
    private UsuarioResponseDTO usuarioResponseDTOAtualizado;
    private List<UsuarioResponseDTO> usuarioResponseDTOList;
    private LoginRequestDTO loginRequestDTO;
    private String json;

    @BeforeEach
    void setup() throws Exception {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .alwaysDo(print())
                .build();

        token = "Bearer token-teste";
        tokenMalformado = "Bearer abc.def";

        List<RoleEnum> roles = List.of(RoleEnum.USER, RoleEnum.ADMIN);

        usuarioRequestDTO = UsuarioRequestDTOFixture.build(
                "Usuario Teste",
                "usuarioteste@gmail.com",
                "usuario123",
                List.of(roles.get(0))
        );

        usuarioRequestDTOParaAtualizar = UsuarioRequestDTOFixture.build(
                "Wilker Teste",
                null,
                null,
                new ArrayList<>(List.of(roles.get(0)))
        );

        usuarioResponseDTO = UsuarioResponseDTOFixture.build(
                1L,
                "Usuario Teste",
                "usuarioteste@gmail.com",
                List.of(roles.get(0))
        );

        usuarioResponseDTOAtualizado = UsuarioResponseDTOFixture.build(
                1L,
                usuarioRequestDTOParaAtualizar.nome(),
                "usuarioteste@gmail.com",
                List.of(roles.get(0))
        );

        usuarioResponseDTOList = List.of(usuarioResponseDTO);

        loginRequestDTO = LoginRequestDTO.builder()
                .email("usuarioteste@gmail.com")
                .senha("usuario123")
                .build();

        json = objectMapper.writeValueAsString(usuarioRequestDTO);
    }

// --------------------- REGISTRO E LOGIN ---------------------

    @Test
    void deveRegistrarUsuarioComSucesso() throws Exception {
        when(usuarioService.registraUsuario(any())).thenReturn(usuarioResponseDTO);

        mockMvc.perform(post("/usuario/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(usuarioService).registraUsuario(usuarioRequestDTO);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveRetornarConflictAoRegistrarUsuarioComEmailExistente() throws Exception {
        when(usuarioService.registraUsuario(any()))
                .thenThrow(new ConflictException("Email já cadastrado!"));

        mockMvc.perform(post("/usuario/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());

        verify(usuarioService).registraUsuario(usuarioRequestDTO);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveLogarUsuarioComSucesso() throws Exception {
        when(usuarioService.autenticaUsuario(any())).thenReturn(token);

        mockMvc.perform(post("/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(usuarioService).autenticaUsuario(loginRequestDTO);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveRetornarUnauthorizedAoLogarComCredenciaisInvalidas() throws Exception {
        when(usuarioService.autenticaUsuario(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas."));

        mockMvc.perform(post("/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized());

        verify(usuarioService).autenticaUsuario(loginRequestDTO);
        verifyNoMoreInteractions(usuarioService);
    }

// --------------------- USUÁRIO COM TOKEN ---------------------

    @Test
    void deveBuscarUsuarioComSucessoQuandoTokenValido() throws Exception {
        when(usuarioService.buscaUsuarioPeloEmail(token)).thenReturn(usuarioResponseDTO);

        mockMvc.perform(get("/usuario")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        verify(usuarioService).buscaUsuarioPeloEmail(token);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveRetornarNotFoundAoBuscarUsuarioComTokenInvalido() throws Exception {
        when(usuarioService.buscaUsuarioPeloEmail(token))
                .thenThrow(new ResourceNotFoundException("Email não encontrado"));

        mockMvc.perform(get("/usuario")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());

        verify(usuarioService).buscaUsuarioPeloEmail(token);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveRetornarBadRequestQuandoTokenNaoEnviado() throws Exception {
        mockMvc.perform(get("/usuario"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarBadRequestQuandoTokenMalformadoAoBuscarUsuario() throws Exception {
        when(usuarioService.buscaUsuarioPeloEmail(tokenMalformado))
                .thenThrow(new MalformedJwtException("Token malformado"));

        mockMvc.perform(get("/usuario")
                        .header("Authorization", tokenMalformado))
                .andExpect(status().isBadRequest());

        verify(usuarioService).buscaUsuarioPeloEmail(tokenMalformado);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveAtualizarUsuarioComTokenValido() throws Exception {
        when(usuarioService.atualizaUsuario(usuarioRequestDTOParaAtualizar, token))
                .thenReturn(usuarioResponseDTOAtualizado);

        mockMvc.perform(put("/usuario")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTOParaAtualizar)))
                .andExpect(status().isOk());

        verify(usuarioService).atualizaUsuario(usuarioRequestDTOParaAtualizar, token);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveRetornarBadRequestAoAtualizarUsuarioSemToken() throws Exception {
        mockMvc.perform(put("/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTOParaAtualizar)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarBadRequestQuandoTokenMalformadoAoAtualizarUsuario() throws Exception {
        when(usuarioService.atualizaUsuario(usuarioRequestDTOParaAtualizar, tokenMalformado))
                .thenThrow(new MalformedJwtException("Token malformado"));

        mockMvc.perform(put("/usuario")
                        .header("Authorization", tokenMalformado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTOParaAtualizar)))
                .andExpect(status().isBadRequest());

        verify(usuarioService).atualizaUsuario(usuarioRequestDTOParaAtualizar, tokenMalformado);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveDeletarUsuarioComTokenValido() throws Exception {
        doNothing().when(usuarioService).deletaUsuario(token);

        mockMvc.perform(delete("/usuario")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        verify(usuarioService).deletaUsuario(token);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveRetornarBadRequestAoDeletarUsuarioSemToken() throws Exception {
        mockMvc.perform(delete("/usuario"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarBadRequestQuandoTokenMalformadoAoDeletarUsuario() throws Exception {
        doThrow(new MalformedJwtException("Token malformado")).when(usuarioService).deletaUsuario(tokenMalformado);

        mockMvc.perform(delete("/usuario")
                        .header("Authorization", tokenMalformado))
                .andExpect(status().isBadRequest());

        verify(usuarioService).deletaUsuario(tokenMalformado);
        verifyNoMoreInteractions(usuarioService);
    }

// --------------------- TODOS USUÁRIOS ---------------------

    @Test
    void deveBuscarTodosUsuariosComSucesso() throws Exception {
        when(usuarioService.buscaTodosUsuarios()).thenReturn(usuarioResponseDTOList);

        mockMvc.perform(get("/usuario/todos"))
                .andExpect(status().isOk());

        verify(usuarioService).buscaTodosUsuarios();
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveRetornarNotFoundAoBuscarTodosUsuariosQuandoNenhumCadastrado() throws Exception {
        when(usuarioService.buscaTodosUsuarios()).thenThrow(new ResourceNotFoundException("Nenhum usuário cadastrado"));

        mockMvc.perform(get("/usuario/todos"))
                .andExpect(status().isNotFound());

        verify(usuarioService).buscaTodosUsuarios();
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveDeletarTodosUsuariosComSucesso() throws Exception {
        doNothing().when(usuarioService).deletaTodosUsuarios();

        mockMvc.perform(delete("/usuario/todos"))
                .andExpect(status().isOk());

        verify(usuarioService).deletaTodosUsuarios();
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void deveRetornarNotFoundAoDeletarTodosUsuariosQuandoNenhumCadastrado() throws Exception {
        doThrow(new ResourceNotFoundException("Nenhum usuário cadastrado"))
                .when(usuarioService).deletaTodosUsuarios();

        mockMvc.perform(delete("/usuario/todos"))
                .andExpect(status().isNotFound());

        verify(usuarioService).deletaTodosUsuarios();
        verifyNoMoreInteractions(usuarioService);
    }

}
