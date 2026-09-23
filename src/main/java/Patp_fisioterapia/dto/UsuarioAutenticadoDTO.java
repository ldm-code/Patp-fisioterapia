package Patp_fisioterapia.dto;

public class UsuarioAutenticadoDTO {

    private String email;
    private String tipo;
    private int id;

          // Construtor usado quando não é informado um ID
public UsuarioAutenticadoDTO(String email, String tipo) {

          this.email = email;
          this.tipo = tipo;
          }

          // Construtor que recebe e armazena o ID
public UsuarioAutenticadoDTO(
          String email,
          String tipo,
          int id) {

          this.email = email;
          this.tipo = tipo;
          this.id = id;
          }

    public int getId(){
          return id;
    }

    public String getEmail() {
        return email;
    }

    public String getTipo() {
        return tipo;
    }
}