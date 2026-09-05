
package Patp_fisioterapia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Patp_fisioterapia.dto.pacienteDto;
import Patp_fisioterapia.service.PacienteService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    // ============================================================
    // CADASTRAR PACIENTE
    // ============================================================

    @PostMapping
    public ResponseEntity<?> cadastrarPaciente(
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String telefone) {

        boolean cadastrado = PacienteService.cadastrarPaciente(
                nome,
                cpf,
                telefone
        );

        if (!cadastrado) {

            return ResponseEntity
                    .badRequest()
                    .body("Dados do paciente inválidos.");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Paciente cadastrado com sucesso.");
    }


    // ============================================================
    // BUSCAR TODOS / FILTRAR
    // ============================================================

    @GetMapping
    public ResponseEntity<List<pacienteDto>> buscarPacientes(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf) {

        if ((nome == null || nome.trim().isEmpty())
                && (cpf == null || cpf.trim().isEmpty())) {

            return ResponseEntity.ok(
                    PacienteService.buscarPacientes()
            );
        }

        return ResponseEntity.ok(
                PacienteService.buscarPorFiltro(nome, cpf)
        );
    }


    // ============================================================
    // BUSCAR PACIENTE POR ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPacientePorId(
            @PathVariable int id) {

        pacienteDto paciente =
                PacienteService.buscarPacientePorId(id);

        if (paciente == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Paciente não encontrado.");
        }

        return ResponseEntity.ok(paciente);
    }


    // ============================================================
    // EDITAR PACIENTE
    // ============================================================

        @PutMapping("/{id}")
        public ResponseEntity<?> editarPaciente(
                @PathVariable int id,
                @RequestParam String nome,
                @RequestParam String cpf,
                @RequestParam String telefone,
                HttpSession session) {

        String tipoUsuario =
                (String) session.getAttribute("tipoUsuario");

        if (!"professor".equals(tipoUsuario)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Apenas professores podem editar pacientes.");
        }

        boolean editado = PacienteService.editarPaciente(
                id,
                nome,
                cpf,
                telefone
        );

        if (!editado) {
                return ResponseEntity
                        .badRequest()
                        .body("Dados do paciente inválidos.");
        }

        return ResponseEntity.ok(
                "Paciente atualizado com sucesso."
        );
        }
  

}

