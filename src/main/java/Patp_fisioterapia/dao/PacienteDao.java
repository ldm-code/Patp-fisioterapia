package Patp_fisioterapia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Patp_fisioterapia.dto.pacienteDto;
import Patp_fisioterapia.dao.conexaoBanco;

public class PacienteDao {

    // ============================================================
    // CADASTRAR PACIENTE
    // ============================================================

    public static boolean cadastrarPaciente(
            String nome,
            String cpf,
            String telefone) {

        String sql = """
                INSERT INTO pacientes (nome, cpf, telefone)
                VALUES (?, ?, ?)
                """;

        // Validação dos campos
        if (nome == null || nome.trim().isEmpty()
                || cpf == null || cpf.trim().isEmpty()
                || telefone == null || telefone.trim().isEmpty()) {

            return false;
        }

        try (
            Connection conn = conexaoBanco.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, nome);
            pstmt.setString(2, cpf);
            pstmt.setString(3, telefone);

            int linhasAfetadas = pstmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }


    // ============================================================
    // BUSCAR TODOS OS PACIENTES
    // ============================================================

    public static List<pacienteDto> buscarPacientes() {

        String sql = """
                SELECT id, nome, cpf, telefone
                FROM pacientes
                """;

        List<pacienteDto> pacientes = new ArrayList<>();

        try (
            Connection conn = conexaoBanco.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                pacienteDto pacienteDto = new pacienteDto();

                pacienteDto.setId(rs.getInt("id"));
                pacienteDto.setNome(rs.getString("nome"));
                pacienteDto.setCpf(rs.getString("cpf"));
                pacienteDto.setTelefone(rs.getString("telefone"));

                pacientes.add(pacienteDto);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return pacientes;
    }


    // ============================================================
    // EDITAR PACIENTE
    // ============================================================

    public static boolean editarDados(
            int id,
            String nome,
            String cpf,
            String telefone) {

        String sql = """
                UPDATE pacientes
                SET nome = ?, cpf = ?, telefone = ?
                WHERE id = ?
                """;

        // Validação do ID
        if (id <= 0) {
            return false;
        }

        // Validação dos campos
        if (nome == null || nome.trim().isEmpty()
                || cpf == null || cpf.trim().isEmpty()
                || telefone == null || telefone.trim().isEmpty()) {

            return false;
        }

        try (
            Connection conn = conexaoBanco.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, nome);
            pstmt.setString(2, cpf);
            pstmt.setString(3, telefone);
            pstmt.setInt(4, id);

            int linhasAfetadas = pstmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }


    // ============================================================
    // BUSCAR PACIENTE POR ID
    // ============================================================

    public static pacienteDto buscarPacientePorId(int id) {

        if (id <= 0) {
            return null;
        }

        String sql = """
                SELECT id, nome, cpf, telefone
                FROM pacientes
                WHERE id = ?
                """;

        try (
            Connection conn = conexaoBanco.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    pacienteDto pacienteDto = new pacienteDto();

                    pacienteDto.setId(rs.getInt("id"));
                    pacienteDto.setNome(rs.getString("nome"));
                    pacienteDto.setCpf(rs.getString("cpf"));
                    pacienteDto.setTelefone(rs.getString("telefone"));

                    return pacienteDto;
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    // ============================================================
    // BUSCAR POR FILTRO
    // ============================================================

    public static List<pacienteDto> buscarPorFiltro(
            String nome,
            String cpf) {

        StringBuilder sql = new StringBuilder("""
                SELECT id, nome, cpf, telefone
                FROM pacientes
                WHERE 1 = 1
                """);

        List<String> parametros = new ArrayList<>();

        if (nome != null && !nome.trim().isEmpty()) {

            sql.append(" AND nome LIKE ?");
            parametros.add("%" + nome.trim() + "%");
        }

        if (cpf != null && !cpf.trim().isEmpty()) {

            sql.append(" AND cpf LIKE ?");
            parametros.add("%" + cpf.trim() + "%");
        }

        List<pacienteDto> pacientes = new ArrayList<>();

        try (
            Connection conn = conexaoBanco.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())
        ) {

            for (int i = 0; i < parametros.size(); i++) {

                pstmt.setString(i + 1, parametros.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    pacienteDto pacienteDto = new pacienteDto();

                    pacienteDto.setId(rs.getInt("id"));
                    pacienteDto.setNome(rs.getString("nome"));
                    pacienteDto.setCpf(rs.getString("cpf"));
                    pacienteDto.setTelefone(rs.getString("telefone"));

                    pacientes.add(pacienteDto);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return pacientes;
    }
}

