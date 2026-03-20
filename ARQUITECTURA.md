# Arquitectura DeTuBarrio - Guía Visual para TFG

## 1. Visión General de la Arquitectura

DeTuBarrio es una **aplicación monolítica Spring Boot** que integra frontend estático y backend API REST en un único servidor. Esta decisión facilita el despliegue, elimina problemas de CORS.

```
┌─────────────────────────────────────────────────────────────┐
│                 NAVEGADOR DEL USUARIO                       │
│         (http://localhost:8080/login_db.html)              │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTP con fetch()
                             ▼
┌─────────────────────────────────────────────────────────────┐
│          Spring Boot 3 - Un solo servidor (Puerto 8080)    │
├──────────────────────────┬──────────────────────────────────┤
│  FRONTEND ESTÁTICO       │     BACKEND API REST            │
│  (Controlador por URL)   │     (Controlador JSON)          │
├──────────────────────────┼──────────────────────────────────┤
│ • /login_db.html         │  • /api/health                  │
│ • /comercio_individual   │  • /api/comercios               │
│ • /gestion_usuario.html  │  • /api/auth/login              │
│ • /gestion_comercio.html │  • /api/auth/register           │
│ • /images, /css, /js     │  • /api/auth/me                 │
│                          │  • /api/comentarios             │
│                          │  • /api/dashboard/*             │
│                          │  • /swagger-ui.html             │
└──────────────────────────┴──────────────────────────────────┘
                             │
                             ▼
        ┌────────────────────────────┐
        │   Capa de Servicios        │
        │  (Lógica de Negocio)       │
        │                            │
        │ • ComercioService          │
        │ • AuthService              │
        │ • ResenaService (Comentarios)
        └────────────────────────────┘
                             │
                             ▼
        ┌────────────────────────────┐
        │   Capa de Repositorios     │
        │  (Spring Data JPA)         │
        │                            │
        │ • ComercioRepository       │
        │ • UsuarioRepository        │
        │ • ResenaRepository         │
        └────────────────────────────┘
                             │
                             ▼
        ┌────────────────────────────┐
        │   Base de Datos            │
        │                            │
        │ Local: H2 en RAM           │
        │ Producción: MySQL          │
        └────────────────────────────┘
```

---

## 2. Flujo de Datos: Ejemplo Real (Crear Reseña)

```
Usuario en navegador
      │
      ├─ Rellena formulario en comercio_individual.html
      │
      └─► Click "Enviar Reseña"
            │
            ▼
      comercio-detalle.js (fetch POST)
            │
            └─► POST /api/comentarios
                    {
                      "comercioId": 1,
                      "titulo": "Excelente",
                      "comentario": "Muy bueno",
                      "valoracion": 5,
                      "autorNombre": "Ana",
                      "autorEmail": "ana@example.com"
                    }
                    │
                    ▼
            ComentarioController.java
                    │
                    ▼
            ResenaService.java (lógica de negocio)
                    │
                    ▼
            ResenaRepository.save()
                    │
                    ▼
            H2 Database (INSERT)
                    │
                    ▼
            Respuesta JSON
            {
              "id": 4,
              "titulo": "Excelente",
              ...
              "fecha": "2026-03-20T09:01:54"
            }
                    │
                    ▼
            comercio-detalle.js (recibe respuesta)
                    │
                    ▼
            Página se refresca con nueva reseña
```

---

## 3. Estructura de Carpetas (¿Dónde está cada cosa?)

