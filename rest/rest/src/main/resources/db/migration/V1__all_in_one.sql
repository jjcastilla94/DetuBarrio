-- Script corregido de base de datos para DeTuBarrio
-- Motor objetivo: MySQL 8+

CREATE TABLE persona (
    id_persona BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(150),
    ciudad VARCHAR(100),
    codigo_postal VARCHAR(10),
    foto_perfil VARCHAR(255)
);

CREATE TABLE usuario (
    id_usuario BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'USUARIO', 'COMERCIO') NOT NULL,
    id_comercio BIGINT NULL
);

CREATE TABLE trabajador (
    id_trabajador BIGINT PRIMARY KEY AUTO_INCREMENT,
    horario VARCHAR(100),
    num_horas_semanales INT,
    hora_entrada TIME,
    hora_salida TIME,
    cargo VARCHAR(80),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    id_persona BIGINT NOT NULL,
    CONSTRAINT fk_trabajador_persona FOREIGN KEY (id_persona) REFERENCES persona(id_persona)
);

CREATE TABLE contrato (
    id_contrato BIGINT PRIMARY KEY AUTO_INCREMENT,
    fecha_alta DATETIME NOT NULL,
    salario DECIMAL(10,2) NOT NULL,
    tipo_contrato VARCHAR(100),
    fecha_fin DATETIME,
    id_trabajador BIGINT NOT NULL,
    CONSTRAINT fk_contrato_trabajador FOREIGN KEY (id_trabajador) REFERENCES trabajador(id_trabajador)
);

CREATE TABLE categoria (
    id_categoria BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_categoria VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE comercio (
    id_comercio BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_comercio VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    horario VARCHAR(100),
    dias_apertura VARCHAR(100),
    logo VARCHAR(255),
    banner VARCHAR(255),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    gestion_autorizada BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_solicitud DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo_rechazo VARCHAR(500),
    motivo_bloqueo_gestion VARCHAR(500),
    id_usuario_creador BIGINT,
    id_categoria BIGINT NOT NULL,
    CONSTRAINT fk_comercio_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    CONSTRAINT fk_comercio_usuario_creador FOREIGN KEY (id_usuario_creador) REFERENCES usuario(id_usuario)
);

CREATE INDEX idx_comercio_estado ON comercio(estado);

CREATE TABLE estadisticas (
    id_estadistica BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero_visitas INT NOT NULL DEFAULT 0,
    puntuacion_media DECIMAL(3,2),
    total_ventas INT NOT NULL DEFAULT 0,
    id_comercio BIGINT NOT NULL,
    CONSTRAINT fk_estadisticas_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio)
);

CREATE TABLE cliente (
    id_cliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    ultimo_acceso DATETIME,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    nivel ENUM('NORMAL', 'PRO') NOT NULL DEFAULT 'NORMAL',
    id_persona BIGINT NOT NULL,
    CONSTRAINT fk_cliente_persona FOREIGN KEY (id_persona) REFERENCES persona(id_persona)
);

CREATE TABLE servicio (
    id_servicio BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_servicio ENUM('RESERVA', 'COMPRA') NOT NULL DEFAULT 'RESERVA',
    descripcion VARCHAR(255),
    fecha DATETIME,
    precio DECIMAL(10,2),
    metodo_pago ENUM('TARJETA', 'BIZUM'),
    id_cliente BIGINT NOT NULL,
    CONSTRAINT fk_servicio_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE reserva (
    id_reserva BIGINT PRIMARY KEY AUTO_INCREMENT,
    estado_reserva ENUM('PENDIENTE', 'PROCESO', 'FINALIZADO') NOT NULL,
    id_servicio BIGINT NOT NULL,
    CONSTRAINT fk_reserva_servicio FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio)
);

