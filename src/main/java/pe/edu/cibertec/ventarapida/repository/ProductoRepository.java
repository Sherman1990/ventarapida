package pe.edu.cibertec.ventarapida.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.ventarapida.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Verifica si ya existe un producto con ese código
    boolean existsByCodigo(String codigo);

    // Lista solo los productos activos
    // Spring Data JPA traduce "findByActivoTrue" a:
    // SELECT * FROM producto WHERE activo = true
    List<Producto> findByActivoTrue();

    // ===== CONSULTA PERSONALIZADA CON JPQL =====
    // JPQL (Java Persistence Query Language) es similar a SQL,
    // pero trabaja sobre las ENTIDADES JAVA, no directamente sobre
    // las tablas. Por eso usamos "p.stock" y "p.stockMinimo"
    // (nombres de atributos Java), no "stock" y "stock_minimo" (columnas SQL).
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo();

    // Búsqueda de productos por nombre, sin importar mayúsculas/minúsculas
    // LOWER() convierte ambos lados a minúsculas antes de comparar
    // El símbolo % es el comodín de "cualquier texto antes/después"
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Producto> buscarPorNombre(String texto);
}