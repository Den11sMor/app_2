package com.mastermartini.backend.videos;

import com.mastermartini.backend.contenido.ContenidoBase;
import jakarta.persistence.*;

@Entity
@Table(name = "videos")
public class Video extends ContenidoBase {}
