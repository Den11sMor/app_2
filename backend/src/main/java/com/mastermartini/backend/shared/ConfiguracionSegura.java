package com.mastermartini.backend.shared;

import java.util.Set;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracionSegura implements InitializingBean {
  private final String direccion;
  private final String clave;

  public ConfiguracionSegura(
      @Value("${server.address:127.0.0.1}") String direccion,
      @Value("${mastermartini.api-key:}") String clave) {
    this.direccion = direccion;
    this.clave = clave;
  }

  @Override
  public void afterPropertiesSet() {
    boolean local = Set.of("127.0.0.1", "localhost", "::1").contains(direccion);
    if (!local && clave.length() < 24) {
      throw new IllegalStateException(
          "Para exponer el backend configura API_KEY con al menos 24 caracteres y HTTPS en el"
              + " alojamiento.");
    }
  }
}
