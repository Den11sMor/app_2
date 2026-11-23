package com.mastermartini.backend.videos;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
  private final VideoService service;

  public VideoController(VideoService service) {
    this.service = service;
  }

  @GetMapping
  public List<VideoDTO> listar() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public VideoDTO consultar(@PathVariable Long id) {
    return service.consultar(id);
  }

  @PostMapping
  public ResponseEntity<VideoDTO> crear(@Valid @RequestBody VideoDTO datos) {
    var creado = service.crear(datos);
    return ResponseEntity.created(URI.create("/api/videos/" + creado.id())).body(creado);
  }

  @PutMapping("/{id}")
  public VideoDTO actualizar(@PathVariable Long id, @Valid @RequestBody VideoDTO datos) {
    return service.actualizar(id, datos);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
