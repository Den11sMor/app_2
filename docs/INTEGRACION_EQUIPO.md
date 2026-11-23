# Acuerdos para integrar y entregar

Android y backend tienen sus repositorios y ramas individuales. El servidor común es backend_app, partiendo del código del integrante 1; los módulos cursos, recetas y videos del integrante 2 se integran allí. Android usa un solo NetworkModule.

Contenido es la entidad base del catálogo; las tres entidades concretas usan herencia JOINED. Los favoritos del servidor apuntan al contenido común. No duplicar configuraciones Spring Boot, Retrofit ni claves de base dentro de Android.

Conservar el JKS y los secretos fuera de Git. Aprovisionar el alojamiento HTTPS/MySQL remota y repetir pruebas con su URL. Registrar las tareas reales en el tablero del equipo cuando se tenga acceso. Se verificó 84,26 % de cobertura de líneas de lógica móvil en Android y 80,38 % global en backend. Android incluye todos los ViewModels y repositorios, utilidades, modelos con comportamiento y configuración de red; excluye vistas Compose y código generado. Los informes indican sus alcances por separado.

Antes de fusionar a main, revisar ambos cambios y conservar la historia. No usar force push. El usuario solicitó aprobar los commits antes de publicarlos.
