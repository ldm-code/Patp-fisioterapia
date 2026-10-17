
package Patp_fisioterapia.controller;
import java.time.LocalDate;


import Patp_fisioterapia.dto.AlunoDTO;
import Patp_fisioterapia.dto.ConsultaDTO;
import Patp_fisioterapia.dto.pacienteDto;
import Patp_fisioterapia.service.ConsultaService;

import jakarta.servlet.http.HttpSession;
import Patp_fisioterapia.service.RelatorioExcelService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService =
            new ConsultaService();
    private final RelatorioExcelService relatorioExcelService =
        new RelatorioExcelService();

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
        @RequestBody ConsultaDTO dados,
        HttpSession session) {

    if (!isCoordenador(session)) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
    }

    if (dados == null) {
        return ResponseEntity
                .badRequest()
                .body("Informe o motivo do cancelamento.");
    }

    String mensagem = consultaService.cancelar(
            id,
            dados.getMotivoCancelamento()
    );

    if ("Consulta cancelada com sucesso.".equals(mensagem)) {
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
        @RequestParam(required = false) Integer consultaId,
        HttpSession session) {

    if (!isCoordenador(session)) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
    }

    try {
        LocalDate dataConsulta = LocalDate.parse(data);

        List<String> horarios;

        if (consultaId != null) {
            horarios =
                    consultaService.buscarHorariosDisponiveisParaEdicao(
                            idAluno, dataConsulta, consultaId
                    );
        } else {
            horarios =
                    consultaService.buscarHorariosDisponiveis(
                            idAluno, dataConsulta
                    );
        }

        return ResponseEntity.ok(horarios);

    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Data inválida.");
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
        @RequestParam(required = false) LocalDate data,
        HttpSession session) {

    Object tipoUsuario = session.getAttribute("tipoUsuario");

    if (tipoUsuario == null ||
            !"aluno".equals(tipoUsuario.toString())) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
    }

    Object idAlunoSession = session.getAttribute("idAluno");

    if (idAlunoSession == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Aluno não identificado.");
    }

    int idAluno = Integer.parseInt(idAlunoSession.toString());

    if (data == null) {
        return ResponseEntity.ok(
                consultaService.listarAgendadasPorAlunoSemData(idAluno)
        );
    }

    return ResponseEntity.ok(
            consultaService.listarAgendadasPorAluno(idAluno, data)
    );
}


@GetMapping("/exportar-excel")
public ResponseEntity<?> exportarExcel(
        @RequestParam(required = false) String aluno,
        @RequestParam(required = false) String paciente,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String data,
        HttpSession session) {

    if (!isCoordenador(session)) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Acesso negado.");
    }

    LocalDate dataFiltro = null;

    try {
        if (data != null && !data.isBlank()) {
            dataFiltro = LocalDate.parse(data);
        }
    } catch (DateTimeParseException e) {
        return ResponseEntity.badRequest()
                .body("Data inválida para exportação.");
    }

    try {
        List<ConsultaDTO> consultas =
                consultaService.listarParaExportacao(
                        aluno,
                        paciente,
                        status,
                        dataFiltro
                );

        byte[] arquivo =
                relatorioExcelService.gerarExcel(consultas);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"consultas-filtradas.xlsx\""
                )
                .body(arquivo);

    } catch (IOException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Não foi possível gerar o arquivo Excel.");
    } catch (Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Não foi possível consultar os dados para exportação.");
    }
}


}