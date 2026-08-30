package Patp_fisioterapia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import Patp_fisioterapia.dao.conexaoBanco;

public class professorDao {

    public static boolean inserirProfessor(String nome, String email, String senha) {

        String sql = "INSERT INTO professores (nome, email, senha) VALUES (?, ?, ?)";

        try (
            Connection conn = conexaoBanco.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, senha);

            int linhasAfetadas = pstmt.executeUpdate();

            System.out.println("Linhas inseridas: " + linhasAfetadas);

            return linhasAfetadas > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
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