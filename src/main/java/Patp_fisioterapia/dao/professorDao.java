package Patp_fisioterapia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import Patp_fisioterapia.dao.conexaoBanco;

public class professorDao {

    public static int inserirProfessor(String nome, String email, String senha) {

    String sql = "INSERT INTO professores (nome, email, senha) VALUES (?, ?, ?)";

    try (
        Connection conn = conexaoBanco.conectar();
        PreparedStatement pstmt = conn.prepareStatement(
            sql,
            Statement.RETURN_GENERATED_KEYS
        )
    ) {

        pstmt.setString(1, nome);
        pstmt.setString(2, email);
        pstmt.setString(3, senha);

        int linhasAfetadas = pstmt.executeUpdate();

        if (linhasAfetadas > 0) {
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0;
}
    public static String buscarTipoPorEmail(String email) {

    String sql = """
            SELECT tipo
            FROM professores
            WHERE email = ?
            """;

    try (
        Connection conn = conexaoBanco.conectar();
        PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {

        pstmt.setString(1, email);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("tipo");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}

public static int buscarIdPorEmail(String email) {

    String sql = """
            SELECT id
            FROM professores
            WHERE email = ?
            """;

    try (
        Connection conn = conexaoBanco.conectar();
        PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {

        pstmt.setString(1, email);

        try (ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("id");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0;
}
       public static String buscarSenhaPorEmail(String email) {

        String sql = "SELECT senha FROM professores WHERE email = ?";

        try (
            Connection conn = conexaoBanco.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, email);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("senha");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}