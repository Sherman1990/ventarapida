package pe.edu.cibertec.ventarapida.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.cibertec.ventarapida.entity.Categoria;
import pe.edu.cibertec.ventarapida.repository.CategoriaRepository;

// @Service le dice a Spring: "esta clase contiene lógica de negocio,
// regístrala como un componente para poder inyectarla donde se necesite"
@Service
public class CategoriaService {

    // @Autowired le pide a Spring que inyecte automáticamente
    // una instancia de CategoriaRepository aquí. No necesitas
    // escribir "new CategoriaRepository()" — Spring lo gestiona por ti.
    @Autowired
    private CategoriaRepository categoriaRepository;

    // Lista todas las categorías
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    // Busca una categoría por id. Devuelve null si no existe
    // (más adelante en el Controller decidiremos qué hacer si es null)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    // Guarda una categoría nueva o actualiza una existente.
    // JPA decide automáticamente: si el id es null, hace INSERT;
    // si el id ya existe, hace UPDATE.
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    // Elimina una categoría por su id
    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }

    // REGLA DE NEGOCIO: verifica si ya existe una categoría con ese nombre
    // antes de permitir guardar una nueva (evita duplicados)
    public boolean existeNombre(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }
}