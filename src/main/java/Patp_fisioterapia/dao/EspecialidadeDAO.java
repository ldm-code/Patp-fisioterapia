package Patp_fisioterapia.dao;

import Patp_fisioterapia.dto.EspecialidadeDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadeDAO {


public List<EspecialidadeDTO> listarEspecialidades() {

    List<EspecialidadeDTO> especialidades = new ArrayList<>();

    String sql = """
            SELECT id, nome
            FROM especialidades;
            """;

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql);
        ResultSet resultado = stmt.executeQuery()
    ) {

        while (resultado.next()) {

            EspecialidadeDTO especialidade =
                    new EspecialidadeDTO();

            especialidade.setId(
                    resultado.getInt("id")
            );

            especialidade.setNome(
                    resultado.getString("nome")
            );

            especialidades.add(especialidade);
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return especialidades;
}


}

