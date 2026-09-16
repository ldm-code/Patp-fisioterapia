package Patp_fisioterapia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import Patp_fisioterapia.dao.AlunoTurnosDao;
import Patp_fisioterapia.dto.AlunoTurnoDTO;

@Service
public class AlunoTurnoService {

    private final AlunoTurnosDao alunoTurnosDAO =
            new AlunoTurnosDao();

    public boolean cadastrar(int idAluno, int idTurno) {

        if (idAluno <= 0 || idTurno <= 0) {
            return false;
        }

        if (alunoTurnosDAO.existe(idAluno, idTurno)) {
            return false;
        }

        return alunoTurnosDAO.cadastrar(idAluno, idTurno);
    }

    public boolean remover(int idAluno, int idTurno) {

        if (idAluno <= 0 || idTurno <= 0) {
            return false;
        }

        return alunoTurnosDAO.remover(idAluno, idTurno);
    }

    public List<AlunoTurnoDTO> listarPorAluno(int idAluno) {

        if (idAluno <= 0) {
            return List.of();
        }

        return alunoTurnosDAO.listarPorAluno(idAluno);
    }
}