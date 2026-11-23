package com.mastermartini.backend.cursos;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {
  private final CursoService service;

  public CursoController(CursoService service) {
    this.service = service;
  }

  @GetMapping
  public List<CursoDTO> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public CursoDTO consultar(@PathVariable Long id) {
    return service.consultar(id);
  }

  @PostMapping
  public ResponseEntity<CursoDTO> crear(@Valid @RequestBody CursoDTO datos) {
    var creado = service.crear(datos);
    return ResponseEntity.created(URI.create("/api/cursos/" + creado.id())).body(creado);
  }

  @PutMapping("/{id}")
  public CursoDTO actualizar(@PathVariable Long id, @Valid @RequestBody CursoDTO datos) {
    return service.actualizar(id, datos);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
