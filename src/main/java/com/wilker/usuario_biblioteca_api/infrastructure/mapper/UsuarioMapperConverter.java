package com.wilker.usuario_biblioteca_api.infrastructure.mapper;

import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.UsuarioRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.response.UsuarioResponseDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapperConverter {

    @Mapping(target = "id", ignore = true)
    UsuarioEntity paraUsuarioEntity(UsuarioRequestDTO usuarioRequestDTO);

    UsuarioResponseDTO paraUsuarioResponseDTO(UsuarioEntity usuarioEntity);
}
