package com.mastermartini.backend.cursos;

import com.mastermartini.backend.shared.NoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CursoService {
  private final CursoRepository repository;

  public CursoService(CursoRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<CursoDTO> listar() {
    return repository.findAll(org.springframework.data.domain.Sort.by("id")).stream()
        .map(CursoDTO::desde)
        .toList();
  }

  @Transactional(readOnly = true)
  public CursoDTO consultar(Long id) {
    return CursoDTO.desde(buscar(id));
  }

  private Curso buscar(Long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NoEncontradoException("Curso no encontrado"));
  }

  public CursoDTO crear(CursoDTO datos) {
    var item = new Curso();
    datos.aplicar(item);
    return CursoDTO.desde(repository.save(item));
  }

  public CursoDTO actualizar(Long id, CursoDTO datos) {
    var item = buscar(id);
    datos.aplicar(item);
    return CursoDTO.desde(repository.save(item));
  }

  public void eliminar(Long id) {
    repository.delete(buscar(id));
  }
}
