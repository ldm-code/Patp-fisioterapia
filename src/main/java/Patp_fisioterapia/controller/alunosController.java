
package Patp_fisioterapia.controller;

import Patp_fisioterapia.service.AlunoService;
import Patp_fisioterapia.dto.AlunoDTO;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class alunosController {

    private final AlunoService alunoService = new AlunoService();

    // ========================================
    // VERIFICAR PERMISSÕES
    // ========================================

    private boolean usuarioAutenticado(HttpSession session) {
        return session.getAttribute("usuarioLogado") != null;
    }

    private boolean usuarioCoordenador(HttpSession session) {
        return "coordenador".equals(
                session.getAttribute("tipoUsuario")
        );
    }

    private boolean usuarioProfessor(HttpSession session) {
        return "comum".equals(
                session.getAttribute("tipoUsuario")
        );
    }

    // ========================================
    // LISTAR TODOS OS ALUNOS - COORDENADOR
    // ========================================

    @GetMapping
    public ResponseEntity<?> selecionarTodosAlunos(
            HttpSession session) {

        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Faça login para continuar.");
        }

        if (!usuarioCoordenador(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas o coordenador pode listar todos os alunos.");
        }

        return ResponseEntity.ok(
                alunoService.listarAlunosService()
        );
    }

    // ========================================
    // BUSCAR ESPECIALIDADE DO PROFESSOR
    // ========================================

    @GetMapping("/professor/especialidade")
    public ResponseEntity<?> buscarEspecialidadeProfessor(
            HttpSession session) {

        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Faça login para continuar.");
        }

        if (!usuarioProfessor(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas professores podem acessar esta consulta.");
        }

        Object id = session.getAttribute("idProfessor");

        if (!(id instanceof Integer)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Sessão de professor inválida.");
        }

        Integer idProfessor = (Integer) id;

        Integer idEspecialidade =
                alunoService.buscarEspecialidadeProfessor(idProfessor);

        return ResponseEntity.ok(idEspecialidade);
    }

    // ========================================
    // LISTAR ALUNOS DO PROFESSOR
    // ========================================

    @GetMapping("/professor")
    public ResponseEntity<?> listarAlunosPorProfessor(
            @RequestParam(required = false) Integer idEspecialidade,
            HttpSession session) {

        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Faça login para continuar.");
        }

        if (!usuarioProfessor(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas professores podem acessar esta consulta.");
        }

        Object id = session.getAttribute("idProfessor");

        if (!(id instanceof Integer)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Sessão de professor inválida.");
        }

        Integer idProfessor = (Integer) id;

        List<AlunoDTO> alunos =
                alunoService.listarAlunosPorProfessor(
                        idProfessor,
                        idEspecialidade
                );

        return ResponseEntity.ok(alunos);
    }

    // ========================================
    // FILTRAR ALUNOS DO PROFESSOR POR E-MAIL
    // ========================================

    @GetMapping("/professor/email")
    public ResponseEntity<?> listarAlunosPorProfessorEmail(
            @RequestParam String email,
            @RequestParam(required = false) Integer idEspecialidade,
            HttpSession session) {

        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Faça login para continuar.");
        }

        if (!usuarioProfessor(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas professores podem acessar esta consulta.");
        }

        Object id = session.getAttribute("idProfessor");

        if (!(id instanceof Integer)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Sessão de professor inválida.");
        }

        Integer idProfessor = (Integer) id;

        List<AlunoDTO> alunos =
                alunoService.selecionarPorEmailProfessor(
                        email,
                        idProfessor,
                        idEspecialidade
                );

        return ResponseEntity.ok(alunos);
    }

    // ========================================
    // BUSCAR ALUNO POR E-MAIL - COORDENADOR
    // ========================================

    @GetMapping("/email")
    public ResponseEntity<?> selecionarPorEmail(
            @RequestParam String email,
            HttpSession session) {

        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Faça login para continuar.");
        }

        if (!usuarioCoordenador(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas o coordenador pode realizar esta consulta.");
        }

        return ResponseEntity.ok(
                alunoService.selecionarPorEmail(email)
        );
    }

    // ========================================
    // CADASTRAR ALUNO - COORDENADOR
    // ========================================

    @PostMapping("/cadastrarAlunos")
    public ResponseEntity<String> cadastrarAluno(
            @RequestBody AlunoDTO aluno,
            HttpSession session) {

        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Faça login para continuar.");
        }

        if (!usuarioCoordenador(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas o coordenador pode cadastrar alunos.");
        }

        boolean cadastrado = alunoService.cadastrarAluno(aluno);

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
    // BUSCAR ALUNO PARA EDIÇÃO - COORDENADOR
    // ========================================

    @GetMapping("/editar/{id}")
    public ResponseEntity<?> buscarAlunoParaEdicao(
            @PathVariable int id,
            HttpSession session) {

        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Faça login para continuar.");
        }

        if (!usuarioCoordenador(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas o coordenador pode editar alunos.");
        }

        AlunoDTO aluno = alunoService.buscarAlunoParaEdicao(id);

        if (aluno == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(aluno);
    }

    // ========================================
    // ATUALIZAR ALUNO - COORDENADOR
    // ========================================

    @PutMapping("/editar")
    public ResponseEntity<String> atualizarAluno(
            @RequestBody AlunoDTO aluno,
            HttpSession session) {

        if (!usuarioAutenticado(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Faça login para continuar.");
        }

        if (!usuarioCoordenador(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas o coordenador pode editar alunos.");
        }

        boolean atualizado = alunoService.atualizarAluno(aluno);

        if (atualizado) {
            return ResponseEntity.ok(
                    "Aluno atualizado com sucesso."
            );
        }

        return ResponseEntity.badRequest()
                .body("Não foi possível atualizar o aluno.");
    }
}
