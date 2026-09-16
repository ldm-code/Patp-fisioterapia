package Patp_fisioterapia.dao;
import Patp_fisioterapia.dto.AlunoDTO;
import Patp_fisioterapia.dto.EspecialidadeDTO;
import Patp_fisioterapia.dto.AlunoTurnoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class AlunoTurnosDao {
          public boolean cadastrar(int idAluno, int idTurno) {
          
                     String sql = """
                    INSERT INTO aluno_turnos
                    (id_aluno, id_turno)
                    VALUES (?, ?);
                    """;

          try (
          Connection conexao = conexaoBanco.conectar();
          PreparedStatement stmt = conexao.prepareStatement(sql)
          ) {

          stmt.setInt(1, idAluno);
          stmt.setInt(2, idTurno);
           stmt.executeUpdate();

          return true;

          } catch (Exception e) {

          e.printStackTrace();

          return false;
          }
}
public boolean remover(int idAluno, int idTurno) {

    String sql = """
            DELETE FROM aluno_turnos
            WHERE id_aluno = ?
            AND id_turno = ?;
            """;

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, idAluno);
        stmt.setInt(2, idTurno);

        stmt.executeUpdate();

        return true;

    } catch (Exception e) {

        e.printStackTrace();

        return false;
    }
}
public boolean existe(int idAluno, int idTurno) {

    String sql = """
            SELECT 1
            FROM alunos_especialidades
            WHERE id_aluno = ?
            AND id_aurno = ?;
            """;

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, idAluno);
        stmt.setInt(2, idTurno);

        return stmt.executeQuery().next();

    } catch (Exception e) {

        e.printStackTrace();

        return false;
    }
}
public List<AlunoTurnoDTO> listarPorAluno(int idAluno) {

    List<AlunoTurnoDTO> turnos = new ArrayList<>();

    String sql = """
        SELECT
            at.id_aluno,
            t.id AS id_turno,
            t.nome AS nome_turno
        FROM aluno_turnos at
        INNER JOIN turnos t
            ON t.id = at.id_turno
        WHERE at.id_aluno = ?
        ORDER BY t.id;
        """;

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, idAluno);

        try (ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                AlunoTurnoDTO turno = new AlunoTurnoDTO();

                turno.setAluno(rs.getInt("id_aluno"));
                turno.setTurno(rs.getInt("id_turno"));
                turno.setNomeTurno(rs.getString("nome_turno"));

                turnos.add(turno);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return turnos;
}

}
