package Patp_fisioterapia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Patp_fisioterapia.dto.AlunoTurnoDTO;
import Patp_fisioterapia.service.AlunoTurnoService;

@RestController
@RequestMapping("/alunos-turnos")
public class AlunoTurnoController {

    private final AlunoTurnoService service =
            new AlunoTurnoService();

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrar(
            @RequestParam int idAluno,
            @RequestParam int idTurno) {

        boolean sucesso = service.cadastrar(idAluno, idTurno);

        if (sucesso) {
            return ResponseEntity.ok(
                "O turno foi vinculado com sucesso."
            );
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Não foi possível vincular o turno.");
    }

    @DeleteMapping("/remover")
    public ResponseEntity<String> remover(
            @RequestParam int idAluno,
            @RequestParam int idTurno) {

        boolean sucesso = service.remover(idAluno, idTurno);

        if (sucesso) {
            return ResponseEntity.ok(
                "O turno foi removido com sucesso."
            );
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Não foi possível remover o turno.");
    }

    @GetMapping
    public ResponseEntity<List<AlunoTurnoDTO>> listarPorAluno(
            @RequestParam int idAluno) {

        if (idAluno <= 0) {
            return ResponseEntity.badRequest().build();
        }

        List<AlunoTurnoDTO> turnos =
                service.listarPorAluno(idAluno);

        return ResponseEntity.ok(turnos);
    }
}