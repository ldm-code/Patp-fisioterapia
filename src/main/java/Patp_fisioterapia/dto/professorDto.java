package Patp_fisioterapia.dto;

public class professorDto {
    private String email;
    private String senha;
    private String nome;
    private int id;
    public int getId(){
      return id;
    }
    public void setId(int id){
      this.id=id;
    }

    public String getNome(){
          return nome;
}
    public void setNome(String nome){
          this.nome=nome;
    }
    public String getEmail() { 
          return email;
     }
    public void setEmail(String email) { 
          this.email = email;
     }
    public String getSenha() { 
          return senha;
 }
    public void setSenha(String senha) { 
          this.senha = senha;
 }
}
