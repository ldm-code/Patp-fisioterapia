package Patp_fisioterapia.service;

import java.util.List;

import Patp_fisioterapia.dao.ConsultaDAO;
import Patp_fisioterapia.dao.RelatorioDAO;
import Patp_fisioterapia.dto.ConsultaDTO;
import Patp_fisioterapia.dto.RelatorioDTO;

public class RelatorioService {

    private final RelatorioDAO relatorioDAO =
            new RelatorioDAO();

    private final ConsultaDAO consultaDAO =
            new ConsultaDAO();


    public String cadastrar(
            RelatorioDTO relatorio,
            int idAluno) {

        if (relatorio == null) {
            return "Dados do relatório inválidos.";
        }

        if (relatorio.getConsultaId() <= 0) {
            return "Consulta inválida.";
        }

        if (relatorio.getDescricao() == null ||
                relatorio.getDescricao().trim().isEmpty()) {

            return "Informe a descrição do relatório.";
        }

        ConsultaDTO consulta =
                consultaDAO.buscarPorId(
                        relatorio.getConsultaId()
                );

        if (consulta == null) {
            return "Consulta não encontrada.";
        }

        if (consulta.getAlunoId() != idAluno) {
            return "A consulta não pertence ao aluno.";
        }

        if (!"agendada".equals(
                consulta.getStatus())) {

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
                relatorio.getConsultaId(),
                "a validar")) {

            return "Relatório criado, mas não foi possível atualizar a consulta.";
        }

        return "Relatório cadastrado com sucesso.";
    }


    public RelatorioDTO buscarPorConsulta(
            int consultaId) {

        if (consultaId <= 0) {
            return null;
        }

        return relatorioDAO.buscarPorConsulta(
                consultaId
        );
    }


    public String atualizarStatus(
            int id,
            String status) {

        if (id <= 0) {
            return "Relatório inválido.";
        }

        if (!"aprovado".equals(status) &&
                !"reprovado".equals(status)) {

            return "Status de relatório inválido.";
        }

        RelatorioDTO relatorio =
                relatorioDAO.buscarPorId(id);

        if (relatorio == null) {
            return "Relatório não encontrado.";
        }

        if (relatorioDAO.atualizarStatus(
                id,
                status)) {

            return "Status do relatório atualizado com sucesso.";
        }

        return "Não foi possível atualizar o status do relatório.";
    }
    public String atualizarDescricao(
        int id,
        String descricao,
        int idAluno) {

    if (id <= 0) {
        return "Relatório inválido.";
    }

    if (descricao == null ||
            descricao.trim().isEmpty()) {

        return "Informe a descrição do relatório.";
    }

    RelatorioDTO relatorio =
            relatorioDAO.buscarPorId(id);

    if (relatorio == null) {
        return "Relatório não encontrado.";
    }

    ConsultaDTO consulta =
            consultaDAO.buscarPorId(
                    relatorio.getConsultaId()
            );

    if (consulta == null) {
        return "Consulta não encontrada.";
    }

    if (consulta.getAlunoId() != idAluno) {
        return "Este relatório não pertence ao aluno.";
    }

    if (!"reprovado".equals(
            relatorio.getStatus())) {

        return "Somente relatórios reprovados podem ser editados.";
    }

    if (!relatorioDAO.atualizarDescricao(
            id,
            descricao.trim())) {

        return "Não foi possível atualizar o relatório.";
    }

    if (!consultaDAO.atualizarStatus(
            relatorio.getConsultaId(),
            "a validar")) {

        return "Relatório atualizado, mas não foi possível atualizar a consulta.";
    }

    return "Relatório atualizado com sucesso.";
}
public java.util.List<RelatorioDTO> listarPorAluno(
        int idAluno,String status) {

    return relatorioDAO.listarPorAluno(idAluno,status);
}
public List<RelatorioDTO> listarPorCoordenador(
        String emailAluno,String status) {

    return relatorioDAO.listarPorCoordenador(emailAluno,status);
}
}
