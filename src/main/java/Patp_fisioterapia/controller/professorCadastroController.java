package Patp_fisioterapia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import Patp_fisioterapia.dto.ProfessorCadastroRespostaDTO;
import Patp_fisioterapia.service.professorCadastroService;

@Controller
public class professorCadastroController {

    private final professorCadastroService service;

    public professorCadastroController(
            professorCadastroService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    public String cadastrarProfessor(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam int idEspecialidade,
            Model model) {

        ProfessorCadastroRespostaDTO resultado = service.cadastrar(
                nome,
                email,
                senha,
                idEspecialidade
        );

        model.addAttribute("mensagem", resultado.getMensagem());

        if (resultado.getIdProfessor() > 0) {
            model.addAttribute(
                    "idProfessor",
                    resultado.getIdProfessor()
            );

            return "login";
        }

        model.addAttribute("erro", resultado.getMensagem());

        return "index";
    }
}