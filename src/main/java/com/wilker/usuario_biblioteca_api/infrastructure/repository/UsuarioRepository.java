package com.wilker.usuario_biblioteca_api.infrastructure.repository;

import com.wilker.usuario_biblioteca_api.infrastructure.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
