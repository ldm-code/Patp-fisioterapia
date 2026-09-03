
package Patp_fisioterapia.service;

import java.util.List;

import Patp_fisioterapia.dao.PacienteDao;
import Patp_fisioterapia.dto.pacienteDto;

public class PacienteService {

    // ============================================================
    // CADASTRAR PACIENTE
    // ============================================================

    public static boolean cadastrarPaciente(
            String nome,
            String cpf,
            String telefone) {

        // Remove espaços desnecessários
        nome = nome != null ? nome.trim() : "";
        cpf = cpf != null ? cpf.trim() : "";
        telefone = telefone != null ? telefone.trim() : "";

        // Verifica campos vazios
        if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty()) {
            return false;
        }

        // ========================================================
        // VALIDAÇÃO DO TELEFONE
        // ========================================================

        // Permite números, parênteses, hífen e espaços
        if (!telefone.matches("^[0-9()\\-\\s]+$")) {
            return false;
        }

        // Remove máscara do telefone
        telefone = telefone.replaceAll("\\D", "");

        // Garante que sobraram somente números
        if (!telefone.matches("\\d+")) {
            return false;
        }

        // Telefone brasileiro deve possuir 10 ou 11 dígitos
        if (telefone.length() != 10 && telefone.length() != 11) {
            return false;
        }

        // ========================================================
        // VALIDAÇÃO DO CPF
        // ========================================================

        // Aceita CPF:
        // 123.456.789-00
        // 123.456.789.00
        // 12345678900

        if (!cpf.matches(
                "^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"
                + "|\\d{3}\\.\\d{3}\\.\\d{3}\\.\\d{2}"
                + "|\\d{11})$")) {

            return false;
        }

        // Remove pontos e hífen
        cpf = cpf.replaceAll("[.-]", "");

        // Garante somente números
        if (!cpf.matches("\\d+")) {
            return false;
        }

        // CPF deve possuir exatamente 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // ========================================================
        // ENVIA PARA O DAO
        // ========================================================

        return PacienteDao.cadastrarPaciente(
                nome,
                cpf,
                telefone
        );
    }


    // ============================================================
    // BUSCAR PACIENTE POR ID
    // ============================================================

    public static pacienteDto buscarPacientePorId(int id) {

        // Validação do ID
        if (id <= 0) {
            return null;
        }

        return PacienteDao.buscarPacientePorId(id);
    }


    // ============================================================
    // EDITAR PACIENTE
    // ============================================================

    public static boolean editarPaciente(
            int id,
            String nome,
            String cpf,
            String telefone) {

        // Validação do ID
        if (id <= 0) {
            return false;
        }

        nome = nome != null ? nome.trim() : "";
        cpf = cpf != null ? cpf.trim() : "";
        telefone = telefone != null ? telefone.trim() : "";

        // Campos obrigatórios
        if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty()) {
            return false;
        }

        // ========================================================
        // VALIDAÇÃO DO TELEFONE
        // ========================================================

        if (!telefone.matches("^[0-9()\\-\\s]+$")) {
            return false;
        }

        telefone = telefone.replaceAll("\\D", "");

        if (!telefone.matches("\\d+")) {
            return false;
        }

        if (telefone.length() != 10 && telefone.length() != 11) {
            return false;
        }

        // ========================================================
        // VALIDAÇÃO DO CPF
        // ========================================================

        if (!cpf.matches(
                "^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"
                + "|\\d{3}\\.\\d{3}\\.\\d{3}\\.\\d{2}"
                + "|\\d{11})$")) {

            return false;
        }

        cpf = cpf.replaceAll("[.-]", "");

        if (!cpf.matches("\\d+")) {
            return false;
        }

        if (cpf.length() != 11) {
            return false;
        }

        // ========================================================
        // ENVIA PARA O DAO
        // ========================================================

        return PacienteDao.editarDados(
                id,
                nome,
                cpf,
                telefone
        );
    }


    // ============================================================
    // BUSCAR TODOS OS PACIENTES
    // ============================================================

    public static List<pacienteDto> buscarPacientes() {

        return PacienteDao.buscarPacientes();
    }


    // ============================================================
    // BUSCAR POR FILTRO
    // ============================================================

    public static List<pacienteDto> buscarPorFiltro(
            String nome,
            String cpf) {

        nome = nome != null ? nome.trim() : "";
        cpf = cpf != null ? cpf.trim() : "";

        return PacienteDao.buscarPorFiltro(nome, cpf);
    }
}

