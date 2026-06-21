package pe.edu.cibertec.ventarapida.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.cibertec.ventarapida.entity.Usuario;
import pe.edu.cibertec.ventarapida.service.UsuarioService;

@Controller
public class PasswordRecoveryController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/recuperar-password")
    public String mostrarFormulario() {
        return "recuperar-password";
    }

    // Paso 1: el usuario confirma su identidad con username + email
    @PostMapping("/recuperar-password/verificar")
    public String verificar(@RequestParam String username, @RequestParam String email, Model model) {
        Usuario usuario = usuarioService.buscarPorUsername(username);

        if (usuario == null || usuario.getEmail() == null || !usuario.getEmail().equalsIgnoreCase(email)) {
            model.addAttribute("error", "No encontramos un usuario con ese username y email juntos.");
            return "recuperar-password";
        }

        model.addAttribute("usernameVerificado", username);
        return "recuperar-password";
    }

    // Paso 2: ya verificado, define la nueva contraseña
    @PostMapping("/recuperar-password/cambiar")
    public String cambiar(@RequestParam String username,
                           @RequestParam String nuevaPassword,
                           @RequestParam String confirmarPassword,
                           Model model) {

        Usuario usuario = usuarioService.buscarPorUsername(username);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "recuperar-password";
        }
        if (nuevaPassword == null || nuevaPassword.length() < 6) {
            model.addAttribute("usernameVerificado", username);
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            return "recuperar-password";
        }
        if (!nuevaPassword.equals(confirmarPassword)) {
            model.addAttribute("usernameVerificado", username);
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "recuperar-password";
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioService.guardar(usuario);

        return "redirect:/login?passwordCambiada=true";
    }
}