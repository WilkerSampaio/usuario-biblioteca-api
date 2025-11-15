package com.wilker.usuario_biblioteca_api.infrastructure.request;

import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.LoginRequestDTO;

public class LoginRequestDTOFixture {

    public static LoginRequestDTO build(String email, String senha){
        return new LoginRequestDTO(email, senha);

    }
}
