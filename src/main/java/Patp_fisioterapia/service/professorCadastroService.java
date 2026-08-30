package Patp_fisioterapia.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import Patp_fisioterapia.dao.professorDao;
import Patp_fisioterapia.dto.professorDto;

@Service
public class professorCadastroService {

    public String cadastrar(String nome, String email, String senha) {

        if (nome == null || nome.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || senha == null || senha.trim().isEmpty()) {

            return "Um dos campos não foi preenchido";
        }

        if (senha.length() < 6) {
            return "A senha deve ter pelo menos 6 caracteres";
        }

        professorDto professor = new professorDto();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String senhaHash = encoder.encode(senha);

        professor.setNome(nome.trim());
        professor.setEmail(email.trim());
        professor.setSenha(senhaHash);

        boolean inserido = professorDao.inserirProfessor(
                professor.getNome(),
                professor.getEmail(),
                professor.getSenha()
        );

        if (inserido) {
            return "Professor cadastrado com sucesso";
        }

        return "Erro ao cadastrar professor";
    }
}