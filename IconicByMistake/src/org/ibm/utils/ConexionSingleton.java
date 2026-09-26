
package org.ibm.utils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Gregory Jerónimo
 */
public class ConexionSingleton {

    private static ConexionSingleton instancia;

    private ConexionSingleton() {
        System.out.println("Conexión inicializada con éxito.");
    }

    public static synchronized ConexionSingleton getInstancia() {
        if (instancia == null) {
            instancia = new ConexionSingleton();
        }
        return instancia;
    }

    public Connection conectar() throws SQLException {
        return Conexion.getInstancia().conectar();
    }
}