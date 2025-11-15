package com.wilker.usuario_biblioteca_api.infrastructure.dto.request;

import com.wilker.usuario_biblioteca_api.infrastructure.enums.RoleEnum;
import lombok.Builder;

import java.util.List;

@Builder
public record UsuarioRequestDTO(
         String nome,
         String email,
         String senha,
         List<RoleEnum> roles
) {}
