
package org.ibm.exception;

/**
 * Excepción de la capa de acceso a datos.
 *
 * Se lanza cuando una operación contra la base de datos (vía procedimiento
 * almacenado) falla por un error real: conexión caída, SQL inválido, etc.
 *
 * A diferencia del enfoque anterior —devolver false/null y solo imprimir en
 * consola—, propagar DaoException permite que el controlador muestre al
 * usuario un mensaje de error claro en lugar de fallar en silencio.
 */
public class DaoException extends RuntimeException {

    public DaoException(String mensaje) {
        super(mensaje);
    }

    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}