package pe.edu.cibertec.ventarapida.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.edu.cibertec.ventarapida.security.UsuarioDetails;
import pe.edu.cibertec.ventarapida.service.CategoriaService;
import pe.edu.cibertec.ventarapida.service.ClienteService;
import pe.edu.cibertec.ventarapida.service.ProductoService;
import pe.edu.cibertec.ventarapida.service.VentaService;

@Controller
public class HomeController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VentaService ventaService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UsuarioDetails usuarioDetails) {
        model.addAttribute("totalCategorias", categoriaService.listarTodas().size());
        model.addAttribute("totalProductos", productoService.listarTodos().size());
        model.addAttribute("totalClientes", clienteService.listarTodos().size());
        model.addAttribute("totalVentas", ventaService.listarTodas().size());
        model.addAttribute("productosStockBajo", productoService.listarStockBajo().size());

        // Para mostrar "Bienvenido, Mahali" y saber si es ADMIN (mostrar u ocultar el módulo Usuarios)
        model.addAttribute("nombreUsuario", usuarioDetails.getUsuario().getNombre());
        model.addAttribute("esAdmin", usuarioDetails.getUsuario().getRol().equals("ADMIN"));

        return "index";
    }
}