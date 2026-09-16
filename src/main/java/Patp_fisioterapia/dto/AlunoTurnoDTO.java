package Patp_fisioterapia.dto;

public class AlunoTurnoDTO {
          private int idAluno;
          private int idTurno;
          private String nomeTurno;
          public void setNomeTurno(String nomeTurno){
                    this.nomeTurno=nomeTurno;
          }
          public String getNomeTurno() {
                     return nomeTurno;
          }
          public int getTurno(){
                    return idTurno;
          }
          public void setTurno(int idTurno){
                    this.idTurno=idTurno;
          }
          public int getAluno(){
                    return idAluno;
          }
          public void setAluno(int idAluno){
                    this.idAluno=idAluno;
          }
}
