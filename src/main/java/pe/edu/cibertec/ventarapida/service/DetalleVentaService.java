package pe.edu.cibertec.ventarapida.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.cibertec.ventarapida.entity.DetalleVenta;
import pe.edu.cibertec.ventarapida.repository.DetalleVentaRepository;

@Service
public class DetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    public List<DetalleVenta> listarPorVenta(Long idVenta) {
        return detalleVentaRepository.findByVentaId(idVenta);
    }

    // Para el reporte de productos más vendidos.
    // Devuelve List<Object[]> porque la consulta agrega
    // dos valores distintos (nombre + suma de cantidad), no una Entity completa
    public List<Object[]> listarProductosMasVendidos() {
        return detalleVentaRepository.findProductosMasVendidos();
    }
}