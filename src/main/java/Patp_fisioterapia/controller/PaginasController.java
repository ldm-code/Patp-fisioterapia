package Patp_fisioterapia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class PaginasController {
    @GetMapping("/alunos/cadastrarAlunos")
    public String cadastroAlunos() {
        return "alunosCadastro";
    }
    @GetMapping("/horarios/cadastrar")
    public String telaCadastroHorarios() {
        return "horariosCadastro";
    }
    
    
    @GetMapping("/pagina/alunos")
    public String alunos(){
        return "alunos";
    }
    @GetMapping("/pagina/cadastro")
    public String cadastro() {
        return "cadastroPacientes";
    }
    @GetMapping("/editarPacientes")
    public String editarPacientes() {
        return "editarPacientes";
    }
    @GetMapping("/cadastroPacientes")
    public String cadastroPacientes() {
        return "cadastroPacientes";
    }
    @GetMapping("/pagina/pacientes")
    public String pacientes() {
        return "pacientes";
    }
    
} 
          

