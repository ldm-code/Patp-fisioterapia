package Patp_fisioterapia.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import Patp_fisioterapia.dao.AlunoDAO;
import Patp_fisioterapia.dao.professorDao;
import Patp_fisioterapia.dto.AlunoDTO;
import Patp_fisioterapia.dto.UsuarioAutenticadoDTO;

@Service
public class loginService {

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    private final AlunoDAO alunoDAO = new AlunoDAO();

    public UsuarioAutenticadoDTO autenticar(
            String email, String senha) {

        if (email == null || email.trim().isEmpty()
                || senha == null || senha.trim().isEmpty()) {
            return null;
        }

        email = email.trim();

        // 1. Tenta autenticar como aluno
        AlunoDTO aluno = alunoDAO.buscarAlunoLogin(email);

        if (aluno != null
                && encoder.matches(senha, aluno.getSenha())) {

            return new UsuarioAutenticadoDTO(
                    aluno.getEmail(),
                    "aluno"
            );
        }

        // 2. Tenta autenticar como professor
        String senhaHash =
                professorDao.buscarSenhaPorEmail(email);

        if (senhaHash != null
                && encoder.matches(senha, senhaHash)) {

            String tipo =
                    professorDao.buscarTipoPorEmail(email);

            if (tipo != null) {

                int id = professorDao.buscarIdPorEmail(email);

                return new UsuarioAutenticadoDTO(
                        email,
                        tipo.trim().toLowerCase(),
                        id
                );
            }
        }

        return null;
    }
}