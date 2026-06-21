package pe.edu.cibertec.ventarapida.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pe.edu.cibertec.ventarapida.entity.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Consulta de ventas por rango de fechas
    // "Between" es una palabra clave que Spring Data JPA reconoce
    // automáticamente para generar: WHERE fecha BETWEEN ? AND ?
    List<Venta> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Consulta del historial de ventas de un cliente específico,
    // ordenado por fecha descendente (las más recientes primero)
    @Query("SELECT v FROM Venta v WHERE v.cliente.idCliente = :idCliente ORDER BY v.fecha DESC")
    List<Venta> findByClienteId(Long idCliente);

    // Suma el total de todas las ventas de una fecha específica
    // (útil para el reporte diario de ventas)
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.fecha = :fecha")
    java.math.BigDecimal sumTotalPorFecha(LocalDate fecha);
}