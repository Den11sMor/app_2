# Master Martini — aplicación Android

Proyecto académico en Kotlin y Jetpack Compose. Conserva el catálogo y la estructura inicial del equipo: recetas, videos, cursos, favoritos y perfil.

## Ejecutar

1. Abrir esta carpeta en Android Studio y sincronizar Gradle.
2. Usar JDK 17 para Gradle e instalar Android SDK 37.
3. Ejecutar la variante `debug` en un emulador o teléfono con Android 7.0 o superior.
4. Crear una cuenta local, elegir intereses y explorar el catálogo.

No se necesita una API, claves ni un servidor. La aplicación de depuración usa el identificador `com.mastermartini.app.debug`.

## Funciones

- Registro con nombre, correo, teléfono, contraseña y confirmación.
- Errores por campo y control de correo duplicado.
- Login con credenciales y sesión recordada entre aperturas.
- Contraseñas derivadas con PBKDF2, sal aleatoria y comparación del hash.
- Intereses editables desde el perfil.
- Catálogo local de recetas, guías de videos y programas de cursos.
- Búsqueda por título o categoría y detalles según el contenido seleccionado.
- Favoritos persistentes separados por usuario.
- Perfil editable, foto desde cámara o galería y cierre de sesión.
- Temporizador de práctica de 15 minutos en las recetas.
- Pantallas desplazables, flechas de regreso, transiciones y mensajes animados.

## Organización

- `data/local`: entidades, DAO y base de datos Room.
- `data/repository`: acceso a datos, sesión y catálogo de ejemplo.
- `domain/model`: usuario y tipos de contenido. Receta, Video y Curso heredan de Contenido y definen su acción.
- `ui/screens`: pantallas y ViewModels con StateFlow.
- `ui/components`: campos, botones, tarjetas y listado compartido.
- `ui/navigation`: rutas y navegación con el identificador del contenido.
- `utils`: validaciones y manejo de contraseñas.
- `MasterMartiniApplication`: comparte las instancias de los repositorios con inyección manual.

La UI muestra el estado del ViewModel. Los ViewModels validan y llaman al repositorio; Room realiza las consultas fuera del hilo principal.

## Recursos del dispositivo

1. **Cámara:** captura una imagen mediante la app de cámara con TakePicture. Un FileProvider comparte únicamente la carpeta temporal de capturas.
2. **Galería:** el selector de fotos de Android entrega únicamente la imagen elegida. Se copia y reduce a un archivo interno de la app.

No se solicitan permisos generales de almacenamiento ni de cámara: se delegan esas acciones a las aplicaciones del sistema. Si no existe una app compatible o se cancela la selección, el perfil sigue disponible.

## Pruebas

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

Las pruebas unitarias cubren validaciones y hash de contraseñas. Las instrumentadas comprueban Room, favoritos por usuario, persistencia al reabrir la base y el flujo de registro, perfil y sesión. Las pruebas del dispositivo requieren un emulador o teléfono conectado.

Consulta también [la guía de revisión](docs/REVISION.md) y [los pasos para tus commits](docs/COMMITS.md).

## Alcance y pendientes de contenido

- Las cuentas y los datos son locales. No se sincronizan con otros teléfonos.
- Los videos incluyen guías de estudio, no archivos de video ni reproducción.
- Los cursos incluyen programas de demostración, sin inscripción ni certificación.
- El temporizador ofrece información visual en la pantalla de detalle; no genera alarmas ni notificaciones en segundo plano.
- El acceso a cámara y galería debe probarse en el teléfono que se usará para la presentación.
- La colaboración se acredita con los commits y tareas reales de cada integrante.

## Referencias técnicas

- [Room](https://developer.android.com/training/data-storage/room)
- [Selector de fotos](https://developer.android.com/training/data-storage/shared/photo-picker)
- [FileProvider](https://developer.android.com/reference/androidx/core/content/FileProvider)
