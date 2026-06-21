package pe.edu.cibertec.ventarapida.controller;

import java.beans.PropertyEditorSupport;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.cibertec.ventarapida.entity.Categoria;
import pe.edu.cibertec.ventarapida.entity.Producto;
import pe.edu.cibertec.ventarapida.entity.Proveedor;
import pe.edu.cibertec.ventarapida.service.CategoriaService;
import pe.edu.cibertec.ventarapida.service.ProductoService;
import pe.edu.cibertec.ventarapida.service.ProveedorService;

@Controller
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Necesitamos estos dos para llenar los <select> del formulario
    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProveedorService proveedorService;

    // ===== CONVERSIÓN DE ID -> OBJETO COMPLETO =====
    // Cuando el formulario envía categoria=3 y proveedor=1 (solo el ID,
    // como texto), Spring necesita saber cómo convertir ese texto en el
    // objeto Categoria/Proveedor real antes de armar el Producto completo.
    // Sin esto, Spring lanzaría un error de conversión al hacer guardar().
    @InitBinder("producto")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Categoria.class,
            new PropertyEditorSupport() {
                @Override
                public void setAsText(String id) {
                    setValue(categoriaService.buscarPorId(Long.valueOf(id)));
                }
            });
        binder.registerCustomEditor(Proveedor.class,
            new PropertyEditorSupport() {
                @Override
                public void setAsText(String id) {
                    setValue(proveedorService.buscarPorId(Long.valueOf(id)));
                }
            });
    }

    @GetMapping("/productos")
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "productos/list";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        cargarListasDesplegables(model);
        return "productos/form";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            return "redirect:/productos";
        }
        model.addAttribute("producto", producto);
        cargarListasDesplegables(model);
        return "productos/form";
    }

    @PostMapping("/productos/guardar")
    public String guardar(@ModelAttribute Producto producto, Model model) {
        // Regla de negocio: código único, solo se valida al CREAR
        if (producto.getIdProducto() == null && productoService.existeCodigo(producto.getCodigo())) {
            model.addAttribute("producto", producto);
            model.addAttribute("error", "Ya existe un producto con ese código.");
            cargarListasDesplegables(model);
            return "productos/form";
        }
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    // Soft delete: desactiva en vez de eliminar físicamente
    // (tal como diseñaste en ProductoService, para no romper ventas pasadas)
    @GetMapping("/productos/desactivar/{id}")
    public String desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
        return "redirect:/productos";
    }

    // Búsqueda por nombre desde la barra de búsqueda del listado
    @GetMapping("/productos/buscar")
    public String buscar(@RequestParam(required = false) String texto, Model model) {
        if (texto == null || texto.isBlank()) {
            model.addAttribute("productos", productoService.listarTodos());
        } else {
            model.addAttribute("productos", productoService.buscarPorNombre(texto));
        }
        model.addAttribute("textoBusqueda", texto);
        return "productos/list";
    }

    private void cargarListasDesplegables(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("proveedores", proveedorService.listarTodos());
    }
}