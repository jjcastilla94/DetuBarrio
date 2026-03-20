package detubarrio.rest.config;

import detubarrio.rest.model.Categoria;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.ComercioProducto;
import detubarrio.rest.model.Producto;
import detubarrio.rest.model.RolUsuario;
import detubarrio.rest.model.Resena;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.CategoriaRepository;
import detubarrio.rest.repository.ComercioProductoRepository;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.ProductoRepository;
import detubarrio.rest.repository.ResenaRepository;
import detubarrio.rest.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataSeederConfig {

    @Bean
    public CommandLineRunner seedData(
            CategoriaRepository categoriaRepository,
            ComercioRepository comercioRepository,
            ProductoRepository productoRepository,
            ComercioProductoRepository comercioProductoRepository,
            ResenaRepository resenaRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            // Guard clause: si ya hay datos, no hacer nada
            if (categoriaRepository.count() > 0) {
                return;
            }

            // Crear Categorías
            Categoria restauracion = categoriaRepository.save(Categoria.builder()
                    .nombreCategoria("Restauracion")
                    .descripcion("Bares, cafeterias y restaurantes")
                    .build());

            Categoria panaderia = categoriaRepository.save(Categoria.builder()
                    .nombreCategoria("Panaderia")
                    .descripcion("Pan y bolleria artesanal")
                    .build());

            Categoria ferreteria = categoriaRepository.save(Categoria.builder()
                    .nombreCategoria("Ferreteria")
                    .descripcion("Bricolaje y hogar")
                    .build());

            // Crear Productos
            Producto panMasaMadre = productoRepository.save(Producto.builder()
                    .nombreProducto("Pan de Masa Madre")
                    .descripcion("Hogaza de fermentacion lenta")
                    .imagen("images/pan.png")
                    .build());

            Producto croissant = productoRepository.save(Producto.builder()
                    .nombreProducto("Croissant de Mantequilla")
                    .descripcion("Croissant artesanal")
                    .imagen("images/croissant.png")
                    .build());

            Producto menuDia = productoRepository.save(Producto.builder()
                    .nombreProducto("Menu del Dia")
                    .descripcion("Primer plato, segundo y postre")
                    .imagen("images/menu.png")
                    .build());

            Producto kitBricolaje = productoRepository.save(Producto.builder()
                    .nombreProducto("Kit Basico Bricolaje")
                    .descripcion("Set de herramientas para casa")
                    .imagen("images/herramientas.png")
                    .build());

            // Crear Comercios
            Comercio saborCasero = comercioRepository.save(Comercio.builder()
                    .nombreComercio("El Sabor Casero")
                    .descripcion("Comida tradicional con productos de proximidad")
                    .horario("L-V 09:00 - 22:00")
                    .diasApertura("Lunes a Domingo")
                    .logo("images/sabor.png")
                    .banner("images/sabor.png")
                    .categoria(restauracion)
                    .build());

            Comercio trigal = comercioRepository.save(Comercio.builder()
                    .nombreComercio("Panaderia El Trigal")
                    .descripcion("Panaderia familiar con masa madre y bolleria artesanal")
                    .horario("L-S 08:00 - 20:00")
                    .diasApertura("Lunes a Sabado")
                    .logo("images/trigal.png")
                    .banner("images/panaderia.png")
                    .categoria(panaderia)
                    .build());

            Comercio tornillo = comercioRepository.save(Comercio.builder()
                    .nombreComercio("El Tornillo Feliz")
                    .descripcion("Ferreteria de barrio para reparaciones y mejoras del hogar")
                    .horario("L-V 09:00 - 19:30")
                    .diasApertura("Lunes a Viernes")
                    .logo("images/torfeliz.png")
                    .banner("images/torfeliz.png")
                    .categoria(ferreteria)
                    .build());

            // Crear relaciones ComercioProducto
            comercioProductoRepository.save(ComercioProducto.builder()
                    .comercio(trigal)
                    .producto(panMasaMadre)
                    .precio(new BigDecimal("3.50"))
                    .stock(20)
                    .build());

            comercioProductoRepository.save(ComercioProducto.builder()
                    .comercio(trigal)
                    .producto(croissant)
                    .precio(new BigDecimal("1.80"))
                    .stock(40)
                    .build());

            comercioProductoRepository.save(ComercioProducto.builder()
                    .comercio(saborCasero)
                    .producto(menuDia)
                    .precio(new BigDecimal("14.90"))
                    .stock(50)
                    .build());

            comercioProductoRepository.save(ComercioProducto.builder()
                    .comercio(tornillo)
                    .producto(kitBricolaje)
                    .precio(new BigDecimal("29.99"))
                    .stock(15)
                    .build());

            // Crear Reseñas (Reviews)
            resenaRepository.save(Resena.builder()
                    .comercio(trigal)
                    .titulo("Excelente trato")
                    .comentario("Muy buena calidad y servicio rapido")
                    .valoracion(5)
                    .autorNombre("Ana Garcia")
                    .autorEmail("ana@example.com")
                    .fecha(LocalDateTime.now().minusDays(2))
                    .build());

            resenaRepository.save(Resena.builder()
                    .comercio(trigal)
                    .titulo("Todo perfecto")
                    .comentario("Pan recien hecho y muy rico")
                    .valoracion(4)
                    .autorNombre("Carlos Ruiz")
                    .autorEmail("carlos@example.com")
                    .fecha(LocalDateTime.now().minusDays(1))
                    .build());

            resenaRepository.save(Resena.builder()
                    .comercio(saborCasero)
                    .titulo("Recomendado")
                    .comentario("Comida casera con buen precio")
                    .valoracion(5)
                    .autorNombre("Lucia M")
                    .autorEmail("lucia@example.com")
                    .fecha(LocalDateTime.now().minusHours(10))
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .nombre("Ana Garcia")
                    .email("ana@detubarrio.local")
                    .passwordHash(passwordEncoder.encode("123456"))
                    .rol(RolUsuario.USUARIO)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .nombre("Tienda Trigal")
                    .email("trigal@detubarrio.local")
                    .passwordHash(passwordEncoder.encode("123456"))
                    .rol(RolUsuario.COMERCIO)
                    .comercio(trigal)
                    .build());
        };
    }
}
