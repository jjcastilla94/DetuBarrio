package detubarrio.rest.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import detubarrio.rest.dto.CategoriaRequest;
import detubarrio.rest.dto.CategoriaResponse;
import detubarrio.rest.model.Categoria;
import detubarrio.rest.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponse> listarCategorias() {
        return categoriaRepository.findAll().stream()
            .map(categoria -> new CategoriaResponse(categoria.getId(), categoria.getNombreCategoria(), categoria.getDescripcion()))
            .toList();
    }

    @Transactional
    public CategoriaResponse crearCategoria(CategoriaRequest request) {
        Categoria categoria = Categoria.builder()
            .nombreCategoria(request.nombreCategoria())
            .descripcion(request.descripcion())
            .build();

        Categoria guardada = categoriaRepository.save(categoria);
        return new CategoriaResponse(guardada.getId(), guardada.getNombreCategoria(), guardada.getDescripcion());
    }
}
