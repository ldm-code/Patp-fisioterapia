package Patp_fisioterapia.dao;

import Patp_fisioterapia.dto.AlunoDTO;
import Patp_fisioterapia.dto.EspecialidadeDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlunoDAO {

    public boolean cadastrarAluno(AlunoDTO aluno) {

        String sql = """
                INSERT INTO alunos
                (nome, senha, cpf, email, tipo)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getSenha());
            stmt.setString(3, aluno.getCpf());
            stmt.setString(4, aluno.getEmail());
            stmt.setString(5, aluno.getTipo());

            stmt.executeUpdate();

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }



    public List<AlunoDTO> selecionarPorEmail(String email) {

        Map<Integer, AlunoDTO> alunos = new LinkedHashMap<>();

        String sql = """
                SELECT
                a.id,
                a.nome,
                a.cpf,
                a.email,
                a.tipo,
                e.id AS idEspecialidade,
                e.nome AS nomeEspecialidade
            FROM alunos a
            LEFT JOIN alunos_especialidades ae
                ON a.id = ae.idAluno
            LEFT JOIN especialidades e
                ON ae.idEspecialidade = e.id
                WHERE email LIKE ?;
                """;

        try (
            Connection conexao = conexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, "%" + email + "%");

            try (ResultSet resultado = stmt.executeQuery()) {

              while (resultado.next()) {

    int idAluno = resultado.getInt("id");

    AlunoDTO aluno = alunos.get(idAluno);

    if (aluno == null) {

        aluno = new AlunoDTO();

        aluno.setId(idAluno);
        aluno.setNome(resultado.getString("nome"));
        aluno.setCpf(resultado.getString("cpf"));
        aluno.setEmail(resultado.getString("email"));
        aluno.setTipo(resultado.getString("tipo"));

        alunos.put(idAluno, aluno);
    }

    String nomeEspecialidade =
            resultado.getString("nomeEspecialidade");

    if (nomeEspecialidade != null) {

        EspecialidadeDTO especialidade =
                new EspecialidadeDTO();

        especialidade.setId(
                resultado.getInt("idEspecialidade")
        );

        especialidade.setNome(nomeEspecialidade);

        aluno.getEspecialidades().add(especialidade);
    }
}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(alunos.values());
    }




        public List<AlunoDTO> listarAlunos() {

            Map<Integer, AlunoDTO> alunos = new LinkedHashMap<>();

            String sql = """
                    SELECT
                        a.id,
                        a.nome,
                        a.cpf,
                        a.email,
                        a.tipo,
                        e.id AS idEspecialidade,
                        e.nome AS nomeEspecialidade
                    FROM alunos a
                    LEFT JOIN alunos_especialidades ae
                        ON a.id = ae.idAluno
                    LEFT JOIN especialidades e
                        ON ae.idEspecialidade = e.id;
                    """;

            try (
                Connection conexao = conexaoBanco.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet resultado = stmt.executeQuery()
            ) {

                while (resultado.next()) {

                    int idAluno = resultado.getInt("id");

                    AlunoDTO aluno = alunos.get(idAluno);

                    if (aluno == null) {

                        aluno = new AlunoDTO();

                        aluno.setId(idAluno);
                        aluno.setNome(resultado.getString("nome"));
                        aluno.setCpf(resultado.getString("cpf"));
                        aluno.setEmail(resultado.getString("email"));
                        aluno.setTipo(resultado.getString("tipo"));

                        alunos.put(idAluno, aluno);
                    }

                    String nomeEspecialidade =
                            resultado.getString("nomeEspecialidade");

                    if (nomeEspecialidade != null) {

                        EspecialidadeDTO especialidade =
                                new EspecialidadeDTO();

                        especialidade.setId(
                                resultado.getInt("idEspecialidade")
                        );

                        especialidade.setNome(nomeEspecialidade);

                        aluno.getEspecialidades().add(especialidade);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return new ArrayList<>(alunos.values());
        }


}

