package Patp_fisioterapia.service;

import Patp_fisioterapia.dao.AlunosEspecialidadeDAO;

public class AlunosEspecialidadeService {


          private AlunosEspecialidadeDAO alunosEspecialidadeDAO =
          new AlunosEspecialidadeDAO();

          public boolean cadastrar(int idAluno, int idEspecialidade) {

          if (idAluno <= 0 || idEspecialidade <= 0) {
          return false;
          }

          return alunosEspecialidadeDAO.cadastrar(
                    idAluno,
                    idEspecialidade
          );
          }


}
