# Integración del equipo

## Fuentes recibidas

Android: avance del integrante 1 en main `7027e04`, basado en `5c4e1c1`. Backend: repositorio `Den11sMor/backend_app`, base `662c8ca`, recibido tras aceptar la invitación. Se conserva la historia original en ambos repositorios.

## Solución integrada

Se trasladaron los módulos provisionales del integrante 2 al backend real del equipo. Se conservan Java 21, Spring Boot 4.2, usuarios, intereses, favoritos, BCrypt y JWT del compañero. Ya no hay una segunda aplicación Spring Boot dentro de Android.

Curso, Receta y Video heredan de Contenido con JPA JOINED. Los datos generales están en contenidos y las tablas específicas comparten su identificador. Por ello el catálogo general, filtros y favoritos pueden consultar los contenidos creados desde los tres módulos.

## Correcciones comprobadas

- Retrofit único en Android, URL configurable para usuarios y contenidos, JWT exclusivo del backend propio.
- Registro seguido de login para obtener el token antes de abrir contenidos protegidos.
- Sin logging de contraseñas; sin HTTP general hacia hosts públicos.
- Un rechazo remoto no se convierte en un acceso aceptado con contraseña local.
- Los ID de Room no se sustituyen por los ID de MySQL; cerrar sesión elimina el token.
- Validación por campo, 404 por ID inexistente y confirmación de borrado.
- Columnas de la clave compuesta de favoritos unificadas; lectura de relaciones con EntityGraph y control de acceso al propietario.
- Eliminación del contenido y sus favoritos dentro de una transacción.

## MySQL

La base master_martini se creó realmente en la instancia local aislada. Contiene las cinco tablas de la foto y las tres de los módulos nuevos. Se exportó el SQL sin datos a `database/estructura-master-martini.sql` en backend_app. Se verificaron registro, login y CRUD con MySQL real; además se probaron desde Retrofit.

## Alcance restante

Alojamiento HTTPS y MySQL remota pendientes de aprovisionar. La cámara, la reproducción y el flujo visual completo deben comprobarse en un Android autorizado. La UI original de perfil/intereses/favoritos mantiene Room; no se afirma que esos formularios ya sincronicen con sus endpoints remotos. Trello espera acceso al tablero del equipo.
