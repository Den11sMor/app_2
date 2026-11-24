# Prueba completa de Master Martini en Android

Fecha: 23 de noviembre de 2026. Entorno: emulador Android 17 / API 37,
Spring Boot real en 127.0.0.1:8080 y MySQL local master_martini, puerto 3307.

## Resultado

**15 pruebas instrumentadas aprobadas, cero fallos, cero errores y cero omitidas.**
Se suman a las 57 pruebas JVM Android y 21 backend verificadas anteriormente:
93 casos distintos aprobados en el conjunto de evidencias, no 93 pruebas nuevas de esta sesión.

| Flujo | Comprobación |
|---|---|
| Registro/login remoto | Cuenta creada desde la pantalla; cierre de sesión; rechazo de contraseña incorrecta; nuevo login correcto |
| Cursos | Listar, crear con validación, consultar por ID, actualizar y eliminar desde la interfaz; comprobar los datos con Retrofit real |
| Recetas | Mismas cinco operaciones y validación; recrear la Activity y actualizar para volver a consultar al servidor |
| Videos remotos | CRUD visual y controles de reproducir/pausar con el archivo HTTPS real |
| API externa | TheMealDB real: búsqueda cake, detalle y búsqueda sin resultados |
| Error de conexión | Puerto inaccesible muestra mensaje y permite actualizar sin cerrar la aplicación |
| Temporizador | Inicia, conserva el estado al recrear la pantalla y permite cancelar |
| Foto | Reducción de imagen de 2200 x 1600 a un máximo de 1024 y guardado del perfil |
| Flujo local | Registro, validaciones, intereses, favoritos, perfil, sesión y recreación |
| Room | Favoritos separados por usuario, actualización del perfil y datos conservados al reabrir la base |
| Videos locales | Imagen, audio, duración, conservación de favoritos y controles/pantalla completa |

La consulta posterior a MySQL devolvió **cero registros temporales con título Prueba UI**.
Se conservaron los ejemplos originales. Las cuentas ficticias de pruebas quedan identificadas
por sus correos de prueba; no se usaron cuentas ni fotos personales.

## Comprobaciones adicionales del APK Release

Se instaló el mismo APK entregado al usuario: Master Martini API 2.0.
Firma v2 verificada correctamente con apksigner. SHA-256: bbeb3d76865ab24c7ba489ef04b0a8c8301a2bc40038699c2f648f058a5e8f47.

Con pulsaciones nativas del emulador, fuera de los tests Compose:
- Login correcto contra Spring Boot y reapertura de la aplicación.
- Acceso a Recetas del servidor y recuperación de Torta de chocolate desde MySQL.
- TheMealDB con resultados e imágenes visibles, comprobadas visualmente.
- Botón Cámara: abrió la cámara virtual, tomó una foto, volvió al perfil y mostró Foto actualizada.
- Botón Galería: abrió el selector del sistema, eligió una imagen de ejemplo del proyecto,
  confirmó Done y volvió al perfil con la imagen y Foto actualizada.

## Incidencia del entorno de pruebas

El emulador mostró un aviso **System UI isn't responding** durante la automatización.
El proceso señalado era la interfaz del sistema Android, no Master Martini. Se recuperó con Wait.
Las 15 pruebas Compose finalizaron correctamente; sus acciones semánticas no prueban el rendimiento
de toda la interfaz del sistema. Después se hicieron las comprobaciones nativas del Release descritas arriba.
Las capturas automáticas conservan ese aviso y no se presentan como capturas limpias de la entrega.
Las imágenes que empiezan por release_ fueron tomadas después de recuperar el sistema.

## Alcance y pendientes

No se utilizó ni modificó el teléfono del trabajo. La cámara probada es virtual;
el sensor, permisos administrados y rendimiento de un teléfono físico siguen dependiendo de ese equipo.
No existe un despliegue en un proveedor remoto: estas pruebas usaron el servidor de este computador.
Perfil, intereses y favoritos originales siguen siendo locales; estas pruebas no los convierten en sincronización remota.
No se ejecutaron todas las combinaciones posibles de dispositivos o versiones Android.

Solo se añadieron/corrigieron tests locales. No se cambió el código de producción ni el APK,
y no se crearon commits ni publicaciones nuevas durante esta prueba.

## Repetir

Con Spring Boot y MySQL activos y el emulador autorizado:
`adb -s emulator-5554 reverse tcp:8080 tcp:8080`

`./gradlew.bat :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.backendReal=true`

El argumento backendReal=true habilita los ocho casos con servidor real. Sin él se omiten
intencionalmente; no se deben contar como aprobados cuando se omiten.

## Casos de la ejecución final

- FlujoAppTest.registroFavoritosPerfilYSesionFuncionan: APROBADO
- FlujosRemotosTest.recetaCrudDesdeLaInterfaz: APROBADO
- FlujosRemotosTest.temporizadorSeConservaAlRecrearLaPantalla: APROBADO
- FlujosRemotosTest.registroLoginRemotoYRechazoDeClaveIncorrecta: APROBADO
- FlujosRemotosTest.fotoSeReduceYSeGuardaEnElPerfil: APROBADO
- FlujosRemotosTest.servidorInaccesibleMuestraErrorSinCerrarLaApp: APROBADO
- FlujosRemotosTest.videoCrudYReproduccionRemota: APROBADO
- FlujosRemotosTest.apiExternaMuestraResultadosDetalleYBusquedaVacia: APROBADO
- FlujosRemotosTest.cursoCrudDesdeLaInterfaz: APROBADO
- PersistenciaTest.archivoDeBaseDeDatosConservaDatosAlReabrirse: APROBADO
- PersistenciaTest.perfilEInteresesSeActualizanSinCambiarLaCuenta: APROBADO
- PersistenciaTest.favoritosSonUnicosYSeSeparanPorUsuario: APROBADO
- VideosTest.controlesPermitenReproducirPausarYAmpliar: APROBADO
- VideosTest.ambosVideosTienenImagenSonidoYDuracionReal: APROBADO
- VideosTest.actualizarCatalogoConservaFavoritosDeLaVersionAnterior: APROBADO
