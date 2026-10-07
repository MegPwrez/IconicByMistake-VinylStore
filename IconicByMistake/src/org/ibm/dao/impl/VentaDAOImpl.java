
package org.ibm.dao.impl;

import org.ibm.dao.DetalleVentaDAO;
import org.ibm.model.DetalleVenta;
import org.ibm.utils.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaImpl implements DetalleVentaDAO {

    public boolean insertar(DetalleVenta detalleVenta) {
        String sql = "{call sp_insertardetalleventa(?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, detalleVenta.getNoVenta());
            cs.setString(2, detalleVenta.getCodigoBarras());
            cs.setInt(3, detalleVenta.getCantidad());
            cs.setDouble(4, detalleVenta.getPrecioUnitario());
            cs.setDouble(5, detalleVenta.getSubTotalDetalle());
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error [Insertar Detalle Venta]: " + e.getMessage());
            return false;
        }
    }

    public List<DetalleVenta> listar() {
List<DetalleVenta> lista = new ArrayList<>();
    String sql = "{call sp_listardetalleventa(?)}";

 try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
        cs.setObject(1, null);
        try (ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                DetalleVenta dv = new DetalleVenta();
                dv.setIdDetalleventa(rs.getInt("id_detalle"));
                dv.setNoVenta(rs.getInt("id_venta"));
                dv.setIsbn(rs.getString("codigo_barras"));
                dv.setCantidad(rs.getInt("cantidad"));
                dv.setPrecioUnitario(rs.getDouble("precio_unitario"));
                dv.setSubTotalDetalle(rs.getDouble("subtotal"));
                lista.add(dv);
            }
        }
    } catch (SQLException e) {
    }
    return lista;
}
    public DetalleVenta buscar(int idDetalleventa) {
        String sql = "{call sp_buscardetalleventa(?)}";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idDetalleventa);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new DetalleVenta(
                            rs.getString("codigo_barras"),
                        rs.getString("id_venta"),
                            rs.getDouble("precio_unitario"),
                        rs.getInt("cantidad"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error [Buscar Detalle Venta]: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(DetalleVenta objeto) {
        String sql = "{call sp_actualizardetalleventa(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, objeto.getIdDetalleventa());
            cs.setInt(2, objeto.getNoVenta());
            cs.setString(3, objeto.getIsbn());
            cs.setInt(4, objeto.getCantidad());
            cs.setDouble(5, objeto.getPrecioUnitario());
            cs.setDouble(6, objeto.getSubTotalDetalle());
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error [Actualizar Detalle Venta]: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idDetalleventa) {
        String sql = "{call sp_eliminardetalleventa(?)}";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idDetalleventa);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error [Eliminar Detalle Venta]: " + e.getMessage());
            return false;
        }
    } 


}