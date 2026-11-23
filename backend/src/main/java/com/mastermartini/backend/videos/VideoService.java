package com.mastermartini.backend.videos;

import com.mastermartini.backend.shared.NoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VideoService {
  private final VideoRepository repository;

  public VideoService(VideoRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<VideoDTO> listar() {
    return repository.findAll(org.springframework.data.domain.Sort.by("id")).stream()
        .map(VideoDTO::desde)
        .toList();
  }

  @Transactional(readOnly = true)
  public VideoDTO consultar(Long id) {
    return VideoDTO.desde(buscar(id));
  }

  private Video buscar(Long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NoEncontradoException("Video no encontrado"));
  }

  public VideoDTO crear(VideoDTO datos) {
    var item = new Video();
    datos.aplicar(item);
    return VideoDTO.desde(repository.save(item));
  }

  public VideoDTO actualizar(Long id, VideoDTO datos) {
    var item = buscar(id);
    datos.aplicar(item);
    return VideoDTO.desde(repository.save(item));
  }

  public void eliminar(Long id) {
    repository.delete(buscar(id));
  }
}
