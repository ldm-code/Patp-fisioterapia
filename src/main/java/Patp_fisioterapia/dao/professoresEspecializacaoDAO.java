package Patp_fisioterapia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class professoresEspecializacaoDAO {

    public static boolean inserirVinculo(
            int professorId,
            int especialidadeId) {

        String sql = """
                INSERT INTO professores_especializacao
                (professor_id, especialidade_id)
                VALUES (?, ?)
                """;

        try (
            Connection conn = conexaoBanco.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, professorId);
            stmt.setInt(2, especialidadeId);

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
