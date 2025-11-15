package com.wilker.usuario_biblioteca_api.infrastructure.response;

import com.wilker.usuario_biblioteca_api.infrastructure.dto.response.UsuarioResponseDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.enums.RoleEnum;

import java.util.List;

public class UsuarioResponseDTOFixture {

    public UsuarioResponseDTO build (Long id,
                                     String nome,
                                     String email,
                                     List<RoleEnum> roles){
        return new UsuarioResponseDTO(id, nome, email, roles);
    }
}
