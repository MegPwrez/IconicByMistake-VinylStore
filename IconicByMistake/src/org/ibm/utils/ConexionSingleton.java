package org.ibm.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionSingleton {

    private static ConexionSingleton instancia;
    // La barra al inicio ("/") le indica que busque directamente en la raíz de Source Packages (default package)
    private static final String CONFIG_FILE = "/sql.properties";

    private final String url;
    private final String user;
    private final String password;

    private ConexionSingleton() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error Driver: " + e.getMessage());
        }

        Properties config = new Properties();
        try (InputStream in = ConexionSingleton.class.getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException(
                        "No se encontró " + CONFIG_FILE + " en el classpath. "
                        + "Asegúrate de que esté en la raíz de Source Packages.");
            }
            config.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Error al leer " + CONFIG_FILE, e);
        }

        this.url = config.getProperty("db.url");
        this.user = config.getProperty("db.user");
        this.password = config.getProperty("db.password");
        
        if (url == null || user == null || password == null) {
            throw new IllegalStateException(
                    "Faltan propiedades (db.url, db.user, db.password) en " + CONFIG_FILE);
        }
    }

    public static synchronized ConexionSingleton getInstancia() {
        if (instancia == null) {
            instancia = new ConexionSingleton();
        }
        return instancia;
    }

    public static Connection getConexion() {
        try {
            ConexionSingleton gestor = getInstancia();
            return DriverManager.getConnection(gestor.url, gestor.user, gestor.password);
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            return null;
        }
    }
}