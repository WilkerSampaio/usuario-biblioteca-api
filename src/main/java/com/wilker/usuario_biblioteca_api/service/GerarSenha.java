package com.wilker.usuario_biblioteca_api.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class GerarSenha {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String senha = "jesusteama";
        String senhaHash = passwordEncoder.encode(senha);
        System.out.println("Senha hasheada: " + senhaHash);
    }
}
