# TheMealDB — API externa de recetas

## Elección

TheMealDB permite mostrar recetas gastronómicas reales y sus preparaciones. Es un servicio independiente del backend Spring Boot del equipo. Se conserva el idioma original de sus resultados y la pantalla indica que el contenido está en inglés.

Documentación oficial: https://www.themealdb.com/api.php

La documentación permite la clave de prueba `1` para desarrollo y uso educativo. Una publicación pública en una tienda requiere revisar y cumplir su modalidad de producción. No se presenta esta clave de prueba como una credencial privada.

## Endpoints utilizados

Base HTTPS: `https://www.themealdb.com/api/json/v1/1/`

- `GET search.php?s=cake`: búsqueda por nombre. Responde con `meals` o `meals: null` cuando no encuentra resultados.
- `GET lookup.php?i={idMeal}`: consulta del detalle por identificador.

Campos consumidos: `idMeal`, `strMeal`, `strCategory`, `strArea`, `strInstructions`, `strMealThumb` y `strSource`.

## Implementación

- `RecetaExternaApi.kt`: interfaz Retrofit y modelos de respuesta.
- `RecetaExternaRepository.kt`: búsqueda y consulta; normaliza listas nulas.
- `RecetaExternaViewModel.kt`: StateFlow, carga, cancelación de búsquedas previas, lista, detalle y error recuperable.
- `RecetaExternaScreen.kt`: búsqueda, tarjetas con imágenes mediante Coil, preparación y atribución visible a TheMealDB.
- `NetworkModule.kt`: configura la instancia para el host externo sin enviar la clave del backend propio.

## Pruebas

`RecetaExternaTest` usa MockWebServer para comprobar rutas, lectura del JSON y ausencia de resultados; también comprueba el ViewModel ante errores y recuperación.

`TheMealDbLiveTest` realiza una búsqueda real con Retrofit, consulta uno de los identificadores recibidos y comprueba preparación e imagen HTTPS. Ejecutar `./gradlew.bat :app:testDebugUnitTest -PliveApi=true`. Esta prueba necesita internet y depende de la disponibilidad de TheMealDB.

Prueba manual: Recetas → Explorar recetas reales · TheMealDB → buscar `cake` → abrir una receta → comprobar título, imagen y preparación. Buscar un texto inexistente para revisar el estado vacío; cortar internet para revisar el error.
