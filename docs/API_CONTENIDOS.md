# API de Cursos, Recetas y Videos

URL base de desarrollo: `http://127.0.0.1:8080/`. En alojamiento remoto usar HTTPS. Iniciar sesión en `/api/usuarios/login` y enviar `Authorization: Bearer TOKEN` en las operaciones protegidas.

Cada módulo usa estas operaciones:

| Operación | Cursos | Recetas | Videos | Respuesta correcta |
|---|---|---|---|---|
| Listar | GET `/api/cursos` | GET `/api/recetas` | GET `/api/videos` | 200, arreglo JSON |
| Consultar | GET `/api/cursos/{id}` | GET `/api/recetas/{id}` | GET `/api/videos/{id}` | 200, objeto JSON |
| Crear | POST `/api/cursos` | POST `/api/recetas` | POST `/api/videos` | 201, objeto y cabecera Location |
| Actualizar | PUT `/api/cursos/{id}` | PUT `/api/recetas/{id}` | PUT `/api/videos/{id}` | 200, objeto actualizado |
| Eliminar | DELETE `/api/cursos/{id}` | DELETE `/api/recetas/{id}` | DELETE `/api/videos/{id}` | 204, sin cuerpo |

## Solicitud POST y PUT

```json
{
  "titulo": "Torta de chocolate",
  "categoria": "Pastelería",
  "duracion": "45 minutos",
  "descripcion": "Preparación para practicar mezclas básicas.",
  "detalle": "Ingredientes\n250 g de harina...\n\nPreparación\n1. Precalienta el horno...",
  "videoUrl": ""
}
```

Campos obligatorios: título (hasta 160), categoría (100), duración (80), descripción (1000), detalle (16000 caracteres). El detalle contiene ingredientes y pasos para recetas, unidades para cursos o guía para videos. `PUT` reemplaza todos estos campos; no es un PATCH parcial.

Para **videos**, `videoUrl` es obligatorio y debe ser HTTPS, hasta 2000 caracteres. Se usa un enlace directo a un archivo reproducible, por ejemplo MP4 H.264/AAC; una página de YouTube no es un archivo de video. Para cursos y recetas el campo puede quedar vacío.

La respuesta añade `id`, generado en MySQL. Un `id` recibido en el cuerpo no decide el identificador de un nuevo registro; el identificador de PUT lo determina la ruta.

## Errores

- **400**: JSON incorrecto o campos inválidos; la respuesta de validación incluye `mensaje` y `errores` por nombre de campo.
- **401**: JWT ausente, inválido o vencido.
- **404**: el identificador no existe para consultar, editar o eliminar.
- **5xx**: error del servidor; Android conserva el formulario y muestra una opción de reintento.

```json
{"mensaje":"Revisa los campos","errores":{"titulo":"Campo obligatorio"}}
```

Prueba repetible de los cinco endpoints en los tres módulos: `./tools/Probar-Servidor.ps1`. Crea registros temporales y elimina únicamente esos registros. Los ejemplos iniciales se cargan con `./tools/Cargar-Ejemplos.ps1 -Token TOKEN`, que no reemplaza módulos que ya tienen datos.
