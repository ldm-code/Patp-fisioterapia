package Patp_fisioterapia.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import Patp_fisioterapia.dao.AlunoDAO;
import Patp_fisioterapia.dto.AlunoDTO;

import java.util.ArrayList;
import java.util.List;

public class AlunoService {

    private AlunoDAO alunoDAO = new AlunoDAO();

    public List<AlunoDTO> listarAlunosService() {

        return alunoDAO.listarAlunos();

    }
    public List<AlunoDTO> listarAlunosPorProfessor(int idProfessor) {

        if (idProfessor <= 0) {
            return new ArrayList<>();
        }

        return alunoDAO.listarAlunosPorProfessor(idProfessor);
    }
    public List<AlunoDTO> selecionarPorEmailProfessor(
        String email, int idProfessor) {

    if (email == null || email.isBlank() || idProfessor <= 0) {
        return new ArrayList<>();
    }

    return alunoDAO.selecionarPorEmailProfessor(
        email.trim(),
        idProfessor
    );
}

    public List<AlunoDTO> selecionarPorEmail(String email) {

        return alunoDAO.selecionarPorEmail(email);

    }

    public boolean cadastrarAluno(AlunoDTO aluno) {

        if (aluno.getNome() == null || aluno.getNome().trim().isEmpty()
                || aluno.getEmail() == null || aluno.getEmail().trim().isEmpty()
                || aluno.getSenha() == null || aluno.getSenha().trim().isEmpty()
                || aluno.getCpf() == null || aluno.getCpf().trim().isEmpty()
                || aluno.getTipo() == null || aluno.getTipo().trim().isEmpty()) {

            return false;
        }

        if (aluno.getSenha().length() < 6) {

            return false;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String senhaHash = encoder.encode(aluno.getSenha());

        aluno.setNome(aluno.getNome().trim());
        aluno.setEmail(aluno.getEmail().trim());
        aluno.setCpf(aluno.getCpf().trim());
        aluno.setSenha(senhaHash);
        aluno.setTipo(aluno.getTipo().trim());

        return alunoDAO.cadastrarAluno(aluno);
    }
}

