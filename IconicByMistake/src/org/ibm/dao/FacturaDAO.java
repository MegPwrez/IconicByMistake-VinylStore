package org.ibm.dao;

import java.util.List;
import org.ibm.model.Factura;

public interface FacturaDAO {
    List<Factura> buscarFactura(int noVenta);
    List<Factura> obtenerVentasDelDia();
}