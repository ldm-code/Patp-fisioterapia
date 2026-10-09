
package Patp_fisioterapia.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import Patp_fisioterapia.config.Config;

public class conexaoBanco {

    public static Connection conectar() {

        try {
            String url = Config.get("spring.datasource.url");
            String user = Config.get("spring.datasource.username");
            String password = Config.get("spring.datasource.password");

            Connection conn = DriverManager.getConnection(
                url,
                user,
                password
            );

            System.out.println("Conectou com sucesso");

            return conn;

        } catch (Exception e) {
            System.out.println("Erro ao conectar ao banco de dados");
            e.printStackTrace();
            throw new RuntimeException(
                "Não foi possível conectar ao banco de dados", e
            );
        }
    }
}
