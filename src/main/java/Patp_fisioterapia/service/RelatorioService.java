package Patp_fisioterapia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import Patp_fisioterapia.dao.ConsultaDAO;
import Patp_fisioterapia.dao.RelatorioDAO;
import Patp_fisioterapia.dto.ConsultaDTO;
import Patp_fisioterapia.dto.RelatorioDTO;

@Service
public class RelatorioService {

    private final RelatorioDAO relatorioDAO = new RelatorioDAO();
    private final ConsultaDAO consultaDAO = new ConsultaDAO();

    public String cadastrar(RelatorioDTO relatorio, int idAluno) {
        if (relatorio == null || relatorio.getConsultaId() <= 0) {
            return "Consulta inválida.";
        }

        if (relatorio.getDescricao() == null
                || relatorio.getDescricao().trim().isEmpty()) {
            return "Informe a descrição do relatório.";
        }

        ConsultaDTO consulta =
            consultaDAO.buscarPorId(relatorio.getConsultaId());

        if (consulta == null) {
            return "Consulta não encontrada.";
        }

        if (consulta.getAlunoId() != idAluno) {
            return "A consulta não pertence ao aluno.";
        }

        if (!"agendada".equals(consulta.getStatus())) {
            return "A consulta não pode receber um relatório.";
        }

        if (relatorioDAO.buscarPorConsulta(
                relatorio.getConsultaId()) != null) {
            return "Esta consulta já possui um relatório.";
        }

        if (!relatorioDAO.cadastrar(relatorio)) {
            return "Não foi possível cadastrar o relatório.";
        }

        if (!consultaDAO.atualizarStatus(
                relatorio.getConsultaId(), "a validar")) {
            return "Relatório cadastrado, mas não foi possível atualizar a consulta.";
        }

        return "Relatório cadastrado com sucesso.";
    }

    public RelatorioDTO buscarPorConsulta(int consultaId) {
        if (consultaId <= 0) {
            return null;
        }
        return relatorioDAO.buscarPorConsulta(consultaId);
    }

    public RelatorioDTO buscarPorId(int id) {
        if (id <= 0) {
            return null;
        }
        return relatorioDAO.buscarPorId(id);
    }

    public boolean anexoPertenceAoAluno(int idRelatorio, int idAluno) {
        return relatorioDAO.pertenceAoAluno(idRelatorio, idAluno);
    }

    public String atualizarStatus(
            int id,
            String status,
            String observacao) {

        if (id <= 0) {
            return "Relatório inválido.";
        }

        if (!"aprovado".equals(status)
                && !"reprovado".equals(status)) {
            return "Status de relatório inválido.";
        }

        if ("reprovado".equals(status)
                && (observacao == null || observacao.trim().isEmpty())) {
            return "Informe a observação para reprovar o relatório.";
        }

        RelatorioDTO relatorio = relatorioDAO.buscarPorId(id);

        if (relatorio == null) {
            return "Relatório não encontrado.";
        }

        if (relatorio.getStatus() != null
                && !relatorio.getStatus().isBlank()) {
            return "Este relatório já foi avaliado.";
        }

        String motivo = "reprovado".equals(status)
                ? observacao.trim()
                : null;

        if (!relatorioDAO.atualizarAvaliacao(id, status, motivo)) {
            return "Não foi possível atualizar o status do relatório.";
        }

      
        return "Status do relatório atualizado com sucesso.";
    }

    public String atualizarDescricao(
            int id,
            String descricao,
            int idAluno,
            String novoAnexo) {

        if (id <= 0) {
            return "Relatório inválido.";
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            return "Informe a descrição do relatório.";
        }

        RelatorioDTO relatorio = relatorioDAO.buscarPorId(id);

        if (relatorio == null) {
            return "Relatório não encontrado.";
        }

        if (!relatorioDAO.pertenceAoAluno(id, idAluno)) {
            return "Este relatório não pertence ao aluno.";
        }

        if (!"reprovado".equals(relatorio.getStatus())) {
            return "Somente relatórios reprovados podem ser editados.";
        }

        if (!relatorioDAO.atualizarDescricao(
                id, descricao.trim(), novoAnexo)) {
            return "Não foi possível atualizar o relatório.";
        }

        if (!consultaDAO.atualizarStatus(
                relatorio.getConsultaId(), "a validar")) {
            return "Relatório atualizado, mas não foi possível atualizar a consulta.";
        }

        return "Relatório atualizado com sucesso.";
    }

    public List<RelatorioDTO> listarPorAluno(
            int idAluno, String status) {
        return relatorioDAO.listarPorAluno(idAluno, status);
    }

    public List<RelatorioDTO> listarPorCoordenador(
            String emailAluno, String status) {
        return relatorioDAO.listarPorCoordenador(emailAluno, status);
    }
}