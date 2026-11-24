# Master Martini 2.1: navegación y correo

## Cambios

- Inicio, Mis favoritos y Mi perfil comparten una sola barra inferior en catálogos, detalles, gestión del servidor, formularios, API externa y edición de intereses.
- La barra respeta el espacio del contenido y marca la sección activa. No se muestra en login, registro ni en la selección inicial obligatoria de intereses.
- Regresar a Inicio consulta la ruta actual del NavController; no conserva una ruta antigua en el callback.
- El correo admite guion bajo, guion y +. Rechaza espacios internos, direcciones incompletas, puntos consecutivos y etiquetas de dominio inválidas.
- Se conservaron los ocho archivos de pruebas del compañero añadidos en main por 3a5fedb, con MockK. No se sustituyeron nuestros repositorios remotos ni el JWT por el cliente anterior.
- APK Release 2.1, código 4, misma clave y applicationId que 2.0 para actualizar conservando los datos.

## Validación real

| Verificación | Resultado |
|---|---|
| Android JVM, incluye la suite del compañero | 78 pruebas, 0 fallos, 0 errores y 0 omitidas |
| Android instrumentado en emulador | 16 pruebas, 0 fallos y 0 omitidas |
| Cobertura de líneas de lógica Android | 87.16% (713/818); excluye vistas Compose y código generado |
| Lint | 0 errores |
| Release | Compilación correcta y firma v2 verificada |

La suite instrumentada comprueba barra única y visible en catálogos, detalle, formularios, perfil e intereses; ausencia antes de iniciar sesión; cambio de pestañas y cierre de sesión. También repite los CRUD reales de cursos/recetas/videos, el registro y login con un correo con guion bajo, la API externa, reproducción, fotos, temporizador y Room.

El emulador API 37 mostró avisos de System UI del entorno. Los tests Compose comprueban estados y acciones de la app; la revisión visual final debe hacerse también en el teléfono. No se presenta esa incidencia del emulador como un error de la aplicación ni se ocultan esos avisos en las capturas originales.

## Prueba física realizada por Luis

En Samsung A12 se registró la cuenta y se verificaron contra MySQL las operaciones de crear, consultar, editar y eliminar una receta (35), un curso (36) y un video (37). Los registros de prueba se borraron y permanecen los originales. TheMealDB mostró resultados e imágenes en inglés. Estas comprobaciones físicas correspondían al APK 2.0, antes de esta mejora visual 2.1.

## APK y entrega

Archivo: MasterMartini-API-2.1-release.apk. SHA-256: `ae2d78fe6dcd31215ac84ac8e4b55ec44d74af9110b13d61dc88f9aa4ef31f85`.
El APK se generó con Gradle y las herramientas oficiales Android SDK; Android Studio permite ejecutar la misma tarea o usar Build > Generate Signed Bundle/APK. Si la evaluación exige generar desde el IDE, repetir ese paso allí con el JKS existente.

Fuentes Android: rama Luis-modulos-remotos. Backend integrado: rama Luis-modulos-contenidos en backend_app.
MySQL y Spring Boot siguen siendo locales; no se ha creado alojamiento remoto ni un tablero Trello del proyecto. Perfil e intereses/favoritos originales continúan usando Room.
JKS, contraseñas y JWT_SECRET no se publican en los repositorios ni en el ZIP de código.
