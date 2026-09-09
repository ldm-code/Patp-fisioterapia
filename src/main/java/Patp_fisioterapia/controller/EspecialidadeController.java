package Patp_fisioterapia.controller;

import Patp_fisioterapia.dto.EspecialidadeDTO;
import Patp_fisioterapia.service.EspecialidadeService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/especialidades")
public class EspecialidadeController {


private EspecialidadeService especialidadeService =
        new EspecialidadeService();

@GetMapping
public List<EspecialidadeDTO> listarEspecialidades() {

    return especialidadeService.listarEspecialidades();
}


}

