package Patp_fisioterapia.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import Patp_fisioterapia.dto.LoginRequest;
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

    boolean autenticado = service.autenticar(email, senha);

    if (autenticado) {

        session.setAttribute("usuarioLogado", email);
        session.setAttribute("tipoUsuario", "professor");

        return "pacientes";
    }

    model.addAttribute("erro", "E-mail ou senha inválidos!");
    return "login";
}
}


