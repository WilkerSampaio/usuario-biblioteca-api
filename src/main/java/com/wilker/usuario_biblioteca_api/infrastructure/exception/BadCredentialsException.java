package com.wilker.usuario_biblioteca_api.infrastructure.exception;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
