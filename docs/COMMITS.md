# Tus commits en la rama Luis

El avance inicial se guardó en dos commits en la rama `Luis`:

1. `feat: completar registro, perfil y favoritos con persistencia local` — aplicación y pruebas.
2. `docs: agregar guía de revisión y resultados de pruebas` — explicación, revisión de la rúbrica y verificaciones.

Se usó la identidad de Git que ya estaba configurada en este repositorio. Ese avance inicial ya fue subido a GitHub e integrado en main.

## Cómo se hicieron

Primero se revisaron los archivos con `git status` y `git diff`. Después se seleccionó el código con `git add .gitattributes app gradle` y se guardó el primer commit con `git commit -m "mensaje"`. Finalmente se seleccionaron `README.md` y `docs` para guardar la documentación en otro commit.

`git add` elige los cambios que se van a guardar; `git commit` crea una versión local. `git push` sube los commits a GitHub.

## Cómo elegir el nombre

Usa un verbo y describe el cambio concreto. El formato es `tipo: descripción`:

- `feat: agregar búsqueda de recetas` para una función nueva.
- `fix: corregir validación del correo` para una corrección.
- `docs: explicar cómo ejecutar la aplicación` para documentación.
- `test: agregar pruebas del registro` para pruebas.

El mensaje se elige al ejecutar `git commit -m "el nombre que quieras"`. Los comandos de las secciones siguientes sirven como referencia para futuros avances; no hace falta repetir los commits de esta entrega.

## Antes de guardar un avance

Abrir una terminal en esta carpeta y comprobar:

```powershell
git branch --show-current
git status
git diff --stat
git config user.name
git config user.email
```

La rama debe ser Luis. Si aparece AV-Alumno en la identidad, configurar solo este repositorio con tu nombre y el correo asociado a tu GitHub. También puedes usar el correo privado noreply de tu cuenta:

```powershell
git config user.name "TU NOMBRE"
git config user.email "TU CORREO DE GITHUB"
```

Estos comandos no cambian la identidad de los commits antiguos.

## Opción sencilla: guardar el avance completo

Después de revisar y probar el proyecto:

```powershell
git add .
git diff --cached --stat
git commit -m "EL NOMBRE QUE TU ELIJAS"
```

Git ignora las carpetas de compilación, propiedades locales y datos privados de la sesión. Revisa igualmente la lista antes de confirmar.

## Opción por funcionalidades

Puedes separar el trabajo en dos avances que se pueden revisar por separado:

1. Aplicación y pruebas: Room, validaciones, pantallas, navegación, recursos del dispositivo y pruebas.
2. Documentación para revisar el proyecto y preparar la entrega.

Para preparar el primer grupo:

```powershell
git add .gitattributes app gradle
git diff --cached --stat
git commit -m "NOMBRE DEL PRIMER AVANCE"
```

Después, para el segundo:

```powershell
git add README.md docs
git diff --cached --stat
git commit -m "NOMBRE DEL SEGUNDO AVANCE"
```

El primer grupo reemplaza las pruebas de ejemplo de la plantilla por pruebas de la aplicación.

Para seleccionar archivos usa la ventana Commit de Android Studio o git add con las rutas concretas. Confirma que el grupo seleccionado coincide con el mensaje.

## Antes de subir

Consultar si tu compañero publicó nuevos cambios:

```powershell
git fetch origin
git log --oneline --left-right HEAD...origin/Luis
```

Si no hay commits remotos nuevos que falten en tu rama, subir:

```powershell
git push origin Luis
```

Si Git rechaza la subida, no usar force. Hay que revisar e integrar los cambios nuevos.

Después se puede abrir un pull request desde Luis hacia main, describiendo las funciones y las pruebas que realmente se ejecutaron. La integración a main se revisa por separado.