CREATE TABLE compra (
    id_compra BIGINT PRIMARY KEY AUTO_INCREMENT,
    estado_compra ENUM('PENDIENTE', 'REPARTO', 'FINALIZADO') NOT NULL,
    id_servicio BIGINT NOT NULL,
    CONSTRAINT fk_compra_servicio FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio)
);

CREATE TABLE producto (
    id_producto BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_producto VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    imagen VARCHAR(255)
);

CREATE TABLE mensaje_contacto (
    id_mensaje_contacto BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    asunto VARCHAR(120) NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    mensaje VARCHAR(2000) NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE solicitud_colaboracion (
    id_solicitud_colaboracion BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre_comercio VARCHAR(120) NOT NULL,
    nombre_titular VARCHAR(120) NOT NULL,
    email_comercio VARCHAR(150) NOT NULL,
    telefono_comercio VARCHAR(30) NOT NULL,
    categoria VARCHAR(80) NOT NULL,
    descripcion VARCHAR(3000),
    id_comercio_origen BIGINT,
    estado ENUM('PENDIENTE', 'APROBADA', 'RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    motivo_rechazo VARCHAR(500),
    fecha_resolucion DATETIME,
    terminos_aceptados BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE resena (
    id_resena BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(80) NOT NULL,
    comentario VARCHAR(255),
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valoracion INT NOT NULL,
    autor_nombre VARCHAR(100) NOT NULL,
    autor_email VARCHAR(150),
    id_cliente BIGINT,
    id_comercio BIGINT NOT NULL,
    CONSTRAINT chk_valoracion CHECK (valoracion BETWEEN 1 AND 5),
    CONSTRAINT fk_resena_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_resena_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio)
);

CREATE TABLE trabajador_comercio (
    id_trabajador BIGINT NOT NULL,
    id_comercio BIGINT NOT NULL,
    PRIMARY KEY (id_trabajador, id_comercio),
    CONSTRAINT fk_trabajador_comercio_trabajador FOREIGN KEY (id_trabajador) REFERENCES trabajador(id_trabajador),
    CONSTRAINT fk_trabajador_comercio_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio)
);

CREATE TABLE cliente_favoritos_comercio (
    id_cliente BIGINT NOT NULL,
    id_comercio BIGINT NOT NULL,
    PRIMARY KEY (id_cliente, id_comercio),
    CONSTRAINT fk_favorito_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_favorito_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio)
);

