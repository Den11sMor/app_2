package com.mastermartini.backend.recetas;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {
  private final RecetaService service;

  public RecetaController(RecetaService service) {
    this.service = service;
  }

  @GetMapping
  public List<RecetaDTO> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public RecetaDTO consultar(@PathVariable Long id) {
    return service.consultar(id);
  }

  @PostMapping
  public ResponseEntity<RecetaDTO> crear(@Valid @RequestBody RecetaDTO datos) {
    var creado = service.crear(datos);
    return ResponseEntity.created(URI.create("/api/recetas/" + creado.id())).body(creado);
  }

  @PutMapping("/{id}")
  public RecetaDTO actualizar(@PathVariable Long id, @Valid @RequestBody RecetaDTO datos) {
    return service.actualizar(id, datos);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
