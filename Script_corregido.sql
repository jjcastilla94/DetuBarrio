-- Script corregido de base de datos para DeTuBarrio
-- Motor objetivo: MySQL 8+

CREATE DATABASE IF NOT EXISTS detubarrio;
USE detubarrio;

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
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    rol ENUM('ADMIN', 'BASICO') NOT NULL DEFAULT 'BASICO',
    id_persona BIGINT NOT NULL,
    CONSTRAINT fk_usuario_persona FOREIGN KEY (id_persona) REFERENCES persona(id_persona)
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
    id_categoria BIGINT NOT NULL,
    CONSTRAINT fk_comercio_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

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
    descripcion VARCHAR(255)
);

CREATE TABLE resena (
    id_resena BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(80) NOT NULL,
    comentario VARCHAR(255),
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valoracion TINYINT NOT NULL,
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
