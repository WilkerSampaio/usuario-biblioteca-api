package com.wilker.usuario_biblioteca_api.infrastructure.dto.request;

import com.wilker.usuario_biblioteca_api.infrastructure.enums.RoleEnum;

public record UsuarioRequestDTO(
         String nome,
         String email,
         String senha,
         RoleEnum roleEnum
) {}
