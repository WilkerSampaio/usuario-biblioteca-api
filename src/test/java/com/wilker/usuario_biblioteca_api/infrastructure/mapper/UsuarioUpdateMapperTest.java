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

    import java.util.ArrayList;
    import java.util.List;

    import static org.junit.jupiter.api.Assertions.assertEquals;

    @ExtendWith(MockitoExtension.class)
    public class UsuarioUpdateMapperTest {

        UsuarioMapperUpdate usuarioMapperUpdate;

        UsuarioEntity usuarioEntity;

        UsuarioEntity usuarioEntityAtualizado;

        UsuarioRequestDTO usuarioRequestDTO;

        UsuarioResponseDTO usuarioResponseDTO;

        List<UsuarioEntity> usuarioEntityList;

        List<UsuarioResponseDTO> usuarioResponseDTOList;

        @BeforeEach
        void setup() {
            usuarioMapperUpdate = Mappers.getMapper(UsuarioMapperUpdate.class);

            List<RoleEnum> roles = new ArrayList<>(List.of(RoleEnum.USER, RoleEnum.ADMIN));

            usuarioEntity = UsuarioEntity.builder()
                    .id(1L)
                    .nome("Usuario Teste")
                    .email("usuarioteste@gmail.com")
                    .senha("usuario123")
                    .roles(new ArrayList<>(List.of(roles.get(0))))
                    .build();

            usuarioRequestDTO = UsuarioRequestDTOFixture.build(
                    "Wilker Teste",
                    null,
                    null,
                    new ArrayList<>(List.of(roles.get(0))));

            usuarioEntityAtualizado = UsuarioEntity.builder()
                    .id(1L)
                    .nome("Wilker Teste")
                    .email("usuarioteste@gmail.com")
                    .senha("usuario123")
                    .roles(new ArrayList<>(List.of(roles.get(0))))
                    .build();
        }

        @Test
        void deveAtualizarUsuarioComSucesso(){

            UsuarioEntity entity = usuarioMapperUpdate.updateUsuario(usuarioRequestDTO, usuarioEntity);

            assertEquals(usuarioEntityAtualizado, entity);

        }

    }
