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
    public AlunoDTO buscarAlunoLogin(String email) {

    String sql = """
            SELECT id, nome, email, senha, tipo
            FROM alunos
            WHERE email = ?
            """;

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {

        stmt.setString(1, email);

        try (ResultSet resultado = stmt.executeQuery()) {

            if (resultado.next()) {

                AlunoDTO aluno = new AlunoDTO();

                aluno.setId(resultado.getInt("id"));
                aluno.setNome(resultado.getString("nome"));
                aluno.setEmail(resultado.getString("email"));
                aluno.setSenha(resultado.getString("senha"));
                aluno.setTipo(resultado.getString("tipo"));

                return aluno;
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}
public List<AlunoDTO> listarAlunosPorProfessor(int idProfessor) {

    Map<Integer, AlunoDTO> alunos = new LinkedHashMap<>();

    String sql = """
            SELECT DISTINCT
                a.id,
                a.nome,
                a.cpf,
                a.email,
                a.tipo
            FROM alunos a
            INNER JOIN alunos_especialidades ae
                ON a.id = ae.idAluno
            INNER JOIN especialidades e
                ON ae.idEspecialidade = e.id
            INNER JOIN professores_especializacao pe
                ON pe.especialidade_id = e.id
            WHERE pe.professor_id = ?;
            """;

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, idProfessor);

        try (ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {

                AlunoDTO aluno = new AlunoDTO();

                aluno.setId(resultado.getInt("id"));
                aluno.setNome(resultado.getString("nome"));
                aluno.setCpf(resultado.getString("cpf"));
                aluno.setEmail(resultado.getString("email"));
                aluno.setTipo(resultado.getString("tipo"));

                alunos.put(aluno.getId(), aluno);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return new ArrayList<>(alunos.values());
}
public List<AlunoDTO> selecionarPorEmailProfessor(
        String email, int idProfessor) {

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
        INNER JOIN alunos_especialidades ae
            ON a.id = ae.idAluno
        INNER JOIN especialidades e
            ON ae.idEspecialidade = e.id
        INNER JOIN professores_especializacao pe
            ON pe.especialidade_id = e.id
        WHERE a.email LIKE ?
          AND pe.professor_id = ?;
        """;

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {
        stmt.setString(1, "%" + email + "%");
        stmt.setInt(2, idProfessor);

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

                int idEspecialidade =
                        resultado.getInt("idEspecialidade");

                boolean especialidadeJaAdicionada =
                        aluno.getEspecialidades().stream()
                        .anyMatch(e ->
                            e.getId() == idEspecialidade
                        );

                if (!especialidadeJaAdicionada) {
                    EspecialidadeDTO especialidade =
                            new EspecialidadeDTO();

                    especialidade.setId(idEspecialidade);
                    especialidade.setNome(
                        resultado.getString("nomeEspecialidade")
                    );

                    aluno.getEspecialidades().add(especialidade);
                }
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return new ArrayList<>(alunos.values());
}

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
        public AlunoDTO buscarAlunoParaEdicao(int id) {

    String sql = """
            SELECT id, nome, cpf, email, tipo
            FROM alunos
            WHERE id = ?
            """;

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {

        stmt.setInt(1, id);

        try (ResultSet resultado = stmt.executeQuery()) {

            if (resultado.next()) {

                AlunoDTO aluno = new AlunoDTO();

                aluno.setId(resultado.getInt("id"));
                aluno.setNome(resultado.getString("nome"));
                aluno.setCpf(resultado.getString("cpf"));
                aluno.setEmail(resultado.getString("email"));
                aluno.setTipo(resultado.getString("tipo"));

                return aluno;
            }
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return null;
}


public boolean atualizarAluno(AlunoDTO aluno) {

    String sql;

    if (aluno.getSenha() != null
            && !aluno.getSenha().trim().isEmpty()) {

        sql = """
                UPDATE alunos
                SET nome = ?, cpf = ?, email = ?, tipo = ?, senha = ?
                WHERE id = ?
                """;

    } else {

        sql = """
                UPDATE alunos
                SET nome = ?, cpf = ?, email = ?, tipo = ?
                WHERE id = ?
                """;
    }

    try (
        Connection conexao = conexaoBanco.conectar();
        PreparedStatement stmt = conexao.prepareStatement(sql)
    ) {

        stmt.setString(1, aluno.getNome());
        stmt.setString(2, aluno.getCpf());
        stmt.setString(3, aluno.getEmail());
        stmt.setString(4, aluno.getTipo());

        if (aluno.getSenha() != null
                && !aluno.getSenha().trim().isEmpty()) {

            stmt.setString(5, aluno.getSenha());
            stmt.setInt(6, aluno.getId());

        } else {

            stmt.setInt(5, aluno.getId());
        }

        return stmt.executeUpdate() > 0;

    } catch (Exception e) {

        e.printStackTrace();

        return false;
    }
}

}

