package pe.edu.cibertec.ventarapida.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import pe.edu.cibertec.ventarapida.entity.Categoria;
import pe.edu.cibertec.ventarapida.service.CategoriaService;

@Controller
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/categorias")
    public String listar(Model model) {
        List<Categoria> categorias = categoriaService.listarTodas();
        model.addAttribute("categorias", categorias);
        return "categorias/list";
    }

    @GetMapping("/categorias/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/form";
    }

    @GetMapping("/categorias/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id);
        if (categoria == null) {
            return "redirect:/categorias";
        }
        model.addAttribute("categoria", categoria);
        return "categorias/form";
    }

    @PostMapping("/categorias/guardar")
    public String guardar(@ModelAttribute Categoria categoria, Model model) {
        if (categoria.getIdCategoria() == null && categoriaService.existeNombre(categoria.getNombre())) {
            model.addAttribute("categoria", categoria);
            model.addAttribute("error", "Ya existe una categoría con ese nombre.");
            return "categorias/form";
        }
        categoriaService.guardar(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/categorias/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return "redirect:/categorias";
    }
}