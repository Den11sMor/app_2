# Conexión y entrega integrada

## Capas

Android consume Spring Boot con Retrofit. Spring Boot valida, autentica con JWT y utiliza JPA para llegar a MySQL. Room conserva datos de práctica en el teléfono. GitHub conserva el código y no mantiene el servidor en ejecución.

## Carpetas y herramientas

Clonar `app_2` y `backend_app` como carpetas hermanas. Android requiere JDK 17; backend_app conserva Java 21 y Spring Boot 4.2 del compañero. En este equipo se usó el JBR 25 de Android Studio para ejecutar y compilar el backend a Java 21, y JDK 17 para Gradle.

Desde Android ejecutar Preparar-MySQL-Local.ps1, Iniciar-Backend.ps1 y Probar-Servidor.ps1. Estos scripts están en `tools/`. Si el backend está en otra carpeta, indicar `-BackendPath`. La instancia MySQL de pruebas escucha solo en loopback, puerto 3307; no se modifican bases de otros proyectos.

Credenciales MySQL y JWT: archivo privado de configuración en `work/mysql-local/credenciales.json`. El script puede conservar un directorio de datos ya preparado. No subir esa carpeta a Git. El backend genera las tablas de `master_martini` desde las entidades integradas; el SQL sin datos está en el repositorio backend.

`Probar-Servidor.ps1` crea una cuenta de prueba, obtiene el JWT, carga ejemplos y prueba el CRUD. Sus credenciales se guardan en `work/pruebas/cuenta-demo.json`. No hay una cuenta pública predeterminada del sistema.

## Android autorizado

1. Instalar el APK firmado Master Martini API. Se instala junto a la versión anterior.
2. Para usar el computador como servidor por USB, activar y autorizar Depuración USB y ejecutar Conectar-Android.ps1.
3. URL: `http://127.0.0.1:8080/`. En emulador: `http://10.0.2.2:8080/`.
4. Registrar una cuenta con Cuentas en servidor activado; Android obtiene el JWT al terminar el registro. También se puede iniciar sesión con una cuenta remota existente.
5. Abrir un módulo y pulsar Gestionar contenidos del servidor. Mantener cable y servidor activos.
6. Al cerrar sesión se borra el token. Si vence, volver a iniciar sesión.

Se detuvo la instalación en el teléfono del trabajo al plantearse esa restricción. Las pruebas manuales en un Android autorizado siguen pendientes.

## Servidor remoto

Falta aprovisionar alojamiento y MySQL remota. En el alojamiento configurar DB_URL con TLS, DB_USERNAME, DB_PASSWORD, JWT_SECRET privado, SERVER_ADDRESS=0.0.0.0 y PORT. Servir la API con HTTPS. Android usa esa URL desde Configurar conexión; nunca recibe las credenciales de la base. Repetir Probar-Servidor.ps1 con la URL del alojamiento para acreditar esa conexión remota.

## Firma

`signing.properties` configura el JKS y sus contraseñas fuera de Git. La plantilla pública no contiene secretos. Guardar una copia privada de la misma clave para futuras actualizaciones. Generar con `./gradlew.bat :app:assembleRelease`. El APK se encuentra en `app/build/outputs/apk/release/app-release.apk`.
