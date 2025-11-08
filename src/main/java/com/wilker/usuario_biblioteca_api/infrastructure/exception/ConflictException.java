package com.wilker.usuario_biblioteca_api.infrastructure.exception;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
