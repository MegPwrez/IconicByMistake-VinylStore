package org.ibm.Ventaservice;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import org.ibm.model.DetalleVenta;
import org.ibm.model.Venta;
import org.ibm.utils.Conexion;

public class VentaService {

    public boolean validarStock(String codigoBarras, int cantidad) {
        String sql = "{call sp_buscarvinilo(?)}";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, codigoBarras);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    int stockActual = rs.getInt("stock");
                    return stockActual >= cantidad;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error [Validar Stock]: " + e.getMessage());
        }
        return false;
    }

    public boolean procesarVenta(Venta venta, List<DetalleVenta> detalles) {
        for (DetalleVenta det : detalles) {
            int cantidadRequerida = det.getCantidad() > 0 ? det.getCantidad() : 1;
            if (!validarStock(det.getCodigoBarras(), cantidadRequerida)) {
                System.err.println("Stock insuficiente para el vinilo código: " + det.getCodigoBarras());
                return false;
            }
        }

        Connection con = null;
        try {
            con = Conexion.getInstancia().conectar();
            con.setAutoCommit(false); 

            String sqlVenta = "{call sp_insertarventa(?, ?, ?, ?, ?, ?)}";
            int idVentaGenerado = -1;

            try (CallableStatement csVenta = con.prepareCall(sqlVenta)) {
                double subtotalNum = Double.parseDouble(venta.getSubTotal().replace(",", "."));
                csVenta.setDouble(1, subtotalNum);
                csVenta.setDouble(2, venta.getDescuento());
                csVenta.setDouble(3, venta.getTotalVenta());
                csVenta.setLong(4, venta.getCuiCliente());
                csVenta.setInt(5, venta.getId_usuario());
                csVenta.registerOutParameter(6, Types.INTEGER);

                csVenta.executeUpdate();
                idVentaGenerado = csVenta.getInt(6);
            }

            if (idVentaGenerado <= 0) {
                con.rollback();
                return false;
            }

            venta.setIdVenta(idVentaGenerado); 
            String sqlDetalle = "{call sp_insertardetalleventa(?, ?, ?, ?, ?)}";
            try (CallableStatement csDetalle = con.prepareCall(sqlDetalle)) {
                for (DetalleVenta det : detalles) {
                    det.setNoVenta(idVentaGenerado);
                    csDetalle.setInt(1, det.getNoVenta());
                    csDetalle.setString(2, det.getCodigoBarras());
                    csDetalle.setInt(3, det.getCantidad());
                    csDetalle.setDouble(4, det.getPrecioUnitario());
                    csDetalle.setDouble(5, det.getsubTotal());
                    csDetalle.executeUpdate();
                }
            }

            con.commit();
            return true;

        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error en la transacción de venta. Ejecutando Rollback... " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al ejecutar Rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }
}