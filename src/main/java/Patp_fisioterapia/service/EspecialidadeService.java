package Patp_fisioterapia.service;

import Patp_fisioterapia.dao.EspecialidadeDAO;
import Patp_fisioterapia.dto.EspecialidadeDTO;

import java.util.List;

public class EspecialidadeService {


private EspecialidadeDAO especialidadeDAO =
        new EspecialidadeDAO();

public List<EspecialidadeDTO> listarEspecialidades() {

    return especialidadeDAO.listarEspecialidades();
}


}
