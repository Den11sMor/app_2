package com.mastermartini.backend.contenido;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class ContenidoBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 160, nullable = false)
  private String titulo = "";

  @Column(length = 100, nullable = false)
  private String categoria = "";

  @Column(length = 80, nullable = false)
  private String duracion = "";

  @Column(length = 1000, nullable = false)
  private String descripcion = "";

  @Column(columnDefinition = "TEXT", nullable = false)
  private String detalle = "";

  @Column(length = 2000, nullable = false)
  private String videoUrl = "";

  public Long getId() {
    return id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String valor) {
    titulo = valor;
  }

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String valor) {
    categoria = valor;
  }

  public String getDuracion() {
    return duracion;
  }

  public void setDuracion(String valor) {
    duracion = valor;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String valor) {
    descripcion = valor;
  }

  public String getDetalle() {
    return detalle;
  }

  public void setDetalle(String valor) {
    detalle = valor;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String valor) {
    videoUrl = valor;
  }
}
