package pe.edu.cibertec.ventarapida.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import pe.edu.cibertec.ventarapida.entity.Usuario;
import pe.edu.cibertec.ventarapida.service.UsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // El mismo Bean que ya definiste en SecurityConfig (BCryptPasswordEncoder).
    // Spring lo inyecta aquí automáticamente porque es un @Bean registrado.
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/usuarios")
    public String listar(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/list";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("esNuevo", true);
        return "usuarios/form";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("esNuevo", false);
        return "usuarios/form";
    }

    @PostMapping("/usuarios/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                           @org.springframework.web.bind.annotation.RequestParam(required = false) String passwordPlano,
                           Model model) {

        boolean esNuevo = usuario.getIdUsuario() == null;

        if (esNuevo) {
            // Reglas de negocio: username único, solo se valida al CREAR
            if (usuarioService.existeUsername(usuario.getUsername())) {
                model.addAttribute("usuario", usuario);
                model.addAttribute("esNuevo", true);
                model.addAttribute("error", "Ya existe un usuario con ese username.");
                return "usuarios/form";
            }
            // Al crear, la contraseña es obligatoria
            if (passwordPlano == null || passwordPlano.isBlank()) {
                model.addAttribute("usuario", usuario);
                model.addAttribute("esNuevo", true);
                model.addAttribute("error", "Debes ingresar una contraseña.");
                return "usuarios/form";
            }
            usuario.setPassword(passwordEncoder.encode(passwordPlano));
        } else {
            // Al EDITAR: si el campo de contraseña se dejó vacío, conservamos
            // la contraseña hasheada que ya existía en la BD (no la sobreescribimos
            // con un valor vacío). Solo se actualiza si escribieron una nueva.
            Usuario existente = usuarioService.buscarPorId(usuario.getIdUsuario());
            if (passwordPlano == null || passwordPlano.isBlank()) {
                usuario.setPassword(existente.getPassword());
            } else {
                usuario.setPassword(passwordEncoder.encode(passwordPlano));
            }
        }

        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }
}