```
DetuBarrio/
├── rest/rest/                              ← Proyecto Maven (donde ejecutas mvnw)
│   ├── pom.xml                             ← Dependencias Java
│   ├── mvnw / mvnw.cmd                     ← Script para ejecutar Maven en Windows
│   │
│   ├── src/main/
│   │   ├── java/detubarrio/rest/
│   │   │   ├── RestApplication.java        ← Clase principal (@SpringBootApplication)
│   │   │   │
│   │   │   ├── controller/                 ← Reciben solicitudes HTTP
│   │   │   │   ├── ComercioController.java     (GET /api/comercios, etc)
│   │   │   │   ├── AuthController.java        (POST /api/auth/login, etc)
│   │   │   │   ├── ComentarioController.java  (POST /api/comentarios)
│   │   │   │   └── DashboardController.java   (GET /api/dashboard/*)
│   │   │   │
│   │   │   ├── service/                    ← Lógica de negocio
│   │   │   │   ├── ComercioService.java
│   │   │   │   ├── AuthService.java
│   │   │   │   └── (ResenaService si hubiera)
│   │   │   │
│   │   │   ├── repository/                 ← Acceso a BD (Spring Data JPA)
│   │   │   │   ├── ComercioRepository.java
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   ├── ResenaRepository.java
│   │   │   │   └── ProductoRepository.java
│   │   │   │
│   │   │   ├── model/                      ← Entidades JPA (tablas BD)
│   │   │   │   ├── Comercio.java
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Resena.java
│   │   │   │   ├── Producto.java
│   │   │   │   ├── Categoria.java
│   │   │   │   └── ComercioProducto.java
│   │   │   │
│   │   │   ├── dto/                        ← Contratos API (request/response)
│   │   │   │   ├── AuthLoginRequest.java   (lo que espera POST /api/auth/login)
│   │   │   │   ├── AuthResponse.java       (lo que devuelve con token JWT)
│   │   │   │   ├── ComentarioRequest.java
│   │   │   │   └── UsuarioMeResponse.java
│   │   │   │
│   │   │   ├── security/                   ← Autenticación JWT
│   │   │   │   ├── JwtService.java         (genera/valida tokens)
│   │   │   │   └── JwtAuthenticationFilter.java (intercepta solicitudes)
│   │   │   │
│   │   │   ├── config/                     ← Configuración
│   │   │   │   ├── SecurityConfig.java     (define rutas públicas/privadas)
│   │   │   │   ├── DataSeederConfig.java   (carga datos 👈 aquí están los usuarios!)
│   │   │   │   ├── CorsConfig.java         (permitir requests del frontend)
│   │   │   │   └── OpenApiConfig.java      (Swagger)
│   │   │   │
│   │   │   └── exception/
│   │   │       └── GlobalExceptionHandler.java (manejo centralizado de errores)
│   │   │
│   │   └── resources/
│   │       ├── application.properties           (config por defecto, MySQL)
│   │       ├── application-local.properties    (config local, H2 en RAM)
│   │       │
│   │       └── static/                         ← 👈 FRONTEND AQUÍ
│   │           ├── login_db.html
│   │           ├── comercio_individual.html
│   │           ├── gestion_usuario.html
│   │           ├── gestion_comercio.html
│   │           ├── js/
│   │           │   ├── auth.js               (login/register)
│   │           │   ├── comercio-detalle.js   (POST reseña)
│   │           │   ├── dashboard-usuario.js
│   │           │   └── dashboard-comercio.js
│   │           ├── css/
│   │           ├── images/
│   │           └── ...
│   │
│   └── target/                             ← Generado al compilar (JAR, bytecode)
│
├── Script_corregido.sql                    ← Script SQL de referencia (MySQL 8+)
└── ARQUITECTURA.md                         ← Este archivo
```

---

## 4. Stack Tecnológico Explicado

