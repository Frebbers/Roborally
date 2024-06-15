package dk.dtu.compute.se.pisd.roborally.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class AppConfig {

    private static Properties properties = new Properties();

    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("Sorry, unable to find application.properties");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream("src/main/resources/application.properties")) {
            properties.store(output, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
