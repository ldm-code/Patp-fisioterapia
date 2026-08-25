package Patp_fisioterapia.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import Patp_fisioterapia.dto.LoginRequest;

@Controller
public class loginController {
    @GetMapping({"/start", "/login"})
    public String home(){
          return "login";
    }
          
    @PostMapping("/login")
    @ResponseBody
    public String processarLogin(@ModelAttribute LoginRequest loginData, Model model) {
        // Acessando os dados recebidos
        String email = loginData.getEmail();
        String senha = loginData.getSenha();

        // Exemplo de lógica simples (apenas para teste)
        if ("admin@email.com".equals(email) && "1234".equals(senha)) {
            return "sucesso"; // Redireciona para templates/sucesso.html
        }
        
        // Se errar, envia mensagem de erro de volta para a tela
        model.addAttribute("erro", "E-mail ou senha inválidos!");
        return "login"; // Recarrega a tela templates/login.html
    }
}


