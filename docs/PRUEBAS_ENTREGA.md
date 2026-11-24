# Verificación de la integración Master Martini

Se integró el backend real del compañero (`backend_app`, base 662c8ca) con los módulos del integrante 2. Android parte de la integración de main 7027e04. Los resultados siguientes corresponden a esta versión, no al backend provisional anterior.

| Verificación | Resultado |
|---|---|
| Pruebas Android | 57 ejecutadas; 0 fallos, errores u omisiones |
| Pruebas backend integrado | 21 ejecutadas; 0 fallos, errores u omisiones |
| Total | 78 pruebas aprobadas |
| JaCoCo backend, alcance global | 80.38 % de líneas (586/729); Maven exige mínimo 80 % |
| JaCoCo Android, lógica remota | 94.32 % de líneas (249/264); no equivale a toda la UI |
| JaCoCo Android, lógica móvil completa | 84.26 % de líneas (685/813); ViewModels, repositorios, utilidades, modelos con comportamiento y configuración de red |
| Lint Android | 0 errores, 54 advertencias |
| Release | APK firmado compilado; firma v2 verificada con apksigner |
| MySQL real | Registro, login JWT y 15 operaciones CRUD aprobados contra master_martini |
| Retrofit real | BackendLiveTest registra una cuenta, obtiene JWT y ejecuta CRUD en los tres módulos |
| API externa real | TheMealDbLiveTest busca y consulta una receta real con Retrofit |
| Instrumentación | APK de pruebas compilado; ejecución manual/instrumentada en Android pendiente |

## Base integrada

MySQL 8.4.3 local, puerto 3307, base master_martini. Tablas reales: usuarios, intereses, usuario_intereses, contenidos, favoritos, cursos, recetas y videos. El SQL sin datos se exportó a database/estructura-master-martini.sql del backend.

Los tres tipos heredan de Contenido mediante JOINED. Se verificó que un curso creado desde su módulo aparece también en el catálogo general y se relaciona con favoritos. Se comprobó el borrado de relaciones, el rechazo de accesos a favoritos ajenos, correo duplicado, cuenta inactiva y contraseña incorrecta.

## Comandos de validación

Android: `./gradlew.bat :app:assembleRelease :app:coberturaLogica :app:coberturaModulos :app:lintDebug :app:assembleDebugAndroidTest -PliveApi=true -PliveBackend=true`.

Backend: `./mvnw.cmd verify`, con Java 21 o superior. En este equipo se usó JBR 25 para backend y JDK 17 para Gradle.

Servidor real: `./tools/Probar-Servidor.ps1`, que prepara una cuenta local de prueba, carga ejemplos y realiza las operaciones. La cuenta queda en work/pruebas/cuenta-demo.json, excluida de Git.

## APK

Master Martini API 2.0, código 3; com.mastermartini.app.integracion; mínimo Android 7.0. Tamaño: 36218755 bytes. SHA-256: `bbeb3d76865ab24c7ba489ef04b0a8c8301a2bc40038699c2f648f058a5e8f47`. JKS, contraseñas y JWT_SECRET fuera de los archivos publicados.

## Límites comprobables

La base y la API funcionan en este computador. No se ha provisionado un alojamiento HTTPS ni MySQL de un proveedor remoto. El teléfono del trabajo no recibió ninguna instalación ni cambio desde estas herramientas. La cámara, el flujo visual completo y los controles de reproducción deben probarse en un Android autorizado. Las pantallas originales de perfil/intereses/favoritos siguen usando Room; sus endpoints remotos del backend se verificaron por separado, no se presentan como sincronización completa de esa UI.
