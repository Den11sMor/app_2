package com.mastermartini.backend.shared;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ErroresController {
  @ExceptionHandler(NoEncontradoException.class)
  public ResponseEntity<?> noEncontrado(NoEncontradoException e) {
    return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> validacion(MethodArgumentNotValidException e) {
    Map<String, String> errores = new LinkedHashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity.badRequest()
        .body(Map.of("mensaje", "Revisa los campos", "errores", errores));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> jsonInvalido() {
    return ResponseEntity.badRequest().body(Map.of("mensaje", "El cuerpo JSON no es válido"));
  }
}
