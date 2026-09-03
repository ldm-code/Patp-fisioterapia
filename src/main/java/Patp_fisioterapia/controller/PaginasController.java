package Patp_fisioterapia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PaginasController {
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
          