CREATE TABLE compra_producto (
    id_compra_producto BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_compra BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    precio_unidad DECIMAL(10,2) NOT NULL,
    cantidad INT NOT NULL,
    hora DATETIME,
    CONSTRAINT fk_compra_producto_compra FOREIGN KEY (id_compra) REFERENCES compra(id_compra),
    CONSTRAINT fk_compra_producto_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE comercio_producto (
    id_comercio_producto BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_comercio BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    precio DECIMAL(10,2) NOT NULL,
    CONSTRAINT uk_comercio_producto UNIQUE (id_comercio, id_producto),
    CONSTRAINT fk_comercio_producto_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio),
    CONSTRAINT fk_comercio_producto_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

INSERT INTO categoria (id_categoria, nombre_categoria, descripcion) VALUES
    (1, 'Hosteleria', 'Bares, cafeterias y restaurantes'),
    (2, 'Panaderia', 'Pan y bolleria artesanal'),
    (3, 'Ferreteria', 'Bricolaje y hogar')
;

INSERT INTO usuario (id_usuario, nombre, email, password_hash, rol, id_comercio) VALUES
    (1, 'Admin DeTuBarrio', 'admin@detubarrio.local', '$2a$10$7Qx1eE2y3Zq9h1Gf7kV/eOqKf1z9Yc8b0Lq1YvK6bS9uJ2c3d4eFG', 'ADMIN', NULL),
    (2, 'Ana Garcia', 'ana@detubarrio.local', '$2a$10$7Qx1eE2y3Zq9h1Gf7kV/eOqKf1z9Yc8b0Lq1YvK6bS9uJ2c3d4eFG', 'USUARIO', NULL),
    (3, 'Panaderia El Trigal', 'trigal@detubarrio.local', '$2a$10$7Qx1eE2y3Zq9h1Gf7kV/eOqKf1z9Yc8b0Lq1YvK6bS9uJ2c3d4eFG', 'COMERCIO', 1)
;

INSERT INTO comercio (
    id_comercio,
    nombre_comercio,
    descripcion,
    horario,
    dias_apertura,
    logo,
    banner,
    estado,
    gestion_autorizada,
    fecha_solicitud,
    motivo_rechazo,
    motivo_bloqueo_gestion,
    id_usuario_creador,
    id_categoria
) VALUES (
    1,
    'Panaderia El Trigal',
    'Panaderia familiar con masa madre y bolleria artesanal',
    'L-S 08:00 - 20:00',
    'Lunes a Sabado',
    'images/trigal.png',
    'images/panaderia.png',
    'APROBADO',
    TRUE,
    CURRENT_TIMESTAMP,
    NULL,
    NULL,
    1,
    2
);

ALTER TABLE usuario
    ADD CONSTRAINT fk_usuario_comercio FOREIGN KEY (id_comercio) REFERENCES comercio(id_comercio);

INSERT INTO producto (id_producto, nombre_producto, descripcion, imagen) VALUES
    (1, 'Pan de Masa Madre', 'Hogaza de fermentacion lenta', 'images/pan.png'),
    (2, 'Croissant de Mantequilla', 'Croissant artesanal', 'images/croissant.png'),
    (3, 'Menu del Dia', 'Primer plato, segundo y postre', 'images/menu.png'),
    (4, 'Kit Basico Bricolaje', 'Set de herramientas para casa', 'images/herramientas.png')
;

INSERT INTO comercio_producto (id_comercio_producto, id_comercio, id_producto, stock, precio) VALUES
    (1, 1, 1, 20, 3.50),
    (2, 1, 2, 40, 1.80),
    (3, 1, 3, 50, 14.90),
    (4, 1, 4, 15, 29.99)
;

INSERT INTO resena (id_resena, titulo, comentario, fecha, valoracion, autor_nombre, autor_email, id_cliente, id_comercio) VALUES
    (1, 'Excelente trato', 'Muy buena calidad y servicio rapido', CURRENT_TIMESTAMP, 5, 'Ana Garcia', 'ana@example.com', NULL, 1),
    (2, 'Todo perfecto', 'Pan recien hecho y muy rico', CURRENT_TIMESTAMP, 4, 'Carlos Ruiz', 'carlos@example.com', NULL, 1),
    (3, 'Recomendado', 'Comida casera con buen precio', CURRENT_TIMESTAMP, 5, 'Lucia M', 'lucia@example.com', NULL, 1)
;

INSERT INTO mensaje_contacto (id_mensaje_contacto, nombre, email, asunto, tipo, mensaje, fecha_creacion) VALUES
    (1, 'Pablo Lopez', 'pablo@example.com', 'Consulta general', 'INFORMACION', 'Quiero saber como dar de alta mi comercio.', CURRENT_TIMESTAMP)
;

INSERT INTO solicitud_colaboracion (
    id_solicitud_colaboracion,
    nombre_comercio,
    nombre_titular,
    email_comercio,
    telefono_comercio,
    categoria,
    descripcion,
    id_comercio_origen,
    estado,
    motivo_rechazo,
    fecha_resolucion,
    terminos_aceptados,
    fecha_creacion
) VALUES (
    1,
    'Taller Barrio Norte',
    'Marta Ruiz',
    'marta@tallernorte.local',
    '600123123',
    'Ferreteria',
    'Solicitud para colaborar y publicar productos del taller.',
    1,
    'PENDIENTE',
    NULL,
    NULL,
    TRUE,
    CURRENT_TIMESTAMP
);