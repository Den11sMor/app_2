package com.mastermartini.backend.shared;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Clave compartida para el prototipo académico. En producción se debe integrar identidad y roles.
@Component
public class ApiKeyFilter extends OncePerRequestFilter {
  private final String key;

  public ApiKeyFilter(@Value("${mastermartini.api-key:}") String key) {
    this.key = key;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String enviada = request.getHeader("X-API-Key");
    if (request.getRequestURI().startsWith("/api/")
        && !key.isBlank()
        && (enviada == null
            || !MessageDigest.isEqual(
                key.getBytes(StandardCharsets.UTF_8), enviada.getBytes(StandardCharsets.UTF_8)))) {
      response.setStatus(401);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"mensaje\":\"Clave de API incorrecta o ausente\"}");
      return;
    }
    chain.doFilter(request, response);
  }
}
