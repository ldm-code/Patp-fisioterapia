package Patp_fisioterapia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;



@Controller
public class PaginasController {
    @GetMapping("/consultasAluno")
    public String abrirConsultasAluno() {
        return "consultasAluno";
    }
    @GetMapping("/alunos/editar")
    public String editarAluno(HttpSession session) {
        String tipoUsuario=(String) session.getAttribute("tipoUsuario");
        if (!"coordenador".equals(tipoUsuario)){
            return "redirect:/";
        }
        return "alunosEdicao";
    }
    

    @GetMapping("/alunosComum")
    public String abrirAlunosComum() {
        return "alunosComum";
}
    @GetMapping("/alunos/cadastrarAlunos")
    public String cadastroAlunos(HttpSession session) {
        String tipoUsuario=(String) session.getAttribute("tipoUsuario");
        if (!"coordenador".equals(tipoUsuario)){
            return "redirect:/";
        }
        return "alunosCadastro";
    }
    @GetMapping("/horarios/cadastrar")
    public String telaCadastroHorarios(HttpSession session) {
        String tipoUsuario=(String) session.getAttribute("tipoUsuario");
        if (!"coordenador".equals(tipoUsuario)){
            return "redirect:/";
        }
        return "horariosCadastro";
    }
    
    
    @GetMapping("/pagina/alunos")
    public String alunos(HttpSession session){
        String tipoUsuario=(String) session.getAttribute("tipoUsuario");
       
        if (!"coordenador".equals(tipoUsuario)){
            return "redirect:/";
        }
        return "alunos";
    }
    @GetMapping("/pagina/cadastro")
    public String cadastro(HttpSession session) {
        String tipoUsuario=(String) session.getAttribute("tipoUsuario");
        if (!"coordenador".equals(tipoUsuario)){
            return "redirect:/";
        }
        return "cadastroPacientes";
    }
    @GetMapping("/editarPacientes")
    public String editarPacientes(HttpSession session) {
        String tipoUsuario=(String) session.getAttribute("tipoUsuario");
        if (!"coordenador".equals(tipoUsuario)){
            return "redirect:/";
        }
        return "editarPacientes";
    }
    @GetMapping("/cadastroPacientes")
    public String cadastroPacientes(HttpSession session) {
        String tipoUsuario=(String) session.getAttribute("tipoUsuario");
        if (!"coordenador".equals(tipoUsuario)){
            return "redirect:/";
        }
        return "cadastroPacientes";
    }
    @GetMapping("/pagina/pacientes")
    public String pacientes(HttpSession session) {
        String tipoUsuario=(String) session.getAttribute("tipoUsuario");
        if (!"coordenador".equals(tipoUsuario)){
            return "redirect:/";
        }
        return "pacientes";
    }
    
} 
          

