package pe.edu.cibertec.ventarapida.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.cibertec.ventarapida.entity.Cliente;
import pe.edu.cibertec.ventarapida.entity.DetalleVenta;
import pe.edu.cibertec.ventarapida.entity.Producto;
import pe.edu.cibertec.ventarapida.entity.Usuario;
import pe.edu.cibertec.ventarapida.entity.Venta;
import pe.edu.cibertec.ventarapida.repository.VentaRepository;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    // Necesitamos ProductoService para validar y reducir el stock
    @Autowired
    private ProductoService productoService;

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    public List<Venta> listarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return ventaRepository.findByFechaBetween(inicio, fin);
    }

    public List<Venta> listarPorCliente(Long idCliente) {
        return ventaRepository.findByClienteId(idCliente);
    }

    // ===== EL CASO DE USO TRANSACCIONAL PRINCIPAL =====
    //
    // @Transactional es la anotación CLAVE de este método.
    // Le dice a Spring: "todas las operaciones de base de datos dentro
    // de este método deben ejecutarse como una sola unidad atómica".
    //
    // Esto significa: si CUALQUIER parte falla (por ejemplo, no hay
    // stock suficiente de un producto a mitad del proceso), Spring
    // automáticamente revierte (ROLLBACK) TODO lo que se había hecho
    // hasta ese momento — ningún dato queda guardado a medias.
    //
    // Sin @Transactional, si fallara después de guardar 2 de 3 productos,
    // tendrías una venta inconsistente en la base de datos.
    @Transactional
    public Venta registrarVenta(Cliente cliente, Usuario usuario, List<DetalleVenta> detalles) {

        // 1. Crear la cabecera de la venta
        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setEstado("COMPLETADA");

        BigDecimal totalVenta = BigDecimal.ZERO;

        // 2. Procesar cada línea de detalle
        for (DetalleVenta detalle : detalles) {
            Producto producto = detalle.getProducto();

            // Validar y reducir el stock ANTES de confirmar la venta.
            // Si no hay stock suficiente, este método lanza una excepción
            // y @Transactional revierte automáticamente todo lo anterior.
            productoService.reducirStock(producto.getIdProducto(), detalle.getCantidad());

            // Calcular el subtotal de esta línea
            BigDecimal subtotal = detalle.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);

            totalVenta = totalVenta.add(subtotal);

            // Conecta el detalle con la venta (relación bidireccional)
            venta.agregarDetalle(detalle);
        }

        venta.setTotal(totalVenta);

        // 3. Guardar la venta. Gracias a CascadeType.ALL en la Entity Venta,
        // al guardar la venta, JPA guarda AUTOMÁTICAMENTE todos sus detalles también
        // — no necesitas guardar cada DetalleVenta por separado.
        return ventaRepository.save(venta);
    }

    public BigDecimal totalVentasPorFecha(LocalDate fecha) {
        return ventaRepository.sumTotalPorFecha(fecha);
    }
}