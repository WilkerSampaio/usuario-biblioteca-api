package com.wilker.usuario_biblioteca_api.infrastructure.dto.request;

import com.wilker.usuario_biblioteca_api.infrastructure.enums.RoleEnum;

import java.util.List;

public record UsuarioRequestDTO(
         String nome,
         String email,
         String senha,
         List<RoleEnum> roles
) {}
