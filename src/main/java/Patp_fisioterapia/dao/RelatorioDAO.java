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
                (consulta_id, descricao)
                VALUES (?, ?)
                """;

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setInt(
                    1,
                    relatorio.getConsultaId()
            );

            stmt.setString(
                    2,
                    relatorio.getDescricao()
            );

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public RelatorioDTO buscarPorConsulta(int consultaId) {

        String sql = """
                SELECT
                    id,
                    consulta_id,
                    descricao,
                    status,
                    data_criacao
                FROM relatorios
                WHERE consulta_id = ?
                """;

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, consultaId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    RelatorioDTO relatorio =
                            new RelatorioDTO();

                    relatorio.setId(
                            rs.getInt("id")
                    );

                    relatorio.setConsultaId(
                            rs.getInt("consulta_id")
                    );

                    relatorio.setDescricao(
                            rs.getString("descricao")
                    );

                    relatorio.setStatus(
                            rs.getString("status")
                    );

                    if (rs.getTimestamp("data_criacao") != null) {

                        relatorio.setDataCriacao(
                                rs.getTimestamp(
                                        "data_criacao"
                                ).toLocalDateTime()
                        );
                    }

                    return relatorio;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public RelatorioDTO buscarPorId(int id) {

        String sql = """
                SELECT
                    id,
                    consulta_id,
                    descricao,
                    status,
                    data_criacao
                FROM relatorios
                WHERE id = ?
                """;

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    RelatorioDTO relatorio =
                            new RelatorioDTO();

                    relatorio.setId(
                            rs.getInt("id")
                    );

                    relatorio.setConsultaId(
                            rs.getInt("consulta_id")
                    );

                    relatorio.setDescricao(
                            rs.getString("descricao")
                    );

                    relatorio.setStatus(
                            rs.getString("status")
                    );

                    if (rs.getTimestamp("data_criacao") != null) {

                        relatorio.setDataCriacao(
                                rs.getTimestamp(
                                        "data_criacao"
                                ).toLocalDateTime()
                        );
                    }

                    return relatorio;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean atualizarStatus(
            int id,
            String status) {

        String sql = """
                UPDATE relatorios
                SET status = ?
                WHERE id = ?
                """;

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarDescricao(
            int id,
            String descricao) {

        String sql = """
                UPDATE relatorios
                SET descricao = ?,
                    status = NULL
                WHERE id = ?
                """;

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, descricao);
            stmt.setInt(2, id);

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
            SELECT
                r.id,
                r.consulta_id,
                r.descricao,
                r.status,
                r.data_criacao
            FROM relatorios r
            INNER JOIN consultas c
                ON c.id = r.consulta_id
            WHERE c.aluno_id = ?
            """);

    if ("pendente".equals(status)) {

        sql.append(" AND r.status IS NULL ");

    } else if ("aprovado".equals(status) ||
            "reprovado".equals(status)) {

        sql.append(" AND r.status = ? ");
    }

    sql.append("""
            ORDER BY r.data_criacao DESC
            """);

    List<RelatorioDTO> relatorios =
            new ArrayList<>();

    try (
            Connection conexao =
                    conexaoBanco.conectar();

            PreparedStatement stmt =
                    conexao.prepareStatement(
                            sql.toString()
                    )
    ) {

        stmt.setInt(1, idAluno);

        if ("aprovado".equals(status) ||
                "reprovado".equals(status)) {

            stmt.setString(2, status);
        }

        try (ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                RelatorioDTO relatorio =
                        new RelatorioDTO();

                relatorio.setId(
                        rs.getInt("id")
                );

                relatorio.setConsultaId(
                        rs.getInt("consulta_id")
                );

                relatorio.setDescricao(
                        rs.getString("descricao")
                );

                relatorio.setStatus(
                        rs.getString("status")
                );

                if (rs.getTimestamp(
                        "data_criacao") != null) {

                    relatorio.setDataCriacao(
                            rs.getTimestamp(
                                    "data_criacao"
                            ).toLocalDateTime()
                    );
                }

                relatorios.add(relatorio);
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
            SELECT
                r.id,
                r.consulta_id,
                r.descricao,
                r.status,
                r.data_criacao,
                a.nome AS nome_aluno,
                a.email AS email_aluno
            FROM relatorios r
            INNER JOIN consultas c
                ON c.id = r.consulta_id
            INNER JOIN alunos a
                ON a.id = c.aluno_id
            WHERE a.email LIKE ?
            """);

    if ("pendente".equals(status)) {

        sql.append(" AND r.status IS NULL ");

    } else if ("aprovado".equals(status) ||
            "reprovado".equals(status)) {

        sql.append(" AND r.status = ? ");
    }

    sql.append("""
            ORDER BY r.data_criacao DESC
            """);

    List<RelatorioDTO> relatorios =
            new ArrayList<>();

    try (
            Connection conexao =
                    conexaoBanco.conectar();

            PreparedStatement stmt =
                    conexao.prepareStatement(
                            sql.toString()
                    )
    ) {

        stmt.setString(
                1,
                "%" +
                (emailAluno == null
                        ? ""
                        : emailAluno.trim()) +
                "%"
        );

        if ("aprovado".equals(status) ||
                "reprovado".equals(status)) {

            stmt.setString(2, status);
        }

        try (ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                RelatorioDTO relatorio =
                        new RelatorioDTO();

                relatorio.setId(
                        rs.getInt("id")
                );

                relatorio.setConsultaId(
                        rs.getInt("consulta_id")
                );

                relatorio.setDescricao(
                        rs.getString("descricao")
                );

                relatorio.setStatus(
                        rs.getString("status")
                );

                relatorio.setNomeAluno(
                        rs.getString("nome_aluno")
                );

                relatorio.setEmailAluno(
                        rs.getString("email_aluno")
                );

                if (rs.getTimestamp(
                        "data_criacao") != null) {

                    relatorio.setDataCriacao(
                            rs.getTimestamp(
                                    "data_criacao"
                            ).toLocalDateTime()
                    );
                }

                relatorios.add(relatorio);
            }
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return relatorios;
}
   
}