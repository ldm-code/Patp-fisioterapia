package Patp_fisioterapia.dto;

public class ProfessorCadastroRespostaDTO {

    private String mensagem;
    private int idProfessor;

    public ProfessorCadastroRespostaDTO(String mensagem, int idProfessor) {
        this.mensagem = mensagem;
        this.idProfessor = idProfessor;
    }

    public String getMensagem() {
        return mensagem;
    }

    public int getIdProfessor() {
        return idProfessor;
    }
}
