# Checklist de validacion funcional (Epicas 1-6)

## 1) Preparacion de entorno

1. Copiar variables de entorno base:
   - `copy .env.example .env`
2. Levantar stack completo:
   - `docker compose up --build -d`
3. Verificar contenedores:
   - `docker compose ps`
4. Entrar al front:
   - http://localhost:4200/login
5. Entrar como admin de prueba:
   - usuario: `admin`
   - password: `admin123`

## 2) Punto critico de seguridad (bloqueo de baneados)

Objetivo: confirmar que un usuario baneado no puede iniciar sesion.

1. En front, navegar a `Moderación y Restricciones` (admin).
2. Seleccionar un usuario normal y aplicar baneo con motivo.
3. Cerrar sesion admin.
4. Intentar iniciar sesion con el usuario baneado.

Resultado esperado:
- El login no debe completar autenticacion.
- Debe mostrarse rechazo de autenticacion por usuario bloqueado.

## 3) Epica 1 - Gestion de publicaciones

1. Entrar como admin o usuario normal.
2. Ir a `Home` y crear una publicacion con categoria.
3. Confirmar que aparece en listado general.
4. Editar la publicacion desde `Gestion de Publicaciones` (admin).
5. Eliminar una publicacion desde `Gestion de Publicaciones`.
6. En `Moderación y Restricciones`, restringir un post con motivo.
7. Quitar la restriccion del post.

Resultado esperado:
- Crear/editar/eliminar persiste correctamente.
- La categoria queda guardada.
- Restriccion y desrestriccion reflejan cambios inmediatos.

## 4) Epica 2 - Gestion de usuarios

1. Entrar como admin.
2. Ir a `Gestión de Usuarios`.
3. Crear un usuario nuevo (rol USER).
4. Editar nombre/email/rol del usuario creado.
5. Eliminar ese usuario.
6. Repetir creando otro usuario y banearlo desde `Moderación y Restricciones`.
7. Probar login del usuario baneado.

Resultado esperado:
- Crear/editar/eliminar persiste en sistema.
- Roles ADMIN/USER se respetan.
- Usuario eliminado no autentica.
- Usuario baneado no autentica.

## 5) Epica 3 - Personalizacion de perfil

1. Entrar como usuario no admin.
2. Ir a `Perfil`.
3. Cambiar avatar, nombre de perfil y descripcion.
4. Cambiar flags de privacidad.
5. Guardar cambios.
6. Entrar como admin y abrir pendientes de moderacion en `Perfil`.
7. Aprobar o rechazar perfil con comentario.

Resultado esperado:
- Cambios de perfil se guardan.
- Privacidad y estado de moderacion se reflejan.
- Admin puede supervisar y moderar.

## 6) Epica 4 - Ver publicaciones propias

1. Entrar como usuario.
2. Ir a `Mi historial`.
3. Validar listado de publicaciones del usuario.
4. Filtrar por categoria.
5. Filtrar por fecha (desde/hasta).
6. Eliminar una publicacion anterior.

Resultado esperado:
- Listado muestra solo publicaciones del usuario.
- Filtros funcionan correctamente.
- Eliminacion persiste al recargar.

## 7) Epica 5 - Amistades

1. Entrar como usuario A.
2. Ir a `Amigos`, buscar usuario B y enviar solicitud.
3. Entrar como usuario B y revisar solicitudes pendientes.
4. Aceptar o rechazar solicitud.
5. Volver a usuario A y abrir `Home` en pestaña `Amigos`.

Resultado esperado:
- Solicitud se crea y aparece en pendientes.
- Estado cambia al aceptar/rechazar.
- Feed de amigos filtra por relaciones aceptadas.

## 8) Epica 6 - Comentarios

1. Entrar como usuario.
2. En `Home`, abrir comentarios de un post y agregar comentario.
3. Ir a `Mis comentarios`.
4. Editar comentario propio.
5. Eliminar comentario propio.
6. Confirmar que no aparece en listado tras eliminar.

Resultado esperado:
- Crear/editar/eliminar comentarios funciona solo para el autor.
- `Mis comentarios` refleja el estado real de BD.

## 9) Validaciones de datos en BD (opcional, recomendadas)

Ejecutar:
- `docker exec mysql-db mysql -uroot -p<tu_password> -e "SHOW DATABASES;"`
- `docker exec mysql-db mysql -uroot -p<tu_password> -e "USE auth_db; SELECT id,username,rol FROM usuarios;"`
- `docker exec mysql-db mysql -uroot -p<tu_password> -e "USE restricciones_db; SELECT * FROM usuarios_baneados;"`
- `docker exec mysql-db mysql -uroot -p<tu_password> -e "USE posts_db; SELECT id,autor_id,categoria FROM posts ORDER BY id DESC;"`
- `docker exec mysql-db mysql -uroot -p<tu_password> -e "USE comentarios_db; SELECT id,post_id,usuario_id FROM comentarios ORDER BY id DESC;"`

Resultado esperado:
- Schemas separados y datos consistentes por microservicio.

## 10) Cierre de prueba

1. Guardar evidencia (capturas y/o logs).
2. Apagar entorno:
   - `docker compose down`
3. Si se requiere limpieza total de datos:
   - `docker compose down -v`
