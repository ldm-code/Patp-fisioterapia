package Patp_fisioterapia.dto;
public class LoginRequest {
    private String email;
    private String senha;
    private int id;

    // Getters e Setters (Obrigatórios para o Spring mapear os dados)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}

