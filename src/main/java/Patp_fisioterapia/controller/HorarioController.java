package Patp_fisioterapia.controller;

import Patp_fisioterapia.dto.HorarioDTO;
import Patp_fisioterapia.service.HorarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/horarios")
public class HorarioController {

    private HorarioService horarioService = new HorarioService();

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarHorario(
        @RequestBody HorarioDTO horario) {

    String mensagem = horarioService.cadastrarHorario(horario);

    if (mensagem.equals("Horário cadastrado com sucesso.")) {
        return ResponseEntity.ok(mensagem);
    }

    return ResponseEntity.badRequest().body(mensagem);
}
}