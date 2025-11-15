package com.wilker.usuario_biblioteca_api.infrastructure.mapper;

import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.UsuarioRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.response.UsuarioResponseDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.entity.UsuarioEntity;
import com.wilker.usuario_biblioteca_api.infrastructure.enums.RoleEnum;
import com.wilker.usuario_biblioteca_api.infrastructure.request.UsuarioRequestDTOFixture;
import com.wilker.usuario_biblioteca_api.infrastructure.response.UsuarioResponseDTOFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UsuarioMapperConverterTest {

    UsuarioMapperConverter usuarioMapperConverter;

    UsuarioEntity usuarioEntity;

    UsuarioRequestDTO usuarioRequestDTO;

    UsuarioResponseDTO usuarioResponseDTO;

    List<UsuarioEntity> usuarioEntityList;

    List<UsuarioResponseDTO> usuarioResponseDTOList;

    @BeforeEach
    void setup(){
        usuarioMapperConverter = Mappers.getMapper(UsuarioMapperConverter.class);

        List<RoleEnum> roles = List.of(RoleEnum.USER, RoleEnum.ADMIN);

        usuarioEntity = UsuarioEntity.builder()
                .id(1L)
                .nome("Usuario Teste")
                .email("usuarioteste@gmail.com")
                .senha("usuario123")
                .roles(List.of(roles.get(0)))
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

         usuarioEntityList = List.of(usuarioEntity);
         usuarioResponseDTOList = List.of(usuarioResponseDTO);
    }

    @Test
    void deveConverterParaUsuarioEntityComSucesso(){

        UsuarioEntity entity = usuarioMapperConverter.paraUsuarioEntity(usuarioRequestDTO);

        assertEquals(usuarioEntity.getNome(), entity.getNome());
        assertEquals(usuarioEntity.getSenha(), entity.getSenha());
        assertEquals(usuarioEntity.getEmail(), entity.getEmail());
        assertEquals(usuarioEntity.getRoles(), entity.getRoles());

    }

    @Test
    void deveConverterParaUsuarioResponseComSucesso(){

         UsuarioResponseDTO response = usuarioMapperConverter.paraUsuarioResponseDTO(usuarioEntity);

         assertEquals(usuarioResponseDTO, response);

    }

    @Test
    void deveRetornarParaListaUsuarioResponseComSucesso(){

        List<UsuarioResponseDTO> responseDTOList = usuarioMapperConverter.paraUsuarioResponseDTOList(usuarioEntityList);

        assertEquals(1, responseDTOList.size());
        assertEquals(usuarioResponseDTOList, responseDTOList);

    }

}
