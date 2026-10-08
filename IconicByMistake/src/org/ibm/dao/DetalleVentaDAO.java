
package org.ibm.dao;
import java.util.List;

import org.ibm.model.DetalleVenta;

public interface DetalleVentaDAO {
      List<DetalleVenta> listar();
    DetalleVenta buscar (int idDetalleventa);
    boolean insertar(DetalleVenta detalleVenta);
    boolean actualizar(DetalleVenta detalleVenta);
    boolean eliminar(int idDetalleventa);   
}