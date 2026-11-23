package com.mastermartini.backend.recetas;

import com.mastermartini.backend.contenido.ContenidoBase;
import jakarta.persistence.*;

@Entity
@Table(name = "recetas")
public class Receta extends ContenidoBase {}
