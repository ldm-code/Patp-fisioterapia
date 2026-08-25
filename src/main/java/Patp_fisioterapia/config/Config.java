package Patp_fisioterapia.config;

import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static Properties props = new Properties();

    static {
        try {
            InputStream file = Config.class.getResourceAsStream("/config.properties");

            if (file == null) {
                throw new RuntimeException("config.properties não encontrado no classpath");
            }

            props.load(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
