# Guía rápida para explicar y demostrar tu parte

## Qué implementa esta entrega

“Mi parte corresponde a cursos, recetas y videos. Agregué formularios para crear, consultar, editar y eliminar contenidos. Android manda las solicitudes con Retrofit al backend Spring Boot; los controladores reciben los DTOs, los servicios aplican las operaciones y los repositorios JPA guardan los registros en MySQL. La pantalla observa el estado del ViewModel para mostrar carga, datos y errores.”

No memorices una explicación que no entiendas. Abre los archivos de abajo y sigue una operación completa: es la mejor forma de responder preguntas del profesor.

## Archivos para abrir

1. `ui/screens/RecetasScreen.kt`: entra al módulo compartido indicando su tipo.
2. `ui/screens/ModuloScreen.kt`: muestra el catálogo local o la gestión del servidor. El formulario reutiliza los campos comunes, muestra errores y pide confirmar antes de eliminar.
3. `ui/viewmodels/RecetasViewModel.kt`: recibe el repositorio y reutiliza `ModuloViewModel` para el tipo Receta.
4. `ui/viewmodels/ModuloViewModel.kt`: `guardar()` valida los datos y decide entre POST y PUT según exista un registro seleccionado. `consultar()` usa GET por ID; `eliminar()` llama DELETE. El estado se publica como `StateFlow` de solo lectura.
5. `utils/ValidacionesContenido.kt`: reglas de campos requeridos, longitudes y URL HTTPS del video. El ViewModel valida antes de enviar; el servidor vuelve a validar aunque un cliente intente saltarse Android.
6. `data/remote/RecetaApi.kt`: las cinco rutas HTTP, anotadas con `@GET`, `@POST`, `@PUT` y `@DELETE`.
7. `data/repository/RecetaRepository.kt` y `ContenidoRemotoRepository.kt`: acceso a la API, interpretación de errores por campo, 401, 404 y problemas de conexión.
8. `data/remote/NetworkModule.kt`: configuración única del backend y de TheMealDB; sus instancias usan hosts distintos. La clave del backend no se envía a TheMealDB.
9. `backend_app/.../recetas/RecetaController.java`: recibe JSON validado y devuelve códigos HTTP. POST retorna 201 y DELETE retorna 204.
10. `RecetaService.java`: consulta el repositorio, lanza 404 si no existe el registro y transforma entidad/DTO. Las operaciones usan transacciones.
11. `RecetaRepository.java`: extiende `JpaRepository`; Spring Data implementa las operaciones de persistencia.
12. `Receta.java` y `entity/Contenido.java`: entidad concreta y campos almacenados con herencia JOINED. `detalle` usa `TEXT` porque los ingredientes y las preparaciones pueden ser extensos.
13. `backend_app/src/main/resources/application.properties`: URL, usuario y contraseña se obtienen de variables de entorno; las contraseñas reales no están en GitHub.

Cursos y videos siguen la misma estructura. La diferencia visible del formulario de videos es que pide el enlace HTTPS al archivo reproducible.

## Demostración sugerida

1. Encender MySQL y el backend. Abrir el APK y explicar la diferencia entre el catálogo local y los contenidos del servidor.
2. Recetas → Gestionar contenidos del servidor → Crear Receta. Pulsar Guardar sin datos para mostrar las validaciones.
3. Completar el formulario y guardar. Mostrar el nuevo registro en el listado y volver a consultarlo.
4. Editar su título, guardar y actualizar el listado. Cerrar y abrir la app para comprobar que sigue en el servidor.
5. Eliminar el registro de prueba, confirmar y mostrar que desapareció.
6. Repetir una creación breve en Cursos y abrir un video real desde Videos. No utilizar una página de YouTube como URL de MP4.
7. Recetas → Explorar recetas reales → buscar `cake` y abrir una preparación. Explicar que esos datos llegan de TheMealDB, no de la base propia.
8. Mostrar las pruebas y los commits de tu rama. Diferenciar cobertura del backend, cobertura de la lógica nueva de Android y pruebas manuales pendientes.

## Preguntas probables

