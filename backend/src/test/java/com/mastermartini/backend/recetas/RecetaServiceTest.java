package com.mastermartini.backend.recetas;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RecetaServiceTest {
  @Autowired RecetaService service;

  @Test
  void normalizaDatosYGeneraIdEnServidor() {
    var creado =
        service.crear(
            new RecetaDTO(
                999L, "  Prueba  ", " Cocina ", " 5 min ", " Descripción ", " Detalle ", null));
    assertNotEquals(999L, creado.id());
    assertEquals("Prueba", creado.titulo());
    assertEquals("", creado.videoUrl());
    assertEquals(creado, service.consultar(creado.id()));
  }
}
