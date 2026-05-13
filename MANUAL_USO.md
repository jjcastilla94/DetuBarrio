# Manual de uso DeTuBarrio

## 1. Objetivo

Este manual describe como utilizar DeTuBarrio en entorno de desarrollo para pruebas funcionales y demo.

## 2. Modos de uso del frontend

- Frontend principal: Vue (`a/vue`)
- Frontend legacy: HTML estatico (`a/html`)

Recomendacion: usar Vue como referencia principal de demo y evolucion funcional.

## 3. Arranque de la aplicacion

### 3.1 Backend

```powershell
cd rest/rest
.\mvnw.cmd spring-boot:run
```

### 3.2 Frontend Vue

```powershell
cd a/vue
npm install
npm run dev
```

URLs:

- Frontend Vue: http://localhost:5173
- Backend API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

## 4. Perfiles de usuario

La plataforma trabaja con tres roles:

- USUARIO: consulta y consumo
- COMERCIO: gestion de cuenta/comercio
- ADMIN: gestion de solicitudes y supervisión

## 5. Funciones disponibles

### 5.1 Funciones publicas

- ver categorias
- listar comercios
- ver detalle de comercio
- enviar solicitud de contacto/colaboracion
- registro y login

### 5.2 Funciones con autenticacion

USUARIO:

- ver su dashboard (`/api/dashboard/usuario`)
- consultar su perfil (`/api/auth/me`)

COMERCIO:

- ver dashboard de comercio (`/api/dashboard/comercio`)
- eliminar cuenta de comercio rechazada (`DELETE /api/dashboard/comercio`)

ADMIN:

- listar comercios pendientes
- aprobar o rechazar comercios
- revisar mensajes de contacto
- aprobar/rechazar colaboraciones

## 6. Flujos funcionales recomendados para demo

### Flujo A: navegacion publica

1. abrir home
2. entrar a listado de comercios
3. abrir detalle de comercio
4. revisar productos y reseñas

### Flujo B: autenticacion

1. ir a login
2. iniciar sesion
3. acceder a mi-cuenta
4. validar redireccion por rol

### Flujo C: administracion

1. login con cuenta ADMIN
2. abrir panel admin
3. revisar solicitudes de comercios
4. aprobar/rechazar y validar estado

### Flujo D: contacto

1. enviar mensaje desde contacto
2. revisar en panel admin de contacto

## 7. Pruebas rapidas por API (smoke test)

```powershell
Invoke-RestMethod "http://localhost:8080/api/health" | ConvertTo-Json -Compress
Invoke-RestMethod "http://localhost:8080/api/comercios" | ConvertTo-Json -Depth 5
```

Login:

```powershell
$loginBody = @{ email='admin@detubarrio.local'; password='admin123' } | ConvertTo-Json
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -ContentType "application/json" -Body $loginBody
$login | ConvertTo-Json -Compress
```

Endpoint protegido:

```powershell
$headers = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Headers $headers | ConvertTo-Json -Compress
```

## 8. Casos de error habituales

401 Unauthorized:

- token ausente o invalido
- sesion expirada

403 Forbidden:

- rol insuficiente para endpoint admin

500 en arranque:

- problema de conexion a DB
- conflicto de migraciones Flyway

## 9. Checklist de operacion diaria

1. backend arriba en 8080
2. frontend Vue arriba en 5173
3. health endpoint responde ok
4. login funciona
5. flujo principal de comercios responde

## 10. Alcance actual y siguientes mejoras

Estado actual:

- base funcional de autenticacion, catalogo, reseñas, contacto y administracion
- documentacion tecnica y operativa unificada

Siguientes mejoras sugeridas:

1. tests de integracion automatizados
2. endurecimiento de seguridad para produccion
3. trazabilidad de auditoria (acciones admin)
4. mejoras de accesibilidad y usabilidad
