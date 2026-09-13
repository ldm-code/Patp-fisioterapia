package Patp_fisioterapia.dao;

import Patp_fisioterapia.dto.HorarioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HorarioDAO {

    public boolean cadastrarHorario(HorarioDTO horario) {

        String sql = """
                INSERT INTO horarios
                (horario, turno_id)
                VALUES (?, ?);
                """;

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setTime(1, Time.valueOf(horario.getHorario() + ":00"));
            stmt.setInt(2, horario.getTurnoId());

            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<HorarioDTO> listarHorarios() {

        String sql = """
                SELECT id, horario, turno_id
                FROM horarios
                ORDER BY turno_id, horario;
                """;

        List<HorarioDTO> horarios = new ArrayList<>();

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                HorarioDTO horario = new HorarioDTO();

                horario.setId(rs.getInt("id"));
                horario.setHorario(rs.getTime("horario").toString());
                horario.setTurnoId(rs.getInt("turno_id"));

                horarios.add(horario);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return horarios;
    }


    public boolean existeHorario(HorarioDTO horario) {

        String sql = """
                SELECT 1
                FROM horarios
                WHERE horario = ?
                AND turno_id = ?
                LIMIT 1;
                """;

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setTime(1, Time.valueOf(horario.getHorario() + ":00"));
            stmt.setInt(2, horario.getTurnoId());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}