package com.wilker.usuario_biblioteca_api.infrastructure.request;

import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.UsuarioRequestDTO;

public class UsuarioRequestDTOFixture {

    public static UsuarioRequestDTO build (
            String nome, String email,
            String senha){

        return new UsuarioRequestDTO(nome, email, senha);
    }

}
