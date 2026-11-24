# Revisión contra la rúbrica de la entrega

Fecha: 23 de noviembre de 2026. Revisión local, antes de autorizar los commits.

| Criterio | Evidencia real | Estado y límite |
|---|---|---|
| Android completo (15%) | Formularios CRUD de cursos, recetas y videos; validaciones, carga, errores y navegación implementados; Release compila y lint no reporta errores. | Falta recorrer manualmente todas las pantallas en un Android autorizado. Perfil/intereses/favoritos de la interfaz original siguen siendo locales. |
| Backend y base (15%) | Spring Boot integrado con el backend del compañero; ocho tablas MySQL reales; cinco endpoints por módulo; JWT y validaciones probados. | Funciona localmente. No existe todavía despliegue HTTPS/MySQL de un proveedor remoto. Es un backend modular, no varios servicios desplegados de forma independiente. |
| Integración móvil/backend (15%) | Retrofit real con JWT: registro/login y las cinco operaciones CRUD de cada uno de los tres módulos, ejecutados contra Spring Boot y MySQL. | Probado desde JVM con Retrofit real; falta la demostración visual desde el celular. |
| API externa (15%) | TheMealDB con búsqueda, detalle, imágenes y pantalla; prueba real de búsqueda y consulta aprobada. | Requiere internet para consultar el proveedor externo. |
| Pruebas >=80% de lógica Android (15%) | 57 pruebas Android aprobadas y 84,26% de líneas de lógica móvil (685/813). Backend: 21 pruebas y 80,38% global (586/729). | Cumple la medición de líneas declarada. Se excluyen vistas Compose y código generado; no equivale a 80% de ramas ni a pruebas manuales de toda la interfaz. |
| APK firmado (10%) | Release 2.0 código 3 compilado y firma v2 verificada. JKS privado y configuración de firma preparados. | Entregar la firma solamente por un canal privado; nunca incluir el ZIP privado en GitHub público. |
| GitHub y Trello (15%) | Historial anterior existente, cambios nuevos preparados en ramas individuales, documentación y CSV de tareas. | Commits nuevos pendientes de autorización. No se han creado ni actualizado tarjetas de Trello: falta acceso al tablero. |

## Comparación con el mensaje del compañero

Ya se ejecutaron las pruebas, se superó el 80% de líneas de lógica Android, existe el JKS y se generó el APK firmado. También se integró el backend real del equipo en lugar de conservar dos configuraciones distintas. Por eso esos puntos ya no están pendientes en esta copia local.

Siguen pendientes la publicación autorizada en GitHub, el tablero real de Trello, el alojamiento remoto, la prueba manual del teléfono y la entrega en AVA. La guía de defensa y la documentación están preparadas, pero se deben estudiar y practicar.

## Material para AVA

1. MasterMartini-API-2.0-release.apk: APK firmado.
2. MasterMartini-Entrega-Completa.zip: ambos proyectos integrados, sin claves ni contraseñas.
3. Resultados-de-pruebas.md e Informes-cobertura.zip: resultados y reportes HTML/XML.
4. MasterMartini-Estructura-MySQL.sql: estructura sin datos; importar únicamente en una base nueva.
5. Entrega-Privada-Firma-AVA.zip: JKS y configuración, solo para el profesor si lo pide y por canal privado.
6. Enlaces de ramas/commits GitHub una vez autorizada y verificada la publicación, y enlace/evidencias de Trello cuando exista el tablero.

No se ha enviado nada a AVA ni instalado nada en el teléfono del trabajo. GitHub aloja el código, pero no mantiene encendido Spring Boot ni MySQL.
