package Patp_fisioterapia.service;

import Patp_fisioterapia.dao.ConsultaDAO;
import Patp_fisioterapia.dao.RelatorioDAO;
import Patp_fisioterapia.dto.AlunoDTO;
import Patp_fisioterapia.dto.ConsultaDTO;
import Patp_fisioterapia.dto.RelatorioDTO;
import Patp_fisioterapia.dto.pacienteDto;
import  java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ConsultaService {

    private final ConsultaDAO consultaDAO = new ConsultaDAO();
    private final RelatorioDAO relatorioDAO =
        new RelatorioDAO();

    public String cadastrar(ConsultaDTO consulta) {

        String erro = validar(consulta);

        if (erro != null) {
            return erro;
        }

        boolean horarioValido =
                consultaDAO.horarioPertenceAoAluno(
                        consulta.getAlunoId(),
                        consulta.getDataConsulta().toLocalTime()
                );

        if (!horarioValido) {
            return "O horário selecionado não pertence aos horários do aluno.";
        }

        if (consultaDAO.cadastrar(consulta)) {
            return "Consulta cadastrada com sucesso.";
        }

        return "Não foi possível cadastrar a consulta.";
    }

    public List<ConsultaDTO> listar() {
        return consultaDAO.listar();
    }

    public ConsultaDTO buscarPorId(int id) {

        if (id <= 0) {
            return null;
        }

        return consultaDAO.buscarPorId(id);
    }

    public String atualizar(ConsultaDTO consulta) {

        if (consulta == null || consulta.getId() <= 0) {
            return "Consulta inválida.";
        }

        String erro = validar(consulta);

        if (erro != null) {
            return erro;
        }

        boolean horarioValido =
                consultaDAO.horarioPertenceAoAluno(
                        consulta.getAlunoId(),
                        consulta.getDataConsulta().toLocalTime()
                );

        if (!horarioValido) {
            return "O horário selecionado não pertence aos horários do aluno.";
        }

        if (consultaDAO.atualizar(consulta)) {
            return "Consulta atualizada com sucesso.";
        }

        return "Não foi possível atualizar a consulta.";
    }

    public String cancelar(int id) {

        ConsultaDTO consulta = buscarPorId(id);

        if (consulta == null) {
            return "Consulta não encontrada.";
        }

        if ("concluida".equals(consulta.getStatus())) {
            return "Uma consulta concluída não pode ser cancelada.";
        }

        if ("cancelada".equals(consulta.getStatus())) {
            return "A consulta já está cancelada.";
        }

        if (consultaDAO.atualizarStatus(id, "cancelada")) {
            return "Consulta cancelada com sucesso.";
        }

        return "Não foi possível cancelar a consulta.";
    }

    public String concluir(int id) {

        ConsultaDTO consulta = buscarPorId(id);
        RelatorioDTO relatorio = relatorioDAO.buscarPorConsulta(id);
        if (consulta == null) {
            return "Consulta não encontrada.";
        }

        if ("cancelada".equals(consulta.getStatus())) {
            return "Uma consulta cancelada não pode ser concluída.";
        }

        if ("concluida".equals(consulta.getStatus())) {
            return "A consulta já está concluída.";
        }

        if (!consulta.isTemRelatorio()) {
            return "A consulta não pode ser concluída porque ainda não possui relatório.";
        }
        if (!"aprovado".equals(
                relatorio.getStatus())) {

            return "A consulta não pode ser concluída porque o relatório ainda não foi aprovado.";
        }

        if (consultaDAO.atualizarStatus(id, "concluida")) {
            return "Consulta concluída com sucesso.";
        }

        return "Não foi possível concluir a consulta.";
    }

    public List<AlunoDTO> buscarAlunos(String nome) {

        if (nome == null || nome.trim().length() < 2) {
            return List.of();
        }

        return consultaDAO.buscarAlunosPorNome(nome);
    }

    public List<pacienteDto> buscarPacientes(String nome) {

        if (nome == null || nome.trim().length() < 2) {
            return List.of();
        }

        return consultaDAO.buscarPacientesPorNome(nome);
    }

    public List<String> buscarHorariosDisponiveis(
        int idAluno,
        LocalDate data
) {

    if (idAluno <= 0 || data == null) {
        return List.of();
    }

    return consultaDAO.buscarHorariosDisponiveisDoAluno(
            idAluno,
            data
    );
}
    
    public List<ConsultaDTO> listarAgendadasPorAluno(int idAluno) {

    if (idAluno <= 0) {
        return List.of();
    }

    return consultaDAO.listarAgendadasPorAluno(idAluno);
}


    private String validar(ConsultaDTO consulta) {

        if (consulta == null) {
            return "Dados da consulta inválidos.";
        }

        if (consulta.getAlunoId() <= 0) {
            return "Selecione um aluno.";
        }

        if (consulta.getPaciente() <= 0) {
            return "Selecione um paciente.";
        }

        if (consulta.getMotivo() == null ||
                consulta.getMotivo().trim().isEmpty()) {

            return "Informe o motivo da consulta.";
        }

        if (consulta.getMotivo().trim().length() > 500) {
            return "O motivo pode ter no máximo 500 caracteres.";
        }

        if (consulta.getDataConsulta() == null) {
            return "Informe a data e o horário da consulta.";
        }

        if (consulta.getDiagnostico() != null &&
                consulta.getDiagnostico().length() > 150) {

            return "O diagnóstico pode ter no máximo 150 caracteres.";
        }

        if (consulta.getDataConsulta().isBefore(LocalDateTime.now())) {
            return "A data da consulta não pode estar no passado.";
        }

        return null;
    }
}
