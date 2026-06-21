package pe.edu.cibertec.ventarapida.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.cibertec.ventarapida.entity.Usuario;
import pe.edu.cibertec.ventarapida.service.UsuarioService;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro/guardar")
    public String registrar(@ModelAttribute Usuario usuario,
                             @RequestParam String passwordPlano,
                             @RequestParam String confirmarPassword,
                             Model model) {

        if (usuarioService.existeUsername(usuario.getUsername())) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "Ese nombre de usuario ya está en uso.");
            return "registro";
        }
        if (usuario.getEmail() != null && !usuario.getEmail().isBlank()
                && usuarioService.existeEmail(usuario.getEmail())) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "Ese email ya está registrado.");
            return "registro";
        }
        if (passwordPlano == null || passwordPlano.length() < 6) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            return "registro";
        }
        if (!passwordPlano.equals(confirmarPassword)) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }

        usuario.setPassword(passwordEncoder.encode(passwordPlano));
        // Por seguridad, el auto-registro nunca crea un ADMIN.
        // Solo otro ADMIN puede ascender a alguien desde /usuarios.
        usuario.setRol("VENDEDOR");
        usuario.setActivo(true);

        usuarioService.guardar(usuario);
        return "redirect:/login?registrado=true";
    }
}