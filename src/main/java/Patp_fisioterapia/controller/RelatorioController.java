package Patp_fisioterapia.controller;

import Patp_fisioterapia.dto.RelatorioDTO;
import Patp_fisioterapia.service.RelatorioService;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService =
            new RelatorioService();


    @PostMapping
    public ResponseEntity<String> cadastrar(
            @RequestBody RelatorioDTO relatorio,
            HttpSession session) {

        Object tipoUsuario =
                session.getAttribute("tipoUsuario");

        if (tipoUsuario == null ||
                !"aluno".equals(tipoUsuario.toString())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado.");
        }

        Object idAlunoSession =
                session.getAttribute("idAluno");

        if (idAlunoSession == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Aluno não identificado.");
        }

        int idAluno;

        try {

            idAluno =
                    Integer.parseInt(
                            idAlunoSession.toString()
                    );

        } catch (NumberFormatException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Aluno não identificado.");
        }

        String mensagem =
                relatorioService.cadastrar(
                        relatorio,
                        idAluno
                );

        if (mensagem.equals(
                "Relatório cadastrado com sucesso.")) {

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mensagem);
        }

        return ResponseEntity
                .badRequest()
                .body(mensagem);
    }


    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<?> buscarPorConsulta(
            @PathVariable int consultaId,
            HttpSession session) {

        Object tipoUsuario =
                session.getAttribute("tipoUsuario");

        if (tipoUsuario == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não identificado.");
        }

        RelatorioDTO relatorio =
                relatorioService.buscarPorConsulta(
                        consultaId
                );

        if (relatorio == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(relatorio);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<String> atualizarStatus(
            @PathVariable int id,
            @RequestParam String status,
            HttpSession session) {

        Object tipoUsuario =
                session.getAttribute("tipoUsuario");

        if (tipoUsuario == null ||
                "aluno".equals(
                        tipoUsuario.toString())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado.");
        }

        String mensagem =
                relatorioService.atualizarStatus(
                        id,
                        status
                );

        if (mensagem.equals(
                "Status do relatório atualizado com sucesso.")) {

            return ResponseEntity.ok(mensagem);
        }

        return ResponseEntity
                .badRequest()
                .body(mensagem);
    }
    @PutMapping("/{id}")
public ResponseEntity<String> atualizar(
        @PathVariable int id,
        @RequestBody RelatorioDTO relatorio,
        HttpSession session) {

    Object tipoUsuario =
            session.getAttribute("tipoUsuario");

    if (tipoUsuario == null ||
            !"aluno".equals(
                    tipoUsuario.toString())) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
    }

    Object idAlunoSession =
            session.getAttribute("idAluno");

    if (idAlunoSession == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Aluno não identificado.");
    }

    int idAluno;

    try {

        idAluno = Integer.parseInt(
                idAlunoSession.toString()
        );

    } catch (NumberFormatException e) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Aluno não identificado.");
    }

    String mensagem =
            relatorioService.atualizarDescricao(
                    id,
                    relatorio.getDescricao(),
                    idAluno
            );

    if (mensagem.equals(
            "Relatório atualizado com sucesso.")) {

        return ResponseEntity.ok(mensagem);
    }

    return ResponseEntity
            .badRequest()
            .body(mensagem);
}
@GetMapping("/aluno")
public ResponseEntity<?> listarRelatoriosAluno(
        HttpSession session) {

    Object tipoUsuario =
            session.getAttribute("tipoUsuario");

    if (tipoUsuario == null ||
            !"aluno".equals(tipoUsuario.toString())) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
    }

    Object idAlunoSession =
            session.getAttribute("idAluno");

    if (idAlunoSession == null) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Aluno não identificado.");
    }

    int idAluno;

    try {

        idAluno = Integer.parseInt(
                idAlunoSession.toString()
        );

    } catch (NumberFormatException e) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Aluno não identificado.");
    }

    return ResponseEntity.ok(
            relatorioService.listarPorAluno(idAluno)
    );
}
@GetMapping("/coordenador")
public ResponseEntity<?> listarRelatoriosCoordenador(
        @RequestParam(required = false) String email,
        HttpSession session) {

    Object tipoUsuario =
            session.getAttribute("tipoUsuario");

    if (tipoUsuario == null ||
            !"coordenador".equals(tipoUsuario.toString())) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
    }

    List<RelatorioDTO> relatorios =
            relatorioService.listarPorCoordenador(email);

    return ResponseEntity.ok(relatorios);
}
}
