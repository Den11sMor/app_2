# Verificación de esta entrega

## Resultados comprobados

Se ejecutaron con Gradle 9.5.0 y JDK 17:

- `:app:assembleDebug`: correcto.
- `:app:testDebugUnitTest`: 6 pruebas, 0 fallos y 0 errores.
- `:app:lintDebug`: 0 errores y 16 advertencias. Son sugerencias de nuevas versiones, recursos de la plantilla sin usar y una sugerencia de KTX.
- `:app:assembleDebugAndroidTest`: correcto. Las pruebas instrumentadas compilan.
- `git diff --check`: correcto.

Las pruebas unitarias verifican entradas vacías, correo, teléfono, contraseña, confirmación, comprobación de hash y sal distinta por usuario.

## Verificaciones pendientes en dispositivo

Las cuatro pruebas instrumentadas (Room y recorrido de la app) no se ejecutaron. El emulador disponible no pudo completar su arranque en este entorno; inicialmente faltó aceleración y después hubo limitaciones de memoria. También se intentó una configuración de software con menos RAM.

No se verificó visualmente el funcionamiento de cámara o galería. Deben comprobarse junto con el recorrido de [REVISION.md](REVISION.md) en un teléfono o emulador que termine de arrancar.

Para ejecutar las pruebas instrumentadas:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

## Estado de Git

- Rama de trabajo: `Luis`.
- Base: `d61ca6e`, el avance visual que ya existía.
- La aplicación y la documentación se guardan en dos commits locales; todavía no se han subido a GitHub.
- La copia de la carpeta del proyecto conserva el historial Git para continuar los commits.
- El ZIP contiene el código fuente sin la carpeta interna .git. Para hacer commits con el historial, usa la carpeta del proyecto conservada junto al ZIP.

## Contenido de demostración

Los videos son guías de estudio sin reproducción; los cursos contienen programas locales. Las cuentas se guardan únicamente en este dispositivo y el temporizador no genera alarmas de fondo.
