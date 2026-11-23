package com.mastermartini.backend.cursos;

import jakarta.validation.constraints.*;

public record CursoDTO(
    Long id,
    @NotBlank(message = "Campo obligatorio") @Size(max = 160) String titulo,
    @NotBlank(message = "Campo obligatorio") @Size(max = 100) String categoria,
    @NotBlank(message = "Campo obligatorio") @Size(max = 80) String duracion,
    @NotBlank(message = "Campo obligatorio") @Size(max = 1000) String descripcion,
    @NotBlank(message = "Campo obligatorio") @Size(max = 16000) String detalle,
    @Size(max = 2000)
        @Pattern(regexp = "^$|https://[^\\s]+", message = "Usa una dirección HTTPS válida")
        String videoUrl) {
  public static CursoDTO desde(Curso item) {
    return new CursoDTO(
        item.getId(),
        item.getTitulo(),
        item.getCategoria(),
        item.getDuracion(),
        item.getDescripcion(),
        item.getDetalle(),
        item.getVideoUrl());
  }

  public void aplicar(Curso item) {
    item.setTitulo(titulo == null ? "" : titulo.trim());
    item.setCategoria(categoria == null ? "" : categoria.trim());
    item.setDuracion(duracion == null ? "" : duracion.trim());
    item.setDescripcion(descripcion == null ? "" : descripcion.trim());
    item.setDetalle(detalle == null ? "" : detalle.trim());
    item.setVideoUrl(videoUrl == null ? "" : videoUrl.trim());
  }
}
