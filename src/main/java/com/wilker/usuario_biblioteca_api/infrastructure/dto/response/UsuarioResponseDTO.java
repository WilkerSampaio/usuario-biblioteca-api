package com.wilker.usuario_biblioteca_api.infrastructure.dto.response;

import com.wilker.usuario_biblioteca_api.infrastructure.enums.RoleEnum;

public record UsuarioResponseDTO(

        Long id,
        String nome,
        String email,
        RoleEnum roleEnum
)
{}
