package com.mastermartini.backend.shared;

public class NoEncontradoException extends RuntimeException {
  public NoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
