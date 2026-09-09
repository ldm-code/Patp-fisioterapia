package Patp_fisioterapia.dto;

import java.util.ArrayList;

import java.util.List;

public class AlunoDTO {
    private int id;
    private String senha;
    private String cpf;
    private String nome;
    private String email;
    private String tipo;
    private List<EspecialidadeDTO> especialidades=new ArrayList<>();;

    public List<EspecialidadeDTO> getEspecialidades() {
      return especialidades;
      }

    public void setEspecialidades(List<EspecialidadeDTO> especialidades) {
      this.especialidades = especialidades;
      }
    public int getId() {
          return id;
          }

    public void setId(int id) {
          this.id = id;
          }
    public String getSenha(){
          return senha;
    }
    public void setSenha(String senha){
          this.senha=senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getTipo(){
          return tipo;
    }
    public void setTipo(String tipo){
          this.tipo=tipo;
    }
    public String getEmail(){
          return email;
    }
    public void setEmail(String email){
          this.email=email;
    }

}