**¿Para qué sirve el ViewModel?** Mantiene el estado y la lógica de la pantalla, llama al repositorio y sobrevive a la recreación de la Activity. La UI observa su StateFlow y se recompone cuando cambia.

**¿Por qué hay un MutableStateFlow privado y un StateFlow público?** El ViewModel puede cambiar el estado; la pantalla solo lo observa. Evita que el composable modifique libremente el estado interno.

**¿Dónde se valida?** En `ValidacionesContenido`, invocado desde `guardar()`, y en los DTOs del backend con `@NotBlank`, `@Size` y validación de la URL. El backend responde 400 con errores por campo.

**¿Cómo agregarías otro formulario?** Definir los campos y reglas, DTO, interfaz de API y repositorio; agregar el estado y sus acciones al ViewModel correspondiente; crear la pantalla y una ruta si se necesita; finalmente probar validación, éxito y fallos. Reutilizar el formulario común solo si el nuevo contenido tiene los mismos campos.

**¿Qué arquitectura usan?** Android aplica MVVM con repositorios e inyección manual. El backend separa controlador, servicio y repositorio. La UI no realiza SQL ni solicitudes Retrofit directamente.

**¿Por qué DTO y Entity no son lo mismo?** El DTO define lo que entra y sale por HTTP y sus validaciones. La entidad representa la tabla de MySQL. Esta separación evita acoplar directamente la API al almacenamiento.

**¿El celular necesita MySQL?** No. Solo necesita poder llegar al backend. MySQL corre en el servidor; Spring Boot administra sus credenciales y consultas.

**¿GitHub es el servidor de la aplicación?** No. Guarda el código. Para usar el backend sin este computador hace falta desplegarlo con HTTPS y una base remota.

**¿Se guardan todos los usuarios en MySQL?** En modo de cuentas del servidor, el registro guarda usuarios en MySQL y el login entrega un JWT. Room conserva la sesión/caché y el perfil local. El modo de práctica usa cuentas locales separadas.

**¿Por qué existe el selector Cuentas en servidor?** Distingue el modo de práctica local del registro/login contra Spring Boot. En modo remoto se debe recibir una respuesta válida del servidor; si rechaza la contraseña no se acepta la copia local como alternativa.

**¿La base de la foto es la misma de las pruebas?** La base integrada es `master_martini`. Ya se creó desde las entidades reales del compañero y contiene las cinco tablas de la foto más cursos, recetas y videos. El SQL se exportó sin datos.

**¿Qué pasa si falla internet?** El catálogo local continúa disponible. Las operaciones remotas muestran un mensaje; el formulario se conserva y no se muestra éxito si el servidor no confirmó la escritura.

**¿La API externa es la misma que el backend?** No. TheMealDB es un servicio de terceros con búsqueda y consulta de recetas. Usa su propio Retrofit y sus modelos de respuesta.

**¿Qué significa la cobertura?** Es la proporción de líneas ejecutadas por las pruebas dentro del alcance del informe. Un porcentaje alto no prueba por sí solo que todo funciona; también se comprueban resultados, errores y operaciones reales.

**¿Qué está pendiente de coordinar?** El alojamiento y MySQL remotos, acceso a Trello y las pruebas finales en un Android autorizado. El backend y el SQL del equipo ya se integraron.


## Explicar la integración del backend

“Recibimos el backend del integrante 1 y agregamos mis tres módulos en ese mismo servidor. Contenido contiene los atributos comunes; Curso, Receta y Video heredan de esa entidad. Con JOINED, MySQL guarda la fila general y su relación en la tabla específica. Esto permite que un curso nuevo también aparezca en el catálogo general y se pueda marcar como favorito.”

“Al registrarse, Spring Boot guarda la contraseña con BCrypt y Android inicia sesión para recibir el JWT. El cliente agrega Authorization: Bearer solo al backend propio. El servidor valida el token antes de ejecutar el CRUD. Al cerrar sesión Android elimina el token. TheMealDB funciona con otra instancia sin ese token.”

Si preguntan por una eliminación: “Primero se comprueba que el ID corresponda al tipo de contenido. Se eliminan sus relaciones de favoritos y después la entidad. Todo ocurre en una transacción para conservar la integridad de la base.”
