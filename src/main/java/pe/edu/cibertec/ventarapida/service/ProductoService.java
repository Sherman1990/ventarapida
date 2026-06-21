package pe.edu.cibertec.ventarapida.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.cibertec.ventarapida.entity.Producto;
import pe.edu.cibertec.ventarapida.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    // En vez de eliminar físicamente un producto (que podría romper
    // el historial de ventas pasadas), lo desactivamos.
    // Esta es una práctica común llamada "soft delete" (borrado lógico).
    public void desactivar(Long id) {
        Producto producto = buscarPorId(id);
        if (producto != null) {
            producto.setActivo(false);
            productoRepository.save(producto);
        }
    }

    public boolean existeCodigo(String codigo) {
        return productoRepository.existsByCodigo(codigo);
    }

    // Reporte: productos con stock bajo el mínimo configurado
    public List<Producto> listarStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    public List<Producto> buscarPorNombre(String texto) {
        return productoRepository.buscarPorNombre(texto);
    }

    // ===== REGLA DE NEGOCIO IMPORTANTE =====
    // Reduce el stock de un producto cuando se vende.
    // Lanza una excepción si no hay stock suficiente, evitando
    // que se registre una venta imposible de cumplir.
    public void reducirStock(Long idProducto, Integer cantidad) {
        Producto producto = buscarPorId(idProducto);
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }
        if (producto.getStock() < cantidad) {
            throw new RuntimeException(
                "Stock insuficiente para el producto: " + producto.getNombre()
            );
        }
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }
}