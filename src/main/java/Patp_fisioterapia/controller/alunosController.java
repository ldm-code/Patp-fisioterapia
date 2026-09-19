package Patp_fisioterapia.controller;
import Patp_fisioterapia.service.AlunoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Patp_fisioterapia.dto.AlunoDTO;
import jakarta.servlet.http.HttpSession;

import java.util.List;


@RestController
@RequestMapping("/alunos")
public class alunosController {
          private AlunoService alunoService=new AlunoService();
          @GetMapping
          public List<AlunoDTO> selecionarTodosAlunos(){
              return alunoService.listarAlunosService();
          }@GetMapping("/professor")
        public ResponseEntity<List<AlunoDTO>> listarAlunosPorProfessor(
                @RequestParam int idProfessor) {

            List<AlunoDTO> alunos =
                    alunoService.listarAlunosPorProfessor(idProfessor);

            return ResponseEntity.ok(alunos);
}
@GetMapping("/professor/email")
public ResponseEntity<List<AlunoDTO>> selecionarPorEmailProfessor(
        @RequestParam String email,
        @RequestParam int idProfessor) {

    List<AlunoDTO> alunos =
            alunoService.selecionarPorEmailProfessor(
                email, idProfessor
            );

    return ResponseEntity.ok(alunos);
}
          @GetMapping("/email")
          public List<AlunoDTO> selecionarPorEmail(@RequestParam String email) {

                    return alunoService.selecionarPorEmail(email);

          }
          @PostMapping("/cadastrarAlunos")
          public ResponseEntity<String> cadastrarAluno(
          @RequestBody AlunoDTO aluno) {

          boolean cadastrado =
                    alunoService.cadastrarAluno(aluno);

          if (cadastrado) {

          return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Aluno cadastrado com sucesso.");
          }

          return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível cadastrar o aluno.");
          }



          
          
}
