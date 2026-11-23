package com.mastermartini.backend.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ConfiguracionSeguraTest {
  @Test
  void permiteDesarrolloLocalYExigeClaveFueraDeLoopback() {
    assertDoesNotThrow(() -> new ConfiguracionSegura("127.0.0.1", "").afterPropertiesSet());
    assertThrows(
        IllegalStateException.class,
        () -> new ConfiguracionSegura("0.0.0.0", "").afterPropertiesSet());
    assertDoesNotThrow(
        () -> new ConfiguracionSegura("0.0.0.0", "a".repeat(32)).afterPropertiesSet());
  }
}
