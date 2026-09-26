
package org.ibm.utils;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;



public class Conexion {

    private static Conexion instancia;
    private String url;
    private String user;
    private String password;

    private Conexion() {
        cargarPropiedades();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error Driver: " + e.getMessage());
        }
    }

    private void cargarPropiedades() {
        Properties properties = new Properties();
        try (InputStream input = Conexion.class.getResourceAsStream("/sql.properties")) {

            if (input == null) {
                System.err.println("Error: No se encontró el archivo sql.properties en src/");
                return;
            }

            properties.load(input);

            this.url = properties.getProperty("db.url");
            this.user = properties.getProperty("db.user");
            this.password = properties.getProperty("db.password");

        } catch (IOException ex) {
            System.err.println("Error al leer el archivo de propiedades: " + ex.getMessage());
        }
    }

    public static synchronized Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}