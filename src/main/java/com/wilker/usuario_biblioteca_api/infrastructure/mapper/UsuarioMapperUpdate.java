package com.wilker.usuario_biblioteca_api.infrastructure.mapper;

import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.UsuarioRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioMapperUpdate {

    @Mapping(target = "id", ignore = true)
    void updateUsuario(UsuarioRequestDTO usuarioRequestDTO, @MappingTarget UsuarioEntity usuarioEntity);
}
