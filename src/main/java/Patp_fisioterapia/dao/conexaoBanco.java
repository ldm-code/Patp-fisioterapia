package Patp_fisioterapia.dao;



import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import Patp_fisioterapia.config.Config;

public class conexaoBanco {

    // 🔹 guarda conexões abertas
    private static final List<Connection> conexoes = new ArrayList<>();

    // 🔹 conexão principal (opcional, mas melhora performance)
    private static Connection conexaoUnica;

    public static Connection conectar() {

        try {
            String url = Config.get("spring.datasource.url");
            String user = Config.get("spring.datasource.username");
            String password = Config.get("spring.datasource.password");

           
            if (conexaoUnica != null && !conexaoUnica.isClosed()) {
                return conexaoUnica;
            }

            Connection conn = DriverManager.getConnection(url, user, password);

            conexaoUnica = conn;
            conexoes.add(conn);

            System.out.println("Conectou com sucesso");

            return conn;

        } catch (Exception e) {
            System.out.println("Erro ao conectar ao banco de dados");
            e.printStackTrace();
            return null;
        }
    }

    
    public static void fecharTudo() {

        

        for (Connection conn : conexoes) {

            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        conexoes.clear();
        conexaoUnica = null;

      
    }
}
