package pe.edu.cibertec.ventarapida.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.ventarapida.entity.Categoria;

// @Repository le indica a Spring que esta interfaz es un componente
// de acceso a datos, para que pueda inyectarla automáticamente
// donde la necesitemos (por ejemplo, en un Service)
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Método personalizado: Spring Data JPA lee el NOMBRE del método
    // y genera el SQL automáticamente, sin que tú escribas nada.
    // "existsByNombre" se traduce a:
    // SELECT COUNT(*) > 0 FROM categoria WHERE nombre = ?
    boolean existsByNombre(String nombre);
}