package org.ibm.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ibm.dao.FacturaDAO;
import org.ibm.exception.DaoException;
import org.ibm.model.DetalleVenta;
import org.ibm.model.Factura;
import org.ibm.utils.Conexion;

public class FacturaDAOImpl implements FacturaDAO {

    @Override
    public List<Factura> buscarFactura(int noVenta) {
        List<Factura> listaFacturas = new ArrayList<>();
        String sql = "{call sp_buscar_factura(?)}";

        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {

            consulta.setInt(1, noVenta);

            try (ResultSet rs = consulta.executeQuery()) {
                Factura facturaActual = null;

                while (rs.next()) {
                    if (facturaActual == null) {
                        facturaActual = new Factura();
                        facturaActual.setNumeroFactura(rs.getInt("numero_factura"));
                        facturaActual.setFechaEmision(rs.getString("fecha_emision"));
                        facturaActual.setCuiCliente(rs.getLong("cui_cliente"));
                        facturaActual.setNombreCliente(rs.getString("nombre_cliente"));
                        facturaActual.setCorreoCliente(rs.getString("correo_cliente"));
                        facturaActual.setUsuarioAtendio(rs.getString("usuario_atendio"));
                        facturaActual.setGranTotal(rs.getDouble("gran_total"));
                    }

                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setCodigoBarras(rs.getString("codigo_vinilo"));
                    detalle.setTituloAlbum(rs.getString("descripcion_album"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    detalle.setSubTotal(rs.getDouble("subtotal_item"));

                    facturaActual.agregarDetalle(detalle);  
                }

                if (facturaActual != null) {
                    listaFacturas.add(facturaActual);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar la factura: " + e.getMessage(), e);
        }

        return listaFacturas;
    }

    @Override
public List<Factura> obtenerVentasDelDia() {
    List<Factura> listaFacturas = new ArrayList<>();
    String sql = "{call sp_resumen_dia()}";

    try (Connection conexion = Conexion.getInstancia().conectar();
         CallableStatement consulta = conexion.prepareCall(sql);
         ResultSet rs = consulta.executeQuery()) {

        Factura facturaActual = null;
        int ultimoNoFactura = -1;

        while (rs.next()) {
            int noFactura = rs.getInt("numero_factura");
            if (facturaActual == null || noFactura != ultimoNoFactura) {
                if (facturaActual != null) {
                    listaFacturas.add(facturaActual);
                }
                facturaActual = new Factura();
                facturaActual.setNumeroFactura(noFactura);
                facturaActual.setFechaEmision(rs.getString("fecha_emision"));
                facturaActual.setCuiCliente(rs.getLong("cui_cliente"));
                facturaActual.setNombreCliente(rs.getString("nombre_cliente"));
                facturaActual.setUsuarioAtendio(rs.getString("usuario_atendio"));
                facturaActual.setGranTotal(rs.getDouble("gran_total"));
                ultimoNoFactura = noFactura;
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setCodigoBarras(rs.getString("codigo_vinilo"));
            detalle.setTituloAlbum(rs.getString("titulo_album"));
            detalle.setCantidad(rs.getInt("cantidad"));
            detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
            detalle.setSubTotal(rs.getDouble("subtotal"));

            facturaActual.agregarDetalle(detalle);
        }

        if (facturaActual != null) {
            listaFacturas.add(facturaActual);
        }

    } catch (SQLException e) {
        throw new DaoException("Error al obtener el resumen del día: " + e.getMessage(), e);
    }

    return listaFacturas;
}
  }