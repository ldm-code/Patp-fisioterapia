package Patp_fisioterapia.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import Patp_fisioterapia.dao.professorDao;

@Service
public class loginService {

    private final BCryptPasswordEncoder encoder;

    public loginService() {
        this.encoder = new BCryptPasswordEncoder();
    }

    public boolean autenticar(String email, String senha) {

        if (email == null || email.trim().isEmpty()
                || senha == null || senha.trim().isEmpty()) {

            return false;
        }

        email = email.trim();

        String senhaHash = professorDao.buscarSenhaPorEmail(email);

        if (senhaHash == null) {
            return false;
        }

        return encoder.matches(senha, senhaHash);
    }
}