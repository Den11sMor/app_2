package com.mastermartini.backend.recetas;

import com.mastermartini.backend.shared.NoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecetaService {
  private final RecetaRepository repository;

  public RecetaService(RecetaRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<RecetaDTO> listar() {
    return repository.findAll(org.springframework.data.domain.Sort.by("id")).stream()
        .map(RecetaDTO::desde)
        .toList();
  }

  @Transactional(readOnly = true)
  public RecetaDTO consultar(Long id) {
    return RecetaDTO.desde(buscar(id));
  }

  private Receta buscar(Long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NoEncontradoException("Receta no encontrado"));
  }

  public RecetaDTO crear(RecetaDTO datos) {
    var item = new Receta();
    datos.aplicar(item);
    return RecetaDTO.desde(repository.save(item));
  }

  public RecetaDTO actualizar(Long id, RecetaDTO datos) {
    var item = buscar(id);
    datos.aplicar(item);
    return RecetaDTO.desde(repository.save(item));
  }

  public void eliminar(Long id) {
    repository.delete(buscar(id));
  }
}
