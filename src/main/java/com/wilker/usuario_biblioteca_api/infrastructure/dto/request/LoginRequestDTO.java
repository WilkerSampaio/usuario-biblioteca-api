package com.wilker.usuario_biblioteca_api.infrastructure.dto.request;

import lombok.Builder;

@Builder
public record LoginRequestDTO(
         String email,
         String senha

) {
}
