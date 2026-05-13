# Arquitectura DeTuBarrio

## 1. Vision general

DeTuBarrio esta organizado como una arquitectura cliente-servidor con separacion clara entre frontend y backend:

- Backend API REST en Spring Boot (carpeta `rest/rest`)
- Frontend SPA en Vue 3 + Vite (carpeta `a/vue`)
- Frontend legacy en HTML/CSS/JS (carpeta `a/html`)
- Base de datos MySQL remota o local, gestionada por Flyway

## 2. Diagrama logico

```text
[Usuario Navegador]
       |
       | HTTP(S)
       v
[Frontend Vue (a/vue)] ---------------------------.
       |                                          |
       | /api/* (proxy Vite en local)             |
       v                                          |
[Spring Boot API (rest/rest)]                     |
       |                                          |
       | JPA + Flyway                             |
       v                                          |
[MySQL (Aiven/local)] <---------------------------'
```

Tambien existe frontend legacy (`a/html`) como soporte de maquetas, pruebas o demo independiente.

## 3. Backend (Spring Boot)

### 3.1 Capas

- Controller: expone endpoints REST
- Service: logica de negocio
- Repository: acceso a datos con Spring Data JPA
- Security: autenticacion JWT y autorizacion por rol
- Config: seguridad HTTP, CORS y OpenAPI

### 3.2 Endpoints por dominio

Autenticacion:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

Catalogo:

- `GET /api/categorias`
- `POST /api/categorias`
- `GET /api/comercios`
- `GET /api/comercios/{comercioId}`
- `POST /api/comercios`
- `GET /api/comercios/{comercioId}/productos`
- `POST /api/comercios/{comercioId}/productos`

Reseñas/comentarios:

- `POST /api/comentarios`
- `GET /api/comercios/{comercioId}/resenas`
- `POST /api/comercios/{comercioId}/resenas`

Dashboard:

- `GET /api/dashboard/usuario`
- `GET /api/dashboard/comercio`
- `DELETE /api/dashboard/comercio`

Contacto:

- `POST /api/contacto/mensaje`
- `POST /api/contacto/colaboracion`

Admin:

- `GET /api/admin/comercios-pendientes`
- `POST /api/admin/comercios/aprobar`
- `POST /api/admin/comercios/rechazar`
- `GET /api/admin/contacto/mensajes`
- `GET /api/admin/contacto/colaboraciones`
- `POST /api/admin/contacto/colaboraciones/aprobar`
- `POST /api/admin/contacto/colaboraciones/rechazar`

Infra:

- `GET /api/health`
- `GET /swagger-ui.html`
- `GET /api-docs`

### 3.3 Seguridad

Segun configuracion actual:

- sesion stateless con JWT
- `register` y `login` publicos
- `me`, `dashboard` y `comentarios` autenticados
- `/api/admin/**` restringido a rol ADMIN
- CORS abierto por patron para desarrollo (`*`)

## 4. Datos y migraciones

- Base de datos: MySQL
- Migracion inicial: `V1__all_in_one.sql`
- Estrategia: `spring.jpa.hibernate.ddl-auto=validate`
- Flyway controla estructura e inserts iniciales

Implicaciones:

- la estructura se versiona con SQL (no con auto-creation de Hibernate)
- para nuevos cambios de esquema se deben crear migraciones nuevas (`V2`, `V3`, etc.)

## 5. Configuracion de entorno

El backend carga variables desde `rest/rest/.env` mediante:

- `spring.config.import=optional:file:.env[.properties]`

Variables clave:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION`

Buenas practicas:

- mantener `.env` fuera de Git
- compartir solo `.env.example`

## 6. Frontend Vue

### 6.1 Estructura

- `a/vue/src/views`: vistas por pagina
- `a/vue/src/services`: cliente HTTP hacia backend
- `a/vue/src/router/index.js`: rutas y guards por rol
- `a/vue/vite.config.js`: proxy `/api` hacia backend local

### 6.2 Flujo de autenticacion

- login en `/login`
- se guarda token JWT en `localStorage` (`detubarrio_auth`)
- interceptor axios añade `Authorization: Bearer ...`
- guard de rutas redirige segun rol

## 7. Frontend legacy

`a/html` mantiene version no SPA con paginas estaticas (login, listado, contacto, dashboards). Es util para:

- demos puntuales
- comparacion de UX
- soporte mientras se completa migracion a Vue

## 8. Decisiones de arquitectura

- Monorepo con backend + dos frontends para evolucion gradual
- Flyway como fuente de verdad del esquema
- JWT para autenticacion sin estado en servidor
- Separacion clara de capas para mantenibilidad
- Configuracion por variables de entorno para despliegues flexibles

## 9. Riesgos tecnicos y recomendaciones

Riesgos actuales:

- CORS demasiado abierto para produccion
- coexistencia de dos frontends puede duplicar mantenimiento
- falta de pipeline de pruebas automatizadas visible en repositorio

Recomendaciones:

1. endurecer CORS y cabeceras para produccion
2. cerrar estrategia de frontend principal (Vue) y acotar legacy
3. definir flujo de migraciones incremental (sin editar historico)
4. incorporar tests de integracion de endpoints criticos
5. documentar checklist de release y rollback
