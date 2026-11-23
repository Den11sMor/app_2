package com.mastermartini.backend.videos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VideoServiceTest {
  @Autowired VideoService service;

  @Test
  void normalizaDatosYGeneraIdEnServidor() {
    var creado =
        service.crear(
            new VideoDTO(
                999L,
                "  Prueba  ",
                " Cocina ",
                " 5 min ",
                " Descripción ",
                " Detalle ",
                "https://example.com/v.mp4"));
    assertNotEquals(999L, creado.id());
    assertEquals("Prueba", creado.titulo());
    assertEquals("https://example.com/v.mp4", creado.videoUrl());
    assertEquals(creado, service.consultar(creado.id()));
  }
}
