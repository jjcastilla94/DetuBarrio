# DeTuBarrio - MVP Backend + Frontend

Aplicacion TFG (2DAW) con API REST, autenticacion JWT, reseñas y frontend Bootstrap conectado.

## Stack tecnologico

- Java 21
- Spring Boot 3 (Web, JPA, Security, Validation)
- H2 (perfil local de desarrollo)
- Maven
- Bootstrap 5 + HTML + CSS + JavaScript vanilla
- OpenAPI/Swagger

## Funcionalidad MVP implementada

- Catalogo de categorias, comercios y productos
- Reseñas/comentarios desde frontend y API
- Autenticacion JWT (login/register/me)
- Dashboards por rol (`USUARIO`, `COMERCIO`)
- Documentacion API con Swagger UI
- Datos semilla para demo local

## Estructura

- `src/main/java/detubarrio/rest/config`: seguridad, CORS, OpenAPI y datos semilla.
- `src/main/java/detubarrio/rest/controller`: endpoints REST.
- `src/main/java/detubarrio/rest/service`: logica de negocio.
- `src/main/java/detubarrio/rest/repository`: acceso a datos.
- `src/main/java/detubarrio/rest/model`: entidades JPA.
- `src/main/java/detubarrio/rest/dto`: contratos de API.
- `src/main/java/detubarrio/rest/security`: JWT y filtro de autenticacion.
- `src/main/java/detubarrio/rest/exception`: manejo global de errores.
- `src/main/resources/static`: frontend servido por Spring Boot.

## Arranque local (Windows PowerShell)

Ejecuta en esta carpeta (`rest/rest`):

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

Importante:

- No cierres esa terminal: si la cierras, se detiene el backend.
- Abre otra terminal para hacer pruebas (`Terminal > New Terminal`).

## Validacion rapida (smoke test)

Pega en una terminal nueva:

```powershell
# Health
Invoke-RestMethod "http://localhost:8080/api/health" | ConvertTo-Json -Compress

# Listar comercios
Invoke-RestMethod "http://localhost:8080/api/comercios" | ConvertTo-Json -Depth 5

# Login usuario semilla
$loginBody = @{ email='ana@detubarrio.local'; password='123456' } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -ContentType "application/json" -Body $loginBody
$login | ConvertTo-Json -Compress

# Endpoint protegido /me
$headers = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Headers $headers | ConvertTo-Json -Compress

# Crear comentario/reseña
$body = @{ comercioId=1; titulo='Prueba'; comentario='Funciona'; valoracion=5; autorNombre='QA'; autorEmail='qa@example.com' } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/comentarios" -ContentType "application/json; charset=utf-8" -Body $body | ConvertTo-Json -Compress
```

Si todas responden bien, el MVP backend+auth+reviews esta operativo.

## Swagger

Con el backend encendido:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

## Frontend para demo

- `http://localhost:8080/login_db.html`
- `http://localhost:8080/gestion_usuario.html`
- `http://localhost:8080/gestion_comercio.html`
- `http://localhost:8080/comercio_individual.html?id=1`

## Script SQL

En la raiz del workspace tienes `Script_corregido.sql` para referencia del modelo relacional (MySQL 8+).

### Aclaracion tecnica sobre el arranque

- El backend **no** carga estos scripts SQL automaticamente porque en `rest/rest/src/main/resources/application.properties` esta configurado `spring.sql.init.mode=never`.
- En local se usa H2 en memoria (perfil `local`) y carga de datos semilla en Java (`DataSeederConfig`), por lo que el arranque **no depende** de `Script.sql` ni de `Script_corregido.sql`.

## Siguientes mejoras 

** Ahora mismo el desarrollo del proyecto esta en fase beta, es decir hay cierta parte funcional ya pero requiere de tiempo para pulir y desarrollar todo lo demas **

1. Arreglar el footer de listado comercios
2. Implementar logica de codigo para funcionamiento de otros elementos
3. Mejorar tema de usabilidad y accesibilidad en el tema de las cosas poder acceder de mejor manera
4. Añadir tests de integracion de auth, dashboard y comentarios.
5. Documentar capturas de flujo en memoria del TFG (login, reseña, Swagger).
6. Preparar despliegue (Docker o VPS) para demostracion final.
..
....
......
........
..........