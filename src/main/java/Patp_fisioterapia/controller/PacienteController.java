
package Patp_fisioterapia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.http.dsl.Http;
import org.springframework.web.bind.annotation.*;

import Patp_fisioterapia.dto.pacienteDto;
import Patp_fisioterapia.service.PacienteService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {
        private boolean usuarioAutenticado(HttpSession session) {
                 return session.getAttribute("usuarioLogado") != null;
        }

        private boolean usuarioCoordenador(HttpSession session) {
                return "coordenador".equals(
                        session.getAttribute("tipoUsuario")
                );
        }
    // ============================================================
    // CADASTRAR PACIENTE
    // ============================================================

    @PostMapping
    public ResponseEntity<?> cadastrarPaciente(
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String telefone,
         HttpSession session) {
         if (!usuarioAutenticado(session)) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("É necessário fazer login.");
    }

    if (!usuarioCoordenador(session)) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Apenas coordenadores podem cadastrar pacientes.");
    }
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
        public ResponseEntity<?> buscarPacientes(
                @RequestParam(required = false) String nome,
                @RequestParam(required = false) String cpf,
                HttpSession session) {

        if (!usuarioAutenticado(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("É necessário fazer login.");
        }

        if (!usuarioCoordenador(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Apenas coordenadores podem consultar pacientes.");
        }

        if ((nome == null || nome.trim().isEmpty())
                && (cpf == null || cpf.trim().isEmpty())) {
                return ResponseEntity.ok(PacienteService.buscarPacientes());
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
        @PathVariable int id,
        HttpSession session) {

    if (!usuarioAutenticado(session)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("É necessário fazer login.");
    }

    if (!usuarioCoordenador(session)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Apenas coordenadores podem consultar pacientes.");
    }

    pacienteDto paciente = PacienteService.buscarPacientePorId(id);

    if (paciente == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
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

                if (!usuarioAutenticado(session)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("É necessário fazer login.");
        }

        if (!usuarioCoordenador(session)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Apenas coordenadores podem editar pacientes.");
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

