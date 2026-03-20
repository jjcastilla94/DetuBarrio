package detubarrio.rest.service;

import detubarrio.rest.dto.ComercioDetailResponse;
import detubarrio.rest.dto.ComercioRequest;
import detubarrio.rest.dto.ComercioSummaryResponse;
import detubarrio.rest.dto.ProductoComercioRequest;
import detubarrio.rest.dto.ProductoComercioResponse;
import detubarrio.rest.dto.ResenaResponse;
import detubarrio.rest.model.Categoria;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.ComercioProducto;
import detubarrio.rest.model.Producto;
import detubarrio.rest.repository.CategoriaRepository;
import detubarrio.rest.repository.ComercioProductoRepository;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.ProductoRepository;
import detubarrio.rest.repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComercioService {

    @Autowired
    private ComercioRepository comercioRepository;

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private ComercioProductoRepository comercioProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ComercioSummaryResponse> listarComercios(Optional<Long> categoriaId) {
        List<Comercio> comercios = categoriaId
                .map(comercioRepository::findByCategoriaId)
                .orElseGet(comercioRepository::findAll);

        return comercios.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComercioDetailResponse obtenerComercio(Long comercioId) {
        Comercio comercio = comercioRepository.findById(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));
        return toDetailResponse(comercio);
    }

    public ComercioSummaryResponse crearComercio(ComercioRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                
        Comercio comercio = Comercio.builder()
                .nombreComercio(request.nombreComercio())
                .descripcion(request.descripcion())
                .horario(request.horario())
                .diasApertura(request.diasApertura())
                .logo(request.logo())
                .banner(request.banner())
                .categoria(categoria)
                .build();
                
        comercio = comercioRepository.save(comercio);
        return toSummaryResponse(comercio);
    }

    @Transactional(readOnly = true)
    public List<ProductoComercioResponse> obtenerProductosComercio(Long comercioId) {
        Comercio comercio = comercioRepository.findById(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));
                
        return comercio.getComercioProductos().stream()
                .map(cp -> new ProductoComercioResponse(
                        cp.getProducto().getId(),
                        cp.getProducto().getNombreProducto(),
                        cp.getProducto().getDescripcion(),
                        cp.getPrecio(),
                        cp.getStock(),
                        cp.getProducto().getImagen()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoComercioResponse agregarProductoAComercio(Long comercioId, ProductoComercioRequest request) {
        Comercio comercio = comercioRepository.findById(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));
                
        Producto producto = Producto.builder()
                .nombreProducto(request.nombreProducto())
                .descripcion(request.descripcion())
                .imagen(request.imagen())
                .build();
        producto = productoRepository.save(producto);
        
        ComercioProducto cp = ComercioProducto.builder()
                .comercio(comercio)
                .producto(producto)
                .precio(request.precio())
                .stock(request.stock())
                .build();
        cp = comercioProductoRepository.save(cp);
        
        return new ProductoComercioResponse(
                cp.getProducto().getId(),
                cp.getProducto().getNombreProducto(),
                cp.getProducto().getDescripcion(),
                cp.getPrecio(),
                cp.getStock(),
                cp.getProducto().getImagen()
        );
    }

    private ComercioSummaryResponse toSummaryResponse(Comercio comercio) {
        Double puntuacionMedia = resenaRepository.findAverageValoracionByComercioId(comercio.getId());
        Long totalResenas = resenaRepository.countByComercioId(comercio.getId());

        return new ComercioSummaryResponse(
                comercio.getId(),
                comercio.getNombreComercio(),
                comercio.getDescripcion(),
                comercio.getHorario(),
                comercio.getDiasApertura(),
                comercio.getLogo(),
                comercio.getCategoria().getNombreCategoria(),
                puntuacionMedia != null ? puntuacionMedia : 0.0,
                totalResenas != null ? totalResenas : 0L
        );
    }

    private ComercioDetailResponse toDetailResponse(Comercio comercio) {
        Double puntuacionMedia = resenaRepository.findAverageValoracionByComercioId(comercio.getId());
        Long totalResenas = resenaRepository.countByComercioId(comercio.getId());

        List<ProductoComercioResponse> productos = comercio.getComercioProductos().stream()
                .map(cp -> new ProductoComercioResponse(
                        cp.getProducto().getId(),
                        cp.getProducto().getNombreProducto(),
                        cp.getProducto().getDescripcion(),
                        cp.getPrecio(),
                        cp.getStock(),
                        cp.getProducto().getImagen()
                ))
                .collect(Collectors.toList());

        List<ResenaResponse> resenas = comercio.getResenas().stream()
                .map(r -> new ResenaResponse(
                        r.getId(),
                        r.getTitulo(),
                        r.getComentario(),
                        r.getValoracion(),
                        r.getAutorNombre(),
                        r.getAutorEmail(),
                        r.getFecha()
                ))
                .collect(Collectors.toList());

        return new ComercioDetailResponse(
                comercio.getId(),
                comercio.getNombreComercio(),
                comercio.getDescripcion(),
                comercio.getHorario(),
                comercio.getDiasApertura(),
                comercio.getLogo(),
                comercio.getBanner(),
                comercio.getCategoria().getNombreCategoria(),
                puntuacionMedia != null ? puntuacionMedia : 0.0,
                totalResenas != null ? totalResenas : 0L,
                productos,
                resenas
        );
    }
}
