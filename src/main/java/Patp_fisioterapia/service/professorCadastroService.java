package Patp_fisioterapia.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import Patp_fisioterapia.dao.professorDao;
import Patp_fisioterapia.dao.professoresEspecializacaoDAO;
import Patp_fisioterapia.dto.professorDto;
import Patp_fisioterapia.dto.ProfessorCadastroRespostaDTO;

@Service
public class professorCadastroService {

    public ProfessorCadastroRespostaDTO cadastrar(
            String nome,
            String email,
            String senha,
            int idEspecialidade) {

        if (nome == null || nome.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || senha == null || senha.trim().isEmpty()) {

            return new ProfessorCadastroRespostaDTO(
                    "Um dos campos não foi preenchido", 0);
        }

        if (idEspecialidade <= 0) {
            return new ProfessorCadastroRespostaDTO(
                    "Selecione uma especialidade", 0);
        }

        if (senha.length() < 6) {
            return new ProfessorCadastroRespostaDTO(
                    "A senha deve ter pelo menos 6 caracteres", 0);
        }

        String emailFormato = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

        if (!email.matches(emailFormato)) {
            return new ProfessorCadastroRespostaDTO(
                    "Email nao tem um formato valido", 0);
        }

        professorDto professor = new professorDto();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaHash = encoder.encode(senha);

        professor.setNome(nome.trim());
        professor.setEmail(email.trim());
        professor.setSenha(senhaHash);

        int idProfessor = professorDao.inserirProfessor(
                professor.getNome(),
                professor.getEmail(),
                professor.getSenha()
        );

        if (idProfessor == 0) {
            return new ProfessorCadastroRespostaDTO(
                    "Erro ao cadastrar professor", 0);
        }

        boolean vinculado =
                professoresEspecializacaoDAO.inserirVinculo(
                        idProfessor,
                        idEspecialidade
                );

        if (!vinculado) {
            return new ProfessorCadastroRespostaDTO(
                    "Professor cadastrado, mas não foi possível vincular a especialidade",
                    idProfessor
            );
        }

        professor.setId(idProfessor);

        return new ProfessorCadastroRespostaDTO(
                "Professor cadastrado com sucesso",
                idProfessor
        );
    }
}