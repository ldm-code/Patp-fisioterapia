package Patp_fisioterapia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AlunosEspecialidadeDAO {


          public boolean cadastrar(int idAluno, int idEspecialidade) {

          String sql = """
                    INSERT INTO alunos_especialidades
                    (idAluno, idEspecialidade)
                    VALUES (?, ?);
                    """;

          try (
          Connection conexao = conexaoBanco.conectar();
          PreparedStatement stmt = conexao.prepareStatement(sql)
          ) {

          stmt.setInt(1, idAluno);
          stmt.setInt(2, idEspecialidade);

          stmt.executeUpdate();

          return true;

          } catch (Exception e) {

          e.printStackTrace();

          return false;
          }
          }
          public boolean remover(int idAluno, int idEspecialidade) {

    String sql = """
            DELETE FROM alunos_especialidades
            WHERE idAluno = ?
            AND idEspecialidade = ?;
            """;

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, idAluno);
        stmt.setInt(2, idEspecialidade);

        stmt.executeUpdate();

        return true;

    } catch (Exception e) {

        e.printStackTrace();

        return false;
    }
}
public boolean existe(int idAluno, int idEspecialidade) {

    String sql = """
            SELECT 1
            FROM alunos_especialidades
            WHERE idAluno = ?
            AND idEspecialidade = ?;
            """;

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, idAluno);
        stmt.setInt(2, idEspecialidade);

        return stmt.executeQuery().next();

    } catch (Exception e) {

        e.printStackTrace();

        return false;
    }
}


}

