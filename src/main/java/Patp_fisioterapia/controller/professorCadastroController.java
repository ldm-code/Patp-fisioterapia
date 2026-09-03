package Patp_fisioterapia.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import Patp_fisioterapia.service.professorCadastroService;
import org.springframework.ui.Model;

@Controller
public class professorCadastroController {
          private final professorCadastroService service;
          public professorCadastroController(professorCadastroService service){
                    this.service=service;
          }
        @PostMapping("/cadastro")
        public String cadastrarProfessor(
                @RequestParam String nome,
                @RequestParam String email,
                @RequestParam String senha,
                Model model) {

            String resultado = service.cadastrar(nome, email, senha);

            if (resultado.equals("Professor cadastrado com sucesso")) {
                return "pacientes";
            }

            model.addAttribute("erro", resultado);
            return "index";
}

        
          
}
