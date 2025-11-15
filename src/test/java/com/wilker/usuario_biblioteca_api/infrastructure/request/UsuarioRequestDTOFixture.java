package com.wilker.usuario_biblioteca_api.infrastructure.request;

import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.UsuarioRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.enums.RoleEnum;

import java.util.List;

public class UsuarioRequestDTOFixture {

    public static UsuarioRequestDTO build (
            String nome, String email,
            String senha,
            List<RoleEnum> roles){

        return new UsuarioRequestDTO(nome, email, senha, roles);
    }

}
