# Microclases en video — versión 1.1

## Contenido incluido

| Archivo local | Tema | Duración real |
| --- | --- | --- |
| tecnicas_chocolateria.mp4 | Bases del templado y método de siembra | 1:25 |
| decoracion_tortas.mp4 | Cobertura, manga pastelera y patrón decorativo | 1:18 |

Son videos educativos ilustrados originales: dibujos animados, narración sintética en español y texto de apoyo. No son filmaciones de una preparación real. Los guiones y las ilustraciones fueron creados para este proyecto; no se descargaron videos, imágenes ni música de terceros.

Los archivos están en `app/src/main/res/raw/`. Usan MP4, video H.264 Constrained Baseline, imagen 960 × 540 a 20 fps y audio AAC-LC mono a 44,1 kHz. Juntos ocupan aproximadamente 2,4 MB y funcionan sin conexión.

## Integración

- Botón Ver video en cada tarjeta y duración actualizada en el catálogo.
- Reproducción iniciada por el usuario, pausa y barra para adelantar o retroceder.
- Pantalla completa con controles que se adaptan al giro del teléfono.
- Pausa al bloquear el teléfono o cambiar de aplicación.
- Liberación del reproductor al volver al catálogo.
- Mensaje y opción de reintento si ocurre un error.
- Actualización del catálogo de instalaciones anteriores con UPDATE; no se reemplazan las filas ni se borran favoritos.

La versión aumenta a 1.1 (código 2), con el mismo identificador de aplicación que el APK de main entregado previamente.

## Cómo se generaron

Los guiones editables están en `tools/videos/lessons.json`. La narración se produce en Windows con una voz española instalada, sin usar un servicio remoto. Pillow dibuja las ilustraciones y FFmpeg las convierte en video.

En un entorno de producción que tenga Python:

```powershell
python -m pip install pillow imageio-ffmpeg
.\tools\videos\narrar.ps1 -CarpetaTrabajo "$PWD\work\videos"
python tools/videos/generar_videos.py work/videos app/src/main/res/raw
```

Las herramientas de generación no forman parte de la app Android. Si se cambia la voz o el guion, revisar `metadata.json` en la carpeta de trabajo y actualizar las duraciones del catálogo.

## Fuentes técnicas del contenido educativo

Las explicaciones se redactaron con palabras propias, tomando como referencia los principios descritos por:

- Callebaut: https://www.callebaut.com/en/callebaut-chocolate-academy/tutorials/tempering-chocolate-the-seeding-method
- Wilton: https://wilton.com/how-to-prepare-a-decorating-bag/wltech-7846/
- Wilton: https://wilton.com/piping-bag-pressure-control/wltech-414/

No se indican temperaturas universales de templado: se remite a la curva del fabricante para cada producto.

Formato Android: https://developer.android.com/media/platform/supported-formats

## Verificación

Se verificó la decodificación completa de imagen y audio con FFmpeg y se revisaron las imágenes de las escenas. Hay pruebas instrumentadas en `VideosTest.kt` para comprobar los recursos, los controles y que se conservan favoritos anteriores. Estas pruebas requieren un teléfono o emulador operativo; su ejecución en dispositivo está pendiente.

La prueba unitaria de correo que ya fallaba en main no se modifica en esta tarea: la validación actual rechaza el símbolo + y la prueba antigua lo acepta. Esa regla sigue pendiente de contrastar con el enunciado.

## Probar en el celular

1. Instalar el APK 1.1 encima de la versión de main instalada anteriormente.
2. Abrir Videos y elegir Técnicas de chocolatería.
3. Pulsar Reproducir y escuchar la narración.
4. Pausar, adelantar, volver a reproducir y usar pantalla completa.
5. Girar el teléfono y comprobar que los controles siguen visibles.
6. Regresar al catálogo: el sonido debe detenerse.
7. Repetir con Decoración de tortas y comprobar ambos en modo avión.

### Resultado de compilación de la actualización

- assembleDebug: correcto.
- compileDebugAndroidTestKotlin: correcto.
- lintDebug: 0 errores y 36 advertencias.
- Firma del APK verificada y coincidente con la versión 1.0 de main.
- Comprobación de integridad de los dos MP4 dentro del APK: correcta.
- Pruebas instrumentadas en teléfono: pendientes de ejecución.
