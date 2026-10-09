package Patp_fisioterapia.dao;

import Patp_fisioterapia.dto.AlunoDTO;
import Patp_fisioterapia.dto.ConsultaDTO;
import Patp_fisioterapia.dto.pacienteDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class ConsultaDAO {

    public boolean cadastrar(ConsultaDTO consulta) {

        String sql = """
                INSERT INTO consultas
                (aluno_id, paciente, motivo, data_consulta, diagnostico)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, consulta.getAlunoId());
            stmt.setInt(2, consulta.getPaciente());
            stmt.setString(3, consulta.getMotivo());
            stmt.setTimestamp(
                    4,
                    Timestamp.valueOf(consulta.getDataConsulta())
            );
            stmt.setString(5, consulta.getDiagnostico());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ConsultaDTO> listar() {

        String sql = """
                SELECT
                    c.id,
                    c.aluno_id,
                    c.paciente,
                    c.motivo,
                    c.data_agendada,
                    c.data_consulta,
                    c.status,
                    c.diagnostico,
                    a.nome AS nome_aluno,
                    p.nome AS nome_paciente,
                    EXISTS (
                        SELECT 1
                        FROM relatorios r
                        WHERE r.consulta_id = c.id
                    ) AS tem_relatorio
                FROM consultas c
                INNER JOIN alunos a
                    ON a.id = c.aluno_id
                INNER JOIN pacientes p
                    ON p.id = c.paciente
                ORDER BY c.data_consulta DESC
                """;

        List<ConsultaDTO> consultas = new ArrayList<>();

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                ConsultaDTO consulta = preencherConsulta(rs);

                consultas.add(consulta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return consultas;
    }

    public ConsultaDTO buscarPorId(int id) {

        String sql = """
                SELECT
                    c.id,
                    c.aluno_id,
                    c.paciente,
                    c.motivo,
                    c.data_agendada,
                    c.data_consulta,
                    c.status,
                    c.diagnostico,
                    a.nome AS nome_aluno,
                    p.nome AS nome_paciente,
                    EXISTS (
                        SELECT 1
                        FROM relatorios r
                        WHERE r.consulta_id = c.id
                    ) AS tem_relatorio
                FROM consultas c
                INNER JOIN alunos a
                    ON a.id = c.aluno_id
                INNER JOIN pacientes p
                    ON p.id = c.paciente
                WHERE c.id = ?
                """;

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return preencherConsulta(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean atualizar(ConsultaDTO consulta) {

        String sql = """
                UPDATE consultas
                SET aluno_id = ?,
                    paciente = ?,
                    motivo = ?,
                    data_consulta = ?,
                    diagnostico = ?
                WHERE id = ?
                AND status NOT IN ('cancelada', 'concluida')
                """;

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, consulta.getAlunoId());
            stmt.setInt(2, consulta.getPaciente());
            stmt.setString(3, consulta.getMotivo());
            stmt.setTimestamp(
                    4,
                    Timestamp.valueOf(consulta.getDataConsulta())
            );
            stmt.setString(5, consulta.getDiagnostico());
            stmt.setInt(6, consulta.getId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarStatus(int id, String status) {

        String sql = """
                UPDATE consultas
                SET status = ?
                WHERE id = ?
                """;

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AlunoDTO> buscarAlunosPorNome(String nome) {

        String sql = """
                SELECT id, nome, cpf, email, tipo
                FROM alunos
                WHERE nome LIKE ?
                ORDER BY nome
                LIMIT 10
                """;

        List<AlunoDTO> alunos = new ArrayList<>();

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, "%" + nome.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    AlunoDTO aluno = new AlunoDTO();

                    aluno.setId(rs.getInt("id"));
                    aluno.setNome(rs.getString("nome"));
                    aluno.setCpf(rs.getString("cpf"));
                    aluno.setEmail(rs.getString("email"));
                    aluno.setTipo(rs.getString("tipo"));

                    alunos.add(aluno);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return alunos;
    }

    public List<pacienteDto> buscarPacientesPorNome(String nome) {

        String sql = """
                SELECT id, nome, cpf, telefone
                FROM pacientes
                WHERE nome LIKE ?
                ORDER BY nome
                LIMIT 10
                """;

        List<pacienteDto> pacientes = new ArrayList<>();

        try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, "%" + nome.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    pacienteDto paciente = new pacienteDto();

                    paciente.setId(rs.getInt("id"));
                    paciente.setNome(rs.getString("nome"));
                    paciente.setCpf(rs.getString("cpf"));
                    paciente.setTelefone(rs.getString("telefone"));

                    pacientes.add(paciente);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pacientes;
    }

  public List<String> buscarHorariosDisponiveisDoAluno(
        int idAluno,
        LocalDate data
) {

    String sql = """
            SELECT DISTINCT
                TIME_FORMAT(h.horario, '%H:%i') AS horario
            FROM aluno_turnos at
            INNER JOIN horarios h
                ON h.turno_id = at.id_turno
            WHERE at.id_aluno = ?

            AND (
                ? > CURDATE()
                OR (
                    ? = CURDATE()
                    AND h.horario > CURTIME()
                )
            )

            AND NOT EXISTS (
                SELECT 1
                FROM consultas c
                WHERE c.aluno_id = at.id_aluno
                  AND DATE(c.data_consulta) = ?
                  AND TIME(c.data_consulta) = h.horario
                  AND c.status = 'agendada'
            )

            ORDER BY horario
            """;

    List<String> horarios = new ArrayList<>();

    try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt =
                    conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, idAluno);

        java.sql.Date dataSql =
                java.sql.Date.valueOf(data);

        stmt.setDate(2, dataSql);
        stmt.setDate(3, dataSql);
        stmt.setDate(4, dataSql);

        try (ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                horarios.add(
                        rs.getString("horario")
                );
            }
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return horarios;
}
public boolean atualizarEdicao(ConsultaDTO consulta) {

    String sql = """
            UPDATE consultas
            SET aluno_id = ?,
                motivo = ?,
                data_consulta = ?,
                diagnostico = ?
            WHERE id = ?
              AND status = 'agendada'
            """;

    try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {
        stmt.setInt(1, consulta.getAlunoId());
        stmt.setString(2, consulta.getMotivo());
        stmt.setTimestamp(
                3,
                Timestamp.valueOf(consulta.getDataConsulta())
        );
        stmt.setString(4, consulta.getDiagnostico());
        stmt.setInt(5, consulta.getId());

        return stmt.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
public boolean horarioDisponivelParaEdicao(
        int idAluno,
        java.time.LocalDateTime dataConsulta,
        int consultaId) {

    String sql = """
            SELECT 1
            FROM aluno_turnos at
            INNER JOIN horarios h
                ON h.turno_id = at.id_turno
            WHERE at.id_aluno = ?
              AND h.horario = TIME(?)
              AND ? > NOW()
              AND NOT EXISTS (
                  SELECT 1
                  FROM consultas c
                  WHERE c.aluno_id = at.id_aluno
                    AND DATE(c.data_consulta) = DATE(?)
                    AND TIME(c.data_consulta) = h.horario
                    AND c.status = 'agendada'
                    AND c.id <> ?
              )
            LIMIT 1
            """;

    try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {
        Timestamp dataSql = Timestamp.valueOf(dataConsulta);

        stmt.setInt(1, idAluno);
        stmt.setTimestamp(2, dataSql);
        stmt.setTimestamp(3, dataSql);
        stmt.setTimestamp(4, dataSql);
        stmt.setInt(5, consultaId);

        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
public List<String> buscarHorariosDisponiveisParaEdicao(
        int idAluno,
        LocalDate data,
        Integer consultaId) {

    String sql = """
            SELECT DISTINCT
                TIME_FORMAT(h.horario, '%H:%i') AS horario
            FROM aluno_turnos at
            INNER JOIN horarios h
                ON h.turno_id = at.id_turno
            WHERE at.id_aluno = ?
              AND (
                  ? > CURDATE()
                  OR (? = CURDATE() AND h.horario > CURTIME())
              )
              AND NOT EXISTS (
                  SELECT 1
                  FROM consultas c
                  WHERE c.aluno_id = at.id_aluno
                    AND DATE(c.data_consulta) = ?
                    AND TIME(c.data_consulta) = h.horario
                    AND c.status = 'agendada'
                    AND c.id <> ?
              )
            ORDER BY horario
            """;

    List<String> horarios = new ArrayList<>();

    try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {
        java.sql.Date dataSql = java.sql.Date.valueOf(data);

        stmt.setInt(1, idAluno);
        stmt.setDate(2, dataSql);
        stmt.setDate(3, dataSql);
        stmt.setDate(4, dataSql);
        stmt.setInt(5, consultaId);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                horarios.add(rs.getString("horario"));
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return horarios;
}
public List<ConsultaDTO> listarAgendadasPorAlunoSemData(
        int idAluno) {

    String sql = """
            SELECT
                c.id,
                c.aluno_id,
                c.paciente,
                c.motivo,
                c.data_agendada,
                c.data_consulta,
                c.status,
                c.diagnostico,
                a.nome AS nome_aluno,
                p.nome AS nome_paciente,
                EXISTS (
                    SELECT 1
                    FROM relatorios r
                    WHERE r.consulta_id = c.id
                ) AS tem_relatorio
            FROM consultas c
            INNER JOIN alunos a
                ON a.id = c.aluno_id
            INNER JOIN pacientes p
                ON p.id = c.paciente
            WHERE c.aluno_id = ?
              AND c.status = 'agendada'
            ORDER BY c.data_consulta ASC
            """;

    List<ConsultaDTO> consultas = new ArrayList<>();

    try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt =
                    conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, idAluno);

        try (ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                ConsultaDTO consulta =
                        preencherConsulta(rs);

                consultas.add(consulta);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return consultas;
}
public List<ConsultaDTO> listarAgendadasPorAluno(
        int idAluno,
        LocalDate data) {

    String sql = """
            SELECT
                c.id,
                c.aluno_id,
                c.paciente,
                c.motivo,
                c.data_agendada,
                c.data_consulta,
                c.status,
                c.diagnostico,
                a.nome AS nome_aluno,
                p.nome AS nome_paciente,
                EXISTS (
                    SELECT 1
                    FROM relatorios r
                    WHERE r.consulta_id = c.id
                ) AS tem_relatorio
            FROM consultas c
            INNER JOIN alunos a
                ON a.id = c.aluno_id
            INNER JOIN pacientes p
                ON p.id = c.paciente
            WHERE c.aluno_id = ?
              AND c.status = 'agendada'
              AND DATE(c.data_consulta) = ?
            ORDER BY c.data_consulta ASC
            """;

    List<ConsultaDTO> consultas = new ArrayList<>();

    try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt =
                    conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, idAluno);
        stmt.setDate(
                2,
                java.sql.Date.valueOf(data)
        );

        try (ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                ConsultaDTO consulta =
                        preencherConsulta(rs);

                consultas.add(consulta);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return consultas;
}
   public boolean horarioPertenceAoAluno(
        int idAluno,
        java.time.LocalTime horario) {

    String sql = """
            SELECT 1
            FROM aluno_turnos at
            INNER JOIN horarios h
                ON h.turno_id = at.id_turno
            WHERE at.id_aluno = ?
            AND h.horario = ?
            LIMIT 1
            """;

    try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, idAluno);

        stmt.setTime(
                2,
                java.sql.Time.valueOf(horario)
        );

        try (ResultSet rs = stmt.executeQuery()) {

            return rs.next();
        }

    } catch (Exception e) {

        e.printStackTrace();

        return false;
    }
}
    private ConsultaDTO preencherConsulta(ResultSet rs) throws Exception {

        ConsultaDTO consulta = new ConsultaDTO();

        consulta.setId(rs.getInt("id"));
        consulta.setAlunoId(rs.getInt("aluno_id"));
        consulta.setPaciente(rs.getInt("paciente"));
        consulta.setMotivo(rs.getString("motivo"));

        if (rs.getTimestamp("data_agendada") != null) {
            consulta.setDataAgendada(
                    rs.getTimestamp("data_agendada").toLocalDateTime()
            );
        }

        consulta.setDataConsulta(
                rs.getTimestamp("data_consulta").toLocalDateTime()
        );

        consulta.setStatus(rs.getString("status"));
        consulta.setDiagnostico(rs.getString("diagnostico"));

        consulta.setNomeAluno(rs.getString("nome_aluno"));
        consulta.setNomePaciente(rs.getString("nome_paciente"));

        consulta.setTemRelatorio(
                rs.getBoolean("tem_relatorio")
        );

        return consulta;
    }
}