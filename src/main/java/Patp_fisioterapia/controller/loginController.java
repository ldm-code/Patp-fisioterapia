package Patp_fisioterapia.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import Patp_fisioterapia.dto.LoginRequest;
import Patp_fisioterapia.dto.UsuarioAutenticadoDTO;
import Patp_fisioterapia.service.loginService;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class loginController {
     private final loginService service;

    public loginController(loginService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home(){
          return "login";
    }
    @GetMapping("/index")
    public String openIndex() {
        return "index";
    }
    
          
 @PostMapping("/login")
public String processarLogin(
        @ModelAttribute LoginRequest loginData,
        Model model,
        HttpSession session) {

    String email = loginData.getEmail();
    String senha = loginData.getSenha();

    UsuarioAutenticadoDTO usuario =
            service.autenticar(email, senha);

    if (usuario == null) {
        model.addAttribute(
                "erro",
                "E-mail ou senha inválidos!"
        );

        return "login";
    }

    session.setAttribute(
            "usuarioLogado",
            usuario.getEmail()
    );

    session.setAttribute(
            "tipoUsuario",
            usuario.getTipo()
    );
    
    if ("aluno".equals(usuario.getTipo())) {
        return "consultasAluno";
    }
    
    if ("comum".equals(usuario.getTipo())) {
        session.setAttribute(
        "idProfessor",
        usuario.getId()
    );
     model.addAttribute(
        "idProfessor",
        usuario.getId()
    );
        return "alunosComum";
    }

    if ("coordenador".equals(usuario.getTipo())) {
        return "pacientes";
    }

    model.addAttribute(
            "erro",
            "Tipo de usuário não reconhecido."
    );

    session.invalidate();

    return "login";
}
}


