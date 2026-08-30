package Patp_fisioterapia.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import Patp_fisioterapia.service.professorCadastroService;

@Controller
public class professorCadastroController {
          private final professorCadastroService service;
          public professorCadastroController(professorCadastroService service){
                    this.service=service;
          }
          @PostMapping("/cadastro")
          public String cadastrarProfessor(@RequestParam String nome,
                    @RequestParam String email,
                    @RequestParam String senha) {
          String resultado=service.cadastrar(nome, email, senha);
          if (resultado.equals("Professor cadastrado com sucesso")) {
            return "inicial";
        }

        return "cadastro";

          }
          
}
