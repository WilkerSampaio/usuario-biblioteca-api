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
    import static org.junit.jupiter.api.Assertions.assertSame;

    @ExtendWith(MockitoExtension.class)
    public class UsuarioUpdateMapperTest {

        UsuarioMapperUpdate usuarioMapperUpdate;

        UsuarioEntity usuarioEntity;

        UsuarioRequestDTO usuarioRequestDTO;

        @BeforeEach
        void setup() {
            usuarioMapperUpdate = Mappers.getMapper(UsuarioMapperUpdate.class);

            List<RoleEnum> roles = new ArrayList<>(List.of(RoleEnum.USER, RoleEnum.ADMIN));

            usuarioEntity = UsuarioEntity.builder()
                    .id(1L)
                    .nome("Nome Antigo")
                    .email("antigo@gmail.com")
                    .senha("antiga123")
                    .roles(new ArrayList<>(List.of(roles.get(1)))) // ADMIN
                    .build();

            usuarioRequestDTO = UsuarioRequestDTOFixture.build(
                    "Wilker Teste",
                    null,
                    null);
        }

        @Test
        void deveAtualizarUsuarioComSucesso(){

            UsuarioEntity entity = usuarioMapperUpdate.updateUsuario(usuarioRequestDTO, usuarioEntity);

            assertSame(usuarioEntity, entity); //verifica se a instancia é a mesma

            assertEquals("Wilker Teste", entity.getNome());
            assertEquals("antigo@gmail.com", entity.getEmail());
            assertEquals("antiga123", entity.getSenha());
            assertEquals(List.of(RoleEnum.ADMIN), entity.getRoles());
            assertEquals(1L, entity.getId());


        }

    }
