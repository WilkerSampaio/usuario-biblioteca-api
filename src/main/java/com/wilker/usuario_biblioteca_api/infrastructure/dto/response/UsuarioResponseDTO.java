package com.wilker.usuario_biblioteca_api.infrastructure.dto.response;

import com.wilker.usuario_biblioteca_api.infrastructure.enums.RoleEnum;
import lombok.Builder;

import java.util.List;

@Builder
public record UsuarioResponseDTO(

        Long id,
        String nome,
        String email,
        List<RoleEnum> roles
)
{}
