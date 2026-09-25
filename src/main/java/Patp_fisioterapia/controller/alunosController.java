package Patp_fisioterapia.controller;

import Patp_fisioterapia.service.AlunoService;
import Patp_fisioterapia.dto.AlunoDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/alunos")
public class alunosController {

    private AlunoService alunoService =
            new AlunoService();


    @GetMapping
    public List<AlunoDTO> selecionarTodosAlunos() {

        return alunoService.listarAlunosService();
    }


    @GetMapping("/professor")
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
                        email,
                        idProfessor
                );

        return ResponseEntity.ok(alunos);
    }


    @GetMapping("/email")
    public List<AlunoDTO> selecionarPorEmail(
            @RequestParam String email) {

        return alunoService.selecionarPorEmail(email);
    }


    // ========================================
    // CADASTRO
    // ========================================

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


    // ========================================
    // BUSCAR ALUNO PARA EDIÇÃO
    // ========================================

    @GetMapping("/editar/{id}")
    public ResponseEntity<AlunoDTO> buscarAlunoParaEdicao(
            @PathVariable int id) {

        AlunoDTO aluno =
                alunoService.buscarAlunoParaEdicao(id);

        if (aluno == null) {

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(aluno);
    }


    // ========================================
    // ATUALIZAR ALUNO
    // ========================================

    @PutMapping("/editar")
    public ResponseEntity<String> atualizarAluno(
            @RequestBody AlunoDTO aluno) {

        boolean atualizado =
                alunoService.atualizarAluno(aluno);

        if (atualizado) {

            return ResponseEntity.ok(
                    "Aluno atualizado com sucesso."
            );
        }

        return ResponseEntity
                .badRequest()
                .body("Não foi possível atualizar o aluno.");
    }
}