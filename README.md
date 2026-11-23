# Master Martini — Android

Aplicación académica Kotlin/Compose con cursos, recetas, videos, perfil y favoritos. Los tres módulos de contenidos tienen formularios CRUD conectados con Retrofit al backend Spring Boot del equipo. TheMealDB aporta recetas reales de una API externa.

## Repositorios del equipo

- Android: https://github.com/Den11sMor/app_2 — rama individual `Luis-modulos-remotos`.
- Backend: https://github.com/Den11sMor/backend_app — integración individual `Luis-modulos-contenidos`.

El backend provisional que se preparó antes de recibir el repositorio del compañero se integró en `backend_app`. Se mantiene **un solo servidor, una configuración de base y una configuración de red Android**. Los commits anteriores se conservan en el historial.

## Ejecución local

Clonar ambos repositorios como carpetas hermanas `app_2` y `backend_app`. Android usa JDK 17 y SDK 37; el backend conserva Java 21 y Spring Boot 4.2 del compañero. Requiere MySQL 8.4.

Desde la raíz de Android:

```powershell
./tools/Preparar-MySQL-Local.ps1 -MySqlBin 'C:/laragon/bin/mysql/mysql-8.4.3-winx64/bin'
./tools/Iniciar-Backend.ps1
# Esperar Started en work/backend/servidor.log.
./tools/Probar-Servidor.ps1
```

Si las carpetas tienen otros nombres, pasar `-BackendPath 'C:/ruta/backend_app'` a `Iniciar-Backend.ps1`. El script utiliza JDK 21 o superior; también puede usar el JBR de Android Studio si está instalado. Las credenciales y la clave JWT se generan y guardan en `work/`, excluido de Git.

`Probar-Servidor.ps1` registra una cuenta de prueba, inicia sesión, carga ejemplos y comprueba las cinco operaciones CRUD de los tres módulos. La cuenta queda en `work/pruebas/cuenta-demo.json`; no publicar ese archivo.

## Probar Android

1. Abrir el proyecto en Android Studio o instalar el APK firmado **Master Martini API**, versión 2.0.
2. Con el backend activo, registrar una cuenta. El registro remoto inicia sesión automáticamente y obtiene un JWT.
3. Abrir Recetas, Videos o Cursos → **Gestionar contenidos del servidor**. Crear, consultar, editar, actualizar el listado y eliminar con confirmación.
4. Recetas → **Explorar recetas reales · TheMealDB** → buscar `cake`, `chocolate` o `pasta` y abrir una preparación.
5. El catálogo local original sigue disponible para practicar. Desde el login, **Configurar conexión** permite elegir cuentas locales en vez de cuentas del servidor.

Para un Android autorizado conectado por USB, activar Depuración USB, aceptar la conexión y ejecutar `./tools/Conectar-Android.ps1`. La URL local es `http://127.0.0.1:8080/`; el script establece `adb reverse`. En emulador usar `http://10.0.2.2:8080/` desde Configurar conexión. Mantener servidor y cable activos durante la demostración.

La variante Release usa `com.mastermartini.app.integracion`, por lo que conserva instalada la versión anterior. Las cuentas y datos de cada instalación son independientes.

## Arquitectura

**Compose → ViewModel/StateFlow → Repository → Retrofit → Controller → Service → JPA → MySQL**.

- `NetworkModule`: URL compartida y envío del JWT al backend propio; TheMealDB no recibe ese token.
- `CursosViewModel`, `RecetasViewModel`, `VideosViewModel`: reutilizan `ModuloViewModel` con su repositorio.
- `ValidacionesContenido`: reglas comunes y errores por campo antes de enviar el formulario.
- `ModuloScreen`: catálogo, formulario, consulta, edición y confirmación de eliminación.
- Room conserva la sesión y el catálogo/favoritos locales. Los ID locales no se sustituyen por los ID del servidor.
- El backend usa `Contenido` como entidad base y herencia `JOINED` para Curso, Receta y Video; comparten el catálogo que utilizan sus favoritos.

## Base de datos y seguridad

La base local integrada es **master_martini**: `usuarios`, `intereses`, `usuario_intereses`, `contenidos`, `favoritos`, `cursos`, `recetas` y `videos`. El SQL sin datos está en `database/estructura-master-martini.sql` del repositorio backend.

Las contraseñas remotas se guardan con BCrypt. El login entrega un JWT que protege las operaciones del servidor. Android no imprime cuerpos de autenticación ni recibe contraseñas de MySQL. El teléfono se comunica por HTTP con Spring Boot, nunca por SQL directo. HTTPS se exige para hosts remotos; HTTP se limita a loopback y emulador.

Las cuentas locales de práctica usan PBKDF2 con sal. Perfil, foto, intereses seleccionados y favoritos de ese catálogo conservan la persistencia Room del proyecto original. El backend tiene sus propios endpoints de intereses/favoritos, probados por separado; la UI anterior no se presenta como sincronizada remotamente.

## Pruebas y firma

```powershell
./gradlew.bat :app:assembleRelease :app:coberturaModulos :app:lintDebug -PliveApi=true -PliveBackend=true
# En backend_app con JDK 21 o superior:
./mvnw.cmd verify
```

`liveApi` consulta TheMealDB real y `liveBackend` prueba registro/login y CRUD mediante Retrofit contra Spring Boot/MySQL local. Sin esos parámetros, esas pruebas de red se omiten para que la suite cotidiana no dependa de servicios activos.

El backend verifica **80 % de cobertura global de líneas** con JaCoCo. Android mide toda la lógica móvil con `:app:coberturaLogica`: 84,26 % de líneas (685/813), incluyendo ViewModels, repositorios, utilidades, modelos con comportamiento y configuración de red. Se excluyen las vistas Compose y el código generado. El informe acotado de módulos remotos se conserva por separado. Resultados y alcance: [PRUEBAS_ENTREGA.md](docs/PRUEBAS_ENTREGA.md).

La firma Release se toma de `signing.properties`, excluido de Git; usar `signing.properties.example` como plantilla. No publicar JKS ni contraseñas. El APK queda en `app/build/outputs/apk/release/app-release.apk`.

## Documentación y pendientes

- [Endpoints de contenidos](docs/API_CONTENIDOS.md).
- [API externa](docs/API_EXTERNA.md).
- [Conexión y entrega](docs/CONEXION_Y_ENTREGA.md).
- [Guía para la defensa](docs/GUIA_LUIS.md).
- [Integración del equipo](docs/REVISION_COMPANERO.md).
- [Tareas para el tablero](docs/TAREAS_LUIS.csv).

Falta aprovisionar alojamiento HTTPS y MySQL remota: la configuración está preparada, pero GitHub no ejecuta el backend por sí solo. No se creó un tablero Trello sin acceso; el CSV registra las tareas preparadas. Las pruebas manuales de pantalla, cámara y reproducción requieren un Android autorizado; no se instaló nada en el teléfono del trabajo presentado por el usuario.
