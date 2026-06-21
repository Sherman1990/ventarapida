package pe.edu.cibertec.ventarapida.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.cibertec.ventarapida.entity.Cliente;
import pe.edu.cibertec.ventarapida.entity.DetalleVenta;
import pe.edu.cibertec.ventarapida.entity.Producto;
import pe.edu.cibertec.ventarapida.entity.Usuario;
import pe.edu.cibertec.ventarapida.entity.Venta;
import pe.edu.cibertec.ventarapida.security.UsuarioDetails;
import pe.edu.cibertec.ventarapida.service.ClienteService;
import pe.edu.cibertec.ventarapida.service.ProductoService;
import pe.edu.cibertec.ventarapida.service.VentaService;

@Controller
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProductoService productoService;

    // Lista el historial de ventas
    @GetMapping("/ventas")
    public String listar(Model model) {
        model.addAttribute("ventas", ventaService.listarTodas());
        return "ventas/list";
    }

    // Muestra el detalle de una venta específica (qué productos llevó)
    @GetMapping("/ventas/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Venta venta = ventaService.buscarPorId(id);
        if (venta == null) {
            return "redirect:/ventas";
        }
        model.addAttribute("venta", venta);
        return "ventas/detalle";
    }

    // Muestra el formulario de "Nueva venta": selector de cliente
    // y catálogo de productos activos para armar el carrito
    @GetMapping("/ventas/nueva")
    public String nuevaVentaFormulario(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("productos", productoService.listarActivos());
        return "ventas/nueva";
    }

    // Procesa la venta completa: recibe el cliente elegido y arrays
    // paralelos con los productos/cantidades que el usuario armó en el carrito
    @PostMapping("/ventas/registrar")
    public String registrar(@RequestParam Long idCliente,
                             @RequestParam(required = false) Long[] idProducto,
                             @RequestParam(required = false) Integer[] cantidad,
                             @AuthenticationPrincipal UsuarioDetails usuarioDetails,
                             Model model) {

        Cliente cliente = clienteService.buscarPorId(idCliente);
        Usuario usuario = usuarioDetails.getUsuario(); // el usuario logueado, registrado como vendedor

        if (idProducto == null || idProducto.length == 0) {
            model.addAttribute("clientes", clienteService.listarTodos());
            model.addAttribute("productos", productoService.listarActivos());
            model.addAttribute("error", "Debes agregar al menos un producto a la venta.");
            return "ventas/nueva";
        }

        List<DetalleVenta> detalles = new ArrayList<>();
        for (int i = 0; i < idProducto.length; i++) {
            Producto producto = productoService.buscarPorId(idProducto[i]);
            if (producto == null || cantidad[i] == null || cantidad[i] <= 0) {
                continue; // ignora filas vacías o inválidas del carrito
            }
            DetalleVenta detalle = new DetalleVenta(cantidad[i], producto.getPrecio(), producto);
            detalles.add(detalle);
        }

        try {
            // Aquí se ejecuta TODA la lógica transaccional que ya construiste
            // en VentaService: valida stock, reduce stock, calcula totales,
            // y si algo falla, Spring revierte todo automáticamente
            Venta ventaRegistrada = ventaService.registrarVenta(cliente, usuario, detalles);
            return "redirect:/ventas/" + ventaRegistrada.getIdVenta();
        } catch (RuntimeException e) {
            // Captura los errores que lanza productoService.reducirStock()
            // (ej: "Stock insuficiente para el producto: ...")
            model.addAttribute("clientes", clienteService.listarTodos());
            model.addAttribute("productos", productoService.listarActivos());
            model.addAttribute("error", e.getMessage());
            return "ventas/nueva";
        }
    }
}