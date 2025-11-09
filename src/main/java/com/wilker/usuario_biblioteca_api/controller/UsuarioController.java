package com.wilker.usuario_biblioteca_api.controller;

import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.LoginRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.request.UsuarioRequestDTO;
import com.wilker.usuario_biblioteca_api.infrastructure.dto.response.UsuarioResponseDTO;
import com.wilker.usuario_biblioteca_api.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario (@RequestBody UsuarioRequestDTO usuarioRequestDTO){
        return ResponseEntity.ok(usuarioService.registraUsuario(usuarioRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUsuario(@RequestBody LoginRequestDTO loginRequestDTO){
        return ResponseEntity.ok(usuarioService.autenticaUsuario(loginRequestDTO));
    }

    @GetMapping()
    public ResponseEntity<UsuarioResponseDTO> buscaDadosUsuario (@RequestParam ("email") String email ){
        return ResponseEntity.ok(usuarioService.buscaUsuarioPeloEmail(email));
    }

    @PutMapping("/{email}")
    public ResponseEntity<UsuarioResponseDTO> atualizaDadosUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO,
                                                                   @PathVariable String email){
        return ResponseEntity.ok(usuarioService.atualizaUsuario(usuarioRequestDTO, email));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletaDadosUsuario(@PathVariable String email){
        usuarioService.deletaUsuario(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/todos")
    public ResponseEntity<List<UsuarioResponseDTO>> buscaTodosUsuarios(){
        return ResponseEntity.ok(usuarioService.buscaTodosUsuarios());
    }

    @DeleteMapping("/todos")
    public ResponseEntity<Void> deletaTodosUsuarios(){
        usuarioService.deletaTodosUsuarios();

        return ResponseEntity.ok().build();
    }

}