| Capa | Tecnología | Para Qué |
|------|-----------|----------|
| **BD** | H2 (desarrollo) / MySQL (producción) | Almacenar comercios, productos, reseñas, usuarios |
| **Acceso datos** | Spring Data JPA | Mapear tablas → objetos Java (sin SQL manual) |
| **Lógica negocio** | Servicios Java | Validar, procesar, coordinar datos |
| **API REST** | Spring Web | Servir JSON en /api/* |
| **Seguridad** | Spring Security + JWT | Login/register, tokens, proteger endpoints |
| **Frontend** | HTML + Bootstrap + JavaScript vanilla | Interfaz usuario, formularios |
| **Documentación API** | Swagger/OpenAPI | Visualizar y probar endpoints en /swagger-ui.html |

---

## 5. Flujo de Autenticación (Login)

```
1. Usuario abre http://localhost:8080/login_db.html
                                        ↓
2. Rellena email y contraseña, click "Login"
                                        ↓
3. auth.js hace POST /api/auth/login
   Body: { "email": "ana@detubarrio.local", "password": "123456" }
                                        ↓
4. AuthController recibe y pasa a AuthService
                                        ↓
5. AuthService:
   a. Busca usuario en BD por email
   b. Compara contraseña (BCrypt)
   c. Si coincide, crea JWT token
   d. Devuelve: { "token": "eyJ...", "rol": "USUARIO" }
                                        ↓
6. JavaScript guarda token en localStorage
                                        ↓
7. Próximas solicitudes:
   Header: Authorization: Bearer eyJ...
                                        ↓
8. JwtAuthenticationFilter intercepta, valida token
   Si es válido → permite acceso
   Si no → devuelve 401 Unauthorized
```

---

## 6. Rutas Públicas vs Protegidas

```
PÚBLICAS (no necesitan token):
  ✓ GET  /                        (home)
  ✓ GET  /login_db.html           (página login)
  ✓ GET  /api/health              (comprobar servidor)
  ✓ GET  /api/comercios           (listar tiendas)
  ✓ POST /api/auth/login          (login)
  ✓ POST /api/auth/register       (registro)
  ✓ POST /api/comentarios         (crear reseña sin login)
  ✓ GET  /swagger-ui.html         (documentación)

PROTEGIDAS (requieren JWT válido):
  🔒 GET  /api/auth/me                   (datos usuario actual)
  🔒 GET  /api/dashboard/usuario         (panel usuario)
  🔒 GET  /api/dashboard/comercio/{id}   (panel comercio)
  🔒 POST /api/comercios/{id}/productos  (añadir producto)
```

---

## 7. Para la Presentación: Explicación de 5 Minutos

### Slide 1: "¿Qué es DeTuBarrio?"
> Plataforma digital para apoyar comercios locales de barrio. Permite:
> - Catálogo de tiendas y productos
> - Reseñas y valoraciones
> - Autenticación de usuarios y comercios
> - Dashboards personalizados por rol

### Slide 2: "Arquitectura - Monolítica"
```
Un servidor Spring Boot sirve:
  → Frontend estático (HTML/CSS/JS) en /
  → Backend API REST en /api
  → Documentación Swagger en /swagger-ui.html
```

### Slide 3: "Cómo Funciona"
1. Usuario abre navegador → recibe HTML/CSS/JS
2. Hace clic en botón → JavaScript hace fetch a /api/*
3. Backend procesa, consulta BD, devuelve JSON
4. JavaScript actualiza la página

### Slide 4: "Stack"
- **Backend:** Java 21, Spring Boot 3, Spring Security, JWT
- **BD:** H2 (desarrollo), MySQL (producción)
- **Frontend:** HTML, Bootstrap 5, JavaScript vanilla
- **Documentación:** Swagger/OpenAPI

### Slide 5: "Demo"
1. Mostrar http://localhost:8080/login_db.html → Login con `ana@detubarrio.local`
2. Mostrar dashboard usuario
3. Ir a tienda → Postear reseña
4. Mostrar http://localhost:8080/swagger-ui.html → Listar endpoints

---

## 8. Decisiones de Diseño y Por Qué

| Decisión | Razón |
|----------|-------|
| **Monolítico vs Microservicios** | Es un TFG, monolítico es rápido de desplegar sin complejidad |
| **H2 en desarrollo** | No requiere servidor MySQL instalado, datos se recrE

an limpios cada inicio |
| **JWT sin sesiones** | Stateless, escalable, sin necesidad de BD de sesiones |
| **DTOs (no entidades directas)** | Separar modelo BD de contrato API, flexibilidad |
| **Spring Data JPA** | Menos SQL manual, más productividad |
| **Frontend integrado en static/** | Deployment simple, un único JAR/servidor |
| **Bootstrap en lugar de custom CSS** | Prototipado rápido, responsive sin esfuerzo |

---

## 9. Cómo Explicar Cada Componente

**Si te preguntan por ComercioService:**
> "Contiene la lógica de negocio. Por ejemplo, cuando alguien lista tiendas, ComercioService calcula la puntuación media sumando todas las reseñas, y esto es más eficiente que hacerlo en la BD o en JavaScript."

**Si te preguntan por DTOs:**
> "No devolví la entidad JPA directamente por seguridad. Un DTO (Data Transfer Object) es un contrato que define exactamente qué campos envío al cliente, evitando exponer campos sensibles."

**Si te preguntan por JWT:**
> "Después de login, genero un token JWT cifrado que el cliente almacena. En cada solicitud protegida, el cliente envía el token en el header Authorization, y el servidor lo valida sin consultar la BD; es stateless y escalable."

**Si te preguntan por H2:**
> "En desarrollo uso H2 (en RAM) para no depender de MySQL. Cada vez que reinicio, tengo datos limpios. Si fuera producción, cambiaría a MySQL con conexión persistente."

---

## 10. Próximas Mejoras (Si Te Preguntan)

1. **Tests de integración** - Validar endpoints con RestAssured
2. **Paginación** - `/api/comercios?page=0&size=10`
3. **Búsqueda avanzada** - Filtrar por categoría, nombre, precio
4. **Carrito de compra** - Nueva tabla `Compra` con líneas
5. **Imagen en S3** - En lugar de `/images/` local
6. **Dockerización** - `docker build` y `docker run`

---

**Última cosa:** cuando presentes, abre dos ventanas:
- Ventana 1: Terminal con `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"`
- Ventana 2: Navegador en http://localhost:8080

Así demuestras que el servidor arranca limpio, datos se cargan, y el frontend funciona en vivo. 🎯
