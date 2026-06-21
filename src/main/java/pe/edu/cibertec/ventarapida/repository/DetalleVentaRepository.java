package pe.edu.cibertec.ventarapida.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.ventarapida.entity.DetalleVenta;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    // Lista todos los detalles de una venta específica
    @Query("SELECT d FROM DetalleVenta d WHERE d.venta.idVenta = :idVenta")
    List<DetalleVenta> findByVentaId(Long idVenta);

    // Consulta para el reporte de productos más vendidos:
    // agrupa por producto y suma las cantidades vendidas,
    // ordenado de mayor a menor
    @Query("SELECT d.producto.nombre, SUM(d.cantidad) " +
           "FROM DetalleVenta d " +
           "GROUP BY d.producto.nombre " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> findProductosMasVendidos();
}