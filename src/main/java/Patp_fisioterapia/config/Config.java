package Patp_fisioterapia.config; // Nota: Recomenda-se pacotes em minúsculas (patp_fisioterapia.config)

import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static Properties props = new Properties();

    static {
        try {
            // CORREÇÃO: Remove o "/resources" e aponta para a pasta "config" que está dentro de resources
            // Não use a barra "/" no início se estiver usando o ClassLoader diretamente
            InputStream file = Config.class.getClassLoader().getResourceAsStream("config/seu_arquivo.properties");

            if (file == null) {
                // Tratamento caso queira ler o application.properties da raiz como plano de fundo
                file = Config.class.getClassLoader().getResourceAsStream("application.properties");
            }

            if (file == null) {
                throw new RuntimeException("Arquivo de configuração não encontrado no classpath");
            }

            props.load(file);
            file.close(); // Boa prática: fechar o stream após o uso

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
