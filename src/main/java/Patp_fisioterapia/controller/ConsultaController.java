
package Patp_fisioterapia.controller;
import java.time.LocalDate;
import Patp_fisioterapia.dto.AlunoDTO;
import Patp_fisioterapia.dto.ConsultaDTO;
import Patp_fisioterapia.dto.pacienteDto;
import Patp_fisioterapia.service.ConsultaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService =
            new ConsultaService();


    @PostMapping
    public ResponseEntity<String> cadastrar(
            @RequestBody ConsultaDTO consulta,
            HttpSession session) {

        if (!isCoordenador(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado.");
        }

        String mensagem =
                consultaService.cadastrar(consulta);

        if (mensagem.equals(
                "Consulta cadastrada com sucesso.")) {

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mensagem);
        }

        return ResponseEntity
                .badRequest()
                .body(mensagem);
    }


    @GetMapping
    public ResponseEntity<?> listar(
            HttpSession session) {

        if (!isCoordenador(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado.");
        }

        return ResponseEntity.ok(
                consultaService.listar()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable int id,
            HttpSession session) {

        if (!isCoordenador(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado.");
        }

        ConsultaDTO consulta =
                consultaService.buscarPorId(id);

        if (consulta == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(consulta);
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(
            @PathVariable int id,
            @RequestBody ConsultaDTO consulta,
            HttpSession session) {

        if (!isCoordenador(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado.");
        }

        consulta.setId(id);

        String mensagem =
                consultaService.atualizar(consulta);

        if (mensagem.equals(
                "Consulta atualizada com sucesso.")) {

            return ResponseEntity.ok(mensagem);
        }

        return ResponseEntity
                .badRequest()
                .body(mensagem);
    }


    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelar(
            @PathVariable int id,
            HttpSession session) {

        if (!isCoordenador(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado.");
        }

        String mensagem =
                consultaService.cancelar(id);

        if (mensagem.equals(
                "Consulta cancelada com sucesso.")) {

            return ResponseEntity.ok(mensagem);
        }

        return ResponseEntity
                .badRequest()
                .body(mensagem);
    }


    @PutMapping("/{id}/concluir")
    public ResponseEntity<String> concluir(
            @PathVariable int id,
            HttpSession session) {

        if (!isCoordenador(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado.");
        }

        String mensagem =
                consultaService.concluir(id);

        if (mensagem.equals(
                "Consulta concluída com sucesso.")) {

            return ResponseEntity.ok(mensagem);
        }

        return ResponseEntity
                .badRequest()
                .body(mensagem);
    }


    @GetMapping("/alunos/busca")
    public ResponseEntity<?> buscarAlunos(
            @RequestParam String nome,
            HttpSession session) {

        if (!isCoordenador(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado.");
        }

        List<AlunoDTO> alunos =
                consultaService.buscarAlunos(nome);

        return ResponseEntity.ok(alunos);
    }


    @GetMapping("/pacientes/busca")
    public ResponseEntity<?> buscarPacientes(
            @RequestParam String nome,
            HttpSession session) {

        if (!isCoordenador(session)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado.");
        }

        List<pacienteDto> pacientes =
                consultaService.buscarPacientes(nome);

        return ResponseEntity.ok(pacientes);
    }


@GetMapping("/horarios/aluno/{idAluno}")
public ResponseEntity<?> buscarHorarios(
        @PathVariable int idAluno,
        @RequestParam String data,
        HttpSession session) {

    if (!isCoordenador(session)) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
    }

    try {

        LocalDate dataConsulta =
                LocalDate.parse(data);

        List<String> horarios =
                consultaService.buscarHorariosDisponiveis(
                        idAluno,
                        dataConsulta
                );

        return ResponseEntity.ok(horarios);

    } catch (Exception e) {

        return ResponseEntity
                .badRequest()
                .body("Data inválida.");
    }
}



    private boolean isCoordenador(HttpSession session) {

        Object tipoUsuario =
                session.getAttribute("tipoUsuario");

        return tipoUsuario != null &&
                "coordenador".equals(
                        tipoUsuario.toString()
                );
    }
    @GetMapping("/aluno/agendadas")
public ResponseEntity<?> listarAgendadasAluno(
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

    int idAluno =
            Integer.parseInt(idAlunoSession.toString());

    return ResponseEntity.ok(
            consultaService.listarAgendadasPorAluno(idAluno)
    );
}
}