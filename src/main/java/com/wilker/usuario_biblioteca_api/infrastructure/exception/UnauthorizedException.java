package com.wilker.usuario_biblioteca_api.infrastructure.exception;

public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException(String message, Throwable cause) {
    super(message);
  }
}
