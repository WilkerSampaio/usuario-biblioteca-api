package com.wilker.usuario_biblioteca_api.infrastructure.dto.request;

public record UsuarioRequestDTO(
         String nome,
         String email,
         String senha
) {}
