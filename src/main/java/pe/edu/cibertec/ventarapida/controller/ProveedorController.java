package pe.edu.cibertec.ventarapida.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import pe.edu.cibertec.ventarapida.entity.Proveedor;
import pe.edu.cibertec.ventarapida.service.ProveedorService;

@Controller
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping("/proveedores")
    public String listar(Model model) {
        List<Proveedor> proveedores = proveedorService.listarTodos();
        model.addAttribute("proveedores", proveedores);
        return "proveedores/list";
    }

    @GetMapping("/proveedores/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores/form";
    }

    @GetMapping("/proveedores/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Proveedor proveedor = proveedorService.buscarPorId(id);
        if (proveedor == null) {
            return "redirect:/proveedores";
        }
        model.addAttribute("proveedor", proveedor);
        return "proveedores/form";
    }

    @PostMapping("/proveedores/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor, Model model) {
        // Regla de negocio: RUC único, solo se valida al CREAR
        if (proveedor.getIdProveedor() == null && proveedorService.existeRuc(proveedor.getRuc())) {
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("error", "Ya existe un proveedor con ese RUC.");
            return "proveedores/form";
        }
        proveedorService.guardar(proveedor);
        return "redirect:/proveedores";
    }

    @PostMapping("/proveedores/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return "redirect:/proveedores";
    }
}