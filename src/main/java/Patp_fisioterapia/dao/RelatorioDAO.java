package Patp_fisioterapia.dao;

import Patp_fisioterapia.dto.RelatorioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {

    public boolean cadastrar(RelatorioDTO relatorio) {
        String sql = """
            INSERT INTO relatorios
                (consulta_id, descricao, anexo_exame)
            VALUES (?, ?, ?)
            """;

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setInt(1, relatorio.getConsultaId());
            stmt.setString(2, relatorio.getDescricao());
            stmt.setString(3, relatorio.getAnexoExame());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public RelatorioDTO buscarPorConsulta(int consultaId) {
        String sql = """
            SELECT id, consulta_id, descricao, status,
                   data_criacao, anexo_exame, observacao
            FROM relatorios
            WHERE consulta_id = ?
            """;

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setInt(1, consultaId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearRelatorio(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public RelatorioDTO buscarPorId(int id) {
        String sql = """
            SELECT id, consulta_id, descricao, status,
                   data_criacao, anexo_exame, observacao
            FROM relatorios
            WHERE id = ?
            """;

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearRelatorio(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean atualizarAvaliacao(
            int id,
            String status,
            String observacao) {

        String sql = """
            UPDATE relatorios
            SET status = ?, observacao = ?
            WHERE id = ?
            """;

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, status);
            stmt.setString(2, observacao);
            stmt.setInt(3, id);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarDescricao(
            int id,
            String descricao,
            String novoAnexo) {

        String sql;

        if (novoAnexo == null) {
            sql = """
                UPDATE relatorios
                SET descricao = ?,
                    status = NULL,
                    observacao = NULL
                WHERE id = ?
                """;
        } else {
            sql = """
                UPDATE relatorios
                SET descricao = ?,
                    anexo_exame = ?,
                    status = NULL,
                    observacao = NULL
                WHERE id = ?
                """;
        }

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, descricao);

            if (novoAnexo == null) {
                stmt.setInt(2, id);
            } else {
                stmt.setString(2, novoAnexo);
                stmt.setInt(3, id);
            }

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<RelatorioDTO> listarPorAluno(
            int idAluno,
            String status) {

        StringBuilder sql = new StringBuilder("""
            SELECT r.id, r.consulta_id, r.descricao, r.status,
                   r.data_criacao, r.anexo_exame, r.observacao
            FROM relatorios r
            INNER JOIN consultas c ON c.id = r.consulta_id
            WHERE c.aluno_id = ?
            """);

        adicionarFiltroStatus(sql, status);
        sql.append(" ORDER BY r.data_criacao DESC");

        List<RelatorioDTO> relatorios = new ArrayList<>();

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt =
                conexao.prepareStatement(sql.toString())
        ) {
            stmt.setInt(1, idAluno);

            if (statusValido(status)) {
                stmt.setString(2, status);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    relatorios.add(mapearRelatorio(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relatorios;
    }

    public List<RelatorioDTO> listarPorCoordenador(
            String emailAluno,
            String status) {

        StringBuilder sql = new StringBuilder("""
            SELECT r.id, r.consulta_id, r.descricao, r.status,
                   r.data_criacao, r.anexo_exame, r.observacao,
                   a.nome AS nome_aluno, a.email AS email_aluno
            FROM relatorios r
            INNER JOIN consultas c ON c.id = r.consulta_id
            INNER JOIN alunos a ON a.id = c.aluno_id
            WHERE a.email LIKE ?
            """);

        adicionarFiltroStatus(sql, status);
        sql.append(" ORDER BY r.data_criacao DESC");

        List<RelatorioDTO> relatorios = new ArrayList<>();

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt =
                conexao.prepareStatement(sql.toString())
        ) {
            String filtro = emailAluno == null ? "" : emailAluno.trim();
            stmt.setString(1, "%" + filtro + "%");

            if (statusValido(status)) {
                stmt.setString(2, status);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RelatorioDTO relatorio = mapearRelatorio(rs);
                    relatorio.setNomeAluno(rs.getString("nome_aluno"));
                    relatorio.setEmailAluno(rs.getString("email_aluno"));
                    relatorios.add(relatorio);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relatorios;
    }

    public boolean pertenceAoAluno(int idRelatorio, int idAluno) {
        String sql = """
            SELECT 1
            FROM relatorios r
            INNER JOIN consultas c ON c.id = r.consulta_id
            WHERE r.id = ? AND c.aluno_id = ?
            """;

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setInt(1, idRelatorio);
            stmt.setInt(2, idAluno);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void adicionarFiltroStatus(
            StringBuilder sql,
            String status) {

        if ("pendente".equals(status)) {
            sql.append(" AND r.status IS NULL ");
        } else if (statusValido(status)) {
            sql.append(" AND r.status = ? ");
        }
    }

    private boolean statusValido(String status) {
        return "aprovado".equals(status)
                || "reprovado".equals(status);
    }

    private RelatorioDTO mapearRelatorio(ResultSet rs)
            throws java.sql.SQLException {

        RelatorioDTO relatorio = new RelatorioDTO();

        relatorio.setId(rs.getInt("id"));
        relatorio.setConsultaId(rs.getInt("consulta_id"));
        relatorio.setDescricao(rs.getString("descricao"));
        relatorio.setStatus(rs.getString("status"));
        relatorio.setAnexoExame(rs.getString("anexo_exame"));
        relatorio.setObservacao(rs.getString("observacao"));

        if (rs.getTimestamp("data_criacao") != null) {
            relatorio.setDataCriacao(
                rs.getTimestamp("data_criacao").toLocalDateTime()
            );
        }

        return relatorio;
    }
}