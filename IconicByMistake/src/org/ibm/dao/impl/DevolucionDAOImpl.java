package org.ibm.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.ibm.utils.Conexion;

/** Devolucion total atomica: una venta solo puede devolverse una vez. */
public class DevolucionDAOImpl {
    public void devolverVenta(int idVenta, int idAdministrador, String motivo) throws SQLException {
        if (idVenta <= 0 || idAdministrador <= 0 || motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique venta, administrador y motivo.");
        }
        try (Connection con = Conexion.getInstancia().conectar()) {
            con.setAutoCommit(false);
            try {
                String estado;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT estado FROM ventas WHERE id_venta = ? FOR UPDATE")) {
                    ps.setInt(1, idVenta);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("La venta no existe.");
                        estado = rs.getString(1);
                    }
                }
                if (!"COMPLETADA".equalsIgnoreCase(estado)) {
                    throw new SQLException("Solo pueden devolverse ventas COMPLETADAS. Estado: " + estado);
                }
                int lineas = 0;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT codigo_barras, cantidad FROM detalle_venta WHERE id_venta = ?")) {
                    ps.setInt(1, idVenta);
                    try (ResultSet rs = ps.executeQuery()) {
                        try (PreparedStatement stock = con.prepareStatement(
                                "UPDATE vinilos SET stock_actual = stock_actual + ? WHERE codigo_barras = ?")) {
                            while (rs.next()) {
                                stock.setInt(1, rs.getInt("cantidad"));
                                stock.setString(2, rs.getString("codigo_barras"));
                                if (stock.executeUpdate() != 1) throw new SQLException("Vinilo inexistente en inventario.");
                                lineas++;
                            }
                        }
                    }
                }
                if (lineas == 0) throw new SQLException("La venta no tiene detalle; no se modifico.");
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE ventas SET estado = 'DEVUELTA', fecha_devolucion = NOW(), " +
                        "usuario_devolucion = ?, motivo_devolucion = ? " +
                        "WHERE id_venta = ? AND estado = 'COMPLETADA'")) {
                    ps.setInt(1, idAdministrador);
                    ps.setString(2, motivo.trim());
                    ps.setInt(3, idVenta);
                    if (ps.executeUpdate() != 1) throw new SQLException("La venta ya cambio de estado.");
                }
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
