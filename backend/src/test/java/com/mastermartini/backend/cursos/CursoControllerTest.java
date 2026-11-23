package com.mastermartini.backend.cursos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CursoControllerTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;
  @Autowired CursoRepository repository;
  private final String ruta = "/api/cursos";

  private String datos(String titulo) throws Exception {
    return json.writeValueAsString(
        Map.of(
            "titulo",
            titulo,
            "categoria",
            "Pastelería",
            "duracion",
            "30 minutos",
            "descripcion",
            "Contenido de prueba",
            "detalle",
            "Preparación paso a paso",
            "videoUrl",
            "https://example.com/video.mp4"));
  }

  @BeforeEach
  void limpiar() {
    repository.deleteAll();
  }

  @Test
  void cincoOperacionesCrudPersisten() throws Exception {
    mvc.perform(get(ruta).header("X-API-Key", "clave-de-prueba"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
    var respuesta =
        mvc.perform(
                post(ruta)
                    .header("X-API-Key", "clave-de-prueba")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(datos("Nuevo contenido")))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andReturn();
    long id = json.readTree(respuesta.getResponse().getContentAsString()).get("id").asLong();
    mvc.perform(get(ruta + "/" + id).header("X-API-Key", "clave-de-prueba"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.titulo").value("Nuevo contenido"));
    mvc.perform(get(ruta).header("X-API-Key", "clave-de-prueba"))
        .andExpect(jsonPath("$[0].id").value(id));
    mvc.perform(
            put(ruta + "/" + id)
                .header("X-API-Key", "clave-de-prueba")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datos("Editado")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.titulo").value("Editado"));
    Assertions.assertEquals("Editado", repository.findById(id).orElseThrow().getTitulo());
    mvc.perform(delete(ruta + "/" + id).header("X-API-Key", "clave-de-prueba"))
        .andExpect(status().isNoContent());
    Assertions.assertFalse(repository.existsById(id));
  }

  @Test
  void identificadoresInexistentesDevuelven404() throws Exception {
    mvc.perform(get(ruta + "/999999").header("X-API-Key", "clave-de-prueba"))
        .andExpect(status().isNotFound());
    mvc.perform(
            put(ruta + "/999999")
                .header("X-API-Key", "clave-de-prueba")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datos("Titulo")))
        .andExpect(status().isNotFound());
    mvc.perform(delete(ruta + "/999999").header("X-API-Key", "clave-de-prueba"))
        .andExpect(status().isNotFound());
  }

  @Test
  void validaObligatoriosLongitudYVideoHttps() throws Exception {
    mvc.perform(
            post(ruta)
                .header("X-API-Key", "clave-de-prueba")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datos(" ")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errores.titulo").exists());
    mvc.perform(
            post(ruta)
                .header("X-API-Key", "clave-de-prueba")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datos("a".repeat(161))))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post(ruta)
                .header("X-API-Key", "clave-de-prueba")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    datos("Titulo")
                        .replace("https://example.com/video.mp4", "javascript:alert(1)")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errores.videoUrl").exists());
    Assertions.assertEquals(0, repository.count());
  }

  @Test
  void rechazaJsonYClaveIncorrectos() throws Exception {
    mvc.perform(
            post(ruta)
                .header("X-API-Key", "clave-de-prueba")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{mal"))
        .andExpect(status().isBadRequest());
    mvc.perform(get(ruta)).andExpect(status().isUnauthorized());
    mvc.perform(get(ruta).header("X-API-Key", "incorrecta")).andExpect(status().isUnauthorized());
  }
}
