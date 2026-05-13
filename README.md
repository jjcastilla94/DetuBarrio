# DeTuBarrio

Proyecto TFG (2DAW) orientado a comercios de barrio con backend Spring Boot + JWT, base de datos MySQL gestionada por Flyway y frontend Vue 3.

## Resumen del proyecto

DeTuBarrio permite:

- consultar categorias y comercios
- ver detalle de comercios y sus productos
- crear reseñas/comentarios
- autenticacion con JWT (login, registro y endpoint me)
- dashboards por rol (USUARIO, COMERCIO y ADMIN)
- gestion administrativa de solicitudes de comercios y mensajes de contacto

## Stack tecnologico

- Backend: Java 21, Spring Boot 3.5.x, Spring Security, Spring Data JPA, Flyway, MySQL, Swagger/OpenAPI
- Frontend principal: Vue 3 + Vite + Vue Router + Bootstrap

## Estructura del repositorio

```text
DetuBarrio/
|- rest/rest/                 # API Spring Boot (backend)
|  |- src/main/java/          # Controladores, servicios, repositorios, seguridad
|  |- src/main/resources/     # application.properties + migraciones Flyway
|  |- .env.example            # plantilla de variables locales (sin secretos)
|  |- pom.xml
|
|- a/vue/                     # Frontend SPA Vue 3
|- ARQUITECTURA.md            # Arquitectura tecnica
|- AIVEN_SETUP.md             # Guia de base de datos remota en Aiven
|- MANUAL_USO.md              # Manual funcional por roles
```

## Documentacion recomendada

1. [ARQUITECTURA.md](ARQUITECTURA.md): vision tecnica completa (capas, seguridad, datos, flujo)
2. [AIVEN_SETUP.md](AIVEN_SETUP.md): instalacion y conexion a MySQL remota
3. [MANUAL_USO.md](MANUAL_USO.md): guia de uso por rol y funciones

## Requisitos previos

- Java 21
- Maven Wrapper incluido (no necesitas Maven global)
- Node.js 20+
- Servicio MySQL accesible (por ejemplo Aiven)

## Configuracion local rapida

### 1) Backend

Desde la raiz del repo:

```powershell
cd rest/rest
Copy-Item .env.example .env
```

Edita el archivo `.env` con valores reales:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION`

Ejecuta el backend:

```powershell
cd rest/rest
.\mvnw.cmd spring-boot:run
```

Swagger:

- http://localhost:8080/swagger-ui.html
- http://localhost:8080/api-docs

### 2) Frontend Vue

```powershell
cd a/vue
npm install
npm run dev
```

App Vue en desarrollo:

- http://localhost:5173

Nota: el frontend Vue usa proxy Vite para `/api` hacia `http://localhost:8080`.

## Endpoints principales

- `GET /api/health`
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me` (requiere token)
- `GET /api/categorias`
- `GET /api/comercios`
- `GET /api/comercios/{id}`
- `POST /api/comentarios` (requiere token)
- `GET /api/dashboard/usuario` (requiere rol USUARIO)
- `GET /api/dashboard/comercio` (requiere rol COMERCIO)
- `GET /api/admin/comercios-pendientes` (requiere rol ADMIN)

## Notas de seguridad importantes

- No subas nunca `rest/rest/.env` al repositorio.
- El repositorio ya ignora `.env` y publica solo `.env.example`.
- Usa un `APP_JWT_SECRET` largo y aleatorio.

Generar secreto seguro en PowerShell:

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

## Flujo recomendado de cambios de BD

- No edites migraciones ya aplicadas en entornos compartidos.
- Crea nuevas migraciones versionadas (`V2__...sql`, `V3__...sql`, etc.).
- Flyway las aplicara automaticamente al arrancar.

## Estado actual del proyecto

- Backend validado conectando a Aiven (`detubarrio_dev`)
- Migraciones Flyway aplicadas correctamente
- Frontend Vue listo para trabajar en local
- Documentacion alineada a la arquitectura real del repositorio.
