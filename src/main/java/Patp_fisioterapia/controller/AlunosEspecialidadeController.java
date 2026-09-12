package Patp_fisioterapia.controller;

import Patp_fisioterapia.service.AlunosEspecialidadeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alunos-especialidade")
public class AlunosEspecialidadeController {

private AlunosEspecialidadeService service =
        new AlunosEspecialidadeService();

@PostMapping("/cadastrar")
public ResponseEntity<String> cadastrar(
        @RequestParam int idAluno,
        @RequestParam int idEspecialidade) {

    boolean cadastrado =
            service.cadastrar(idAluno, idEspecialidade);

    if (cadastrado) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Especialidade vinculada com sucesso.");
    }

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Não foi possível vincular a especialidade.");
}
@DeleteMapping("/remover")
public ResponseEntity<String> remover(
        @RequestParam int idAluno,
        @RequestParam int idEspecialidade) {

    boolean removido =
            service.remover(idAluno, idEspecialidade);

    if (removido) {
        return ResponseEntity.ok(
                "Especialidade removida com sucesso."
        );
    }

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Não foi possível remover a especialidade.");
}

}
