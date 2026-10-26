# Guía para revisar y explicar el proyecto

## Cómo funciona una acción

Ejemplo: crear una cuenta.

1. RegistroScreen dibuja los campos y observa RegistroUiState.
2. Cada cambio de texto llega a RegistroViewModel.
3. Al enviar, el ViewModel consulta Validaciones y publica los errores.
4. Si los campos son válidos, UsuarioRepository normaliza el correo y deriva la contraseña con una sal aleatoria.
5. UsuarioDao inserta el usuario en Room.
6. Se recuerda el identificador de sesión y la UI navega a los intereses.

No se compara una contraseña directamente dentro de un botón. Las pantallas tampoco ejecutan consultas SQL.

Ejemplo: guardar un favorito.

1. El corazón envía el identificador del contenido al ViewModel.
2. FavoritoRepository agrega o quita la relación usuario/contenido.
3. Room emite los nuevos identificadores mediante Flow.
4. El StateFlow del ViewModel se actualiza y Compose cambia el corazón.

## Relación con la rúbrica

| Criterio | Implementación para demostrar |
| --- | --- |
| IE 2.1.1 Interfaz y navegación | Tema del proyecto, componentes compartidos, pantallas desplazables, detalle por identificador y flechas para volver. |
| IE 2.1.2 Formularios | Registro, login, perfil e intereses con errores visibles, etiquetas e iconos. |
| IE 2.2.1 Validación y estado | Validaciones reutilizables y ViewModels con StateFlow; carga, éxito y error. |
| IE 2.2.2 Animaciones | Entrada/salida de pantallas, mensajes animados y cambio de selección en intereses. |
| IE 2.3.1 Organización y persistencia | Room con usuarios, contenidos y favoritos; capas data/domain/ui; herencia de Contenido y acciones específicas. |
| IE 2.3.2 Colaboración | Revisar y realizar los commits reales de cada integrante. La documentación no sustituye la participación. |
| IE 2.4.1 Recursos del dispositivo | Cámara y galería desde Perfil, con URI limitada por FileProvider y selector del sistema. Verificar ambos en el dispositivo de presentación. |

## Recorrido manual recomendado

- [ ] Intentar registrar campos vacíos y revisar los mensajes.
- [ ] Escribir correo o teléfono incorrectos y contraseñas diferentes.
- [ ] Crear una cuenta válida y seleccionar intereses.
- [ ] Cerrar sesión e intentar registrar el mismo correo.
- [ ] Probar login con contraseña incorrecta y después correcta.
- [ ] Abrir todas las secciones, buscar Brownie y revisar su detalle.
- [ ] Agregar y quitar favoritos desde tarjetas y detalle.
- [ ] Cerrar y abrir la app; comprobar sesión y favoritos.
- [ ] Editar el nombre y teléfono, guardar y volver a Inicio.
- [ ] Cambiar intereses desde Perfil.
- [ ] Abrir Cámara, cancelar y comprobar que se mantiene la foto anterior.
- [ ] Tomar una foto y comprobar que se guarda al reabrir la app.
- [ ] Elegir una foto desde Galería y repetir la comprobación.
- [ ] Iniciar y cancelar el temporizador.
- [ ] Rotar el teléfono y revisar navegación, formularios y temporizador.
- [ ] Iniciar sesión con otro usuario y comprobar que no ve los favoritos del primero.

## Límites que debes conocer

Las guías de video y los programas de cursos son contenido educativo local de demostración. No hay videos reproducibles, conexión a un servidor, inscripción en cursos ni notificaciones del temporizador. No afirmar esas funciones durante la presentación.

No se recibió un documento de reglas de negocio específico aparte de la rúbrica. Si el profesor entregó otro enunciado, hay que contrastarlo antes de entregar.
