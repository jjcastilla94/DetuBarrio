# Guia de instalacion de base de datos en Aiven

Esta guia describe como conectar DeTuBarrio a MySQL en Aiven para trabajo colaborativo.

## 1. Crear servicio MySQL

1. Entra en https://aiven.io
2. Crea proyecto
3. Crea servicio MySQL
4. En la seccion de conexion copia:
   - host
   - port
   - database
   - user
   - password

## 2. Preparar entorno local del backend

Desde la raiz del repositorio:

```powershell
cd rest/rest
Copy-Item .env.example .env
```

Edita `rest/rest/.env` con tus valores de Aiven:

```env
DB_URL=jdbc:mysql://TU_HOST:TU_PORT/TU_DATABASE?sslMode=REQUIRED&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=TU_USER
DB_PASSWORD=TU_PASSWORD
APP_JWT_SECRET=TU_SECRETO_JWT_LARGO_Y_ALEATORIO
APP_JWT_EXPIRATION=86400
```

Generar secreto JWT en PowerShell:

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

## 3. Arranque y validacion

Arrancar backend:

```powershell
cd rest/rest
.\mvnw.cmd spring-boot:run
```

Validar endpoints:

- http://localhost:8080/api/health
- http://localhost:8080/swagger-ui.html

## 4. Trabajo en equipo

Para que dos desarrolladores compartan sesiones correctamente:

- misma base de datos remota
- mismo `APP_JWT_SECRET`
- misma configuracion de timezone en la JDBC URL

## 5. Buenas practicas

- No subir `rest/rest/.env` a Git
- Mantener solo `rest/rest/.env.example` en versionado
- Hacer backup antes de cambios grandes:

```bash
mysqldump -h HOST -u USER -p DATABASE > backup.sql
```

Restaurar:

```bash
mysql -h HOST -u USER -p DATABASE < backup.sql
```

## 6. Resolucion de problemas

Error de validacion de esquema al arrancar:

- revisar que la migracion Flyway aplicada corresponde al codigo actual
- no editar migraciones historicas ya ejecutadas en un entorno compartido
- para cambios de esquema nuevos, crear `V2__...sql`, `V3__...sql`, etc.

Error de acceso denegado:

- revisar usuario/password
- revisar que la IP/red puede llegar al host de Aiven
- confirmar `sslMode=REQUIRED` en `DB_URL`

Frontend no conecta con backend en local:

- verificar backend en puerto 8080
- verificar frontend Vue en 5173
- verificar proxy de Vite (`/api` -> `http://localhost:8080`)
