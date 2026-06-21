package pe.edu.cibertec.ventarapida.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import pe.edu.cibertec.ventarapida.entity.Cliente;
import pe.edu.cibertec.ventarapida.service.ClienteService;

@Controller
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/clientes")
    public String listar(Model model) {
        List<Cliente> clientes = clienteService.listarTodos();
        model.addAttribute("clientes", clientes);
        return "clientes/list";
    }

    @GetMapping("/clientes/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            return "redirect:/clientes";
        }
        model.addAttribute("cliente", cliente);
        return "clientes/form";
    }

    @PostMapping("/clientes/guardar")
    public String guardar(@ModelAttribute Cliente cliente, Model model) {
        // Reglas de negocio: DNI y email únicos, solo se validan al CREAR
        // (si estás editando el mismo cliente, su propio DNI/email no debe rechazarse)
        boolean esNuevo = cliente.getIdCliente() == null;

        if (esNuevo && clienteService.existeDni(cliente.getDni())) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("error", "Ya existe un cliente con ese DNI.");
            return "clientes/form";
        }

        if (esNuevo && cliente.getEmail() != null && !cliente.getEmail().isBlank()
                && clienteService.existeEmail(cliente.getEmail())) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("error", "Ya existe un cliente con ese email.");
            return "clientes/form";
        }

        clienteService.guardar(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/clientes/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return "redirect:/clientes";
    }
}