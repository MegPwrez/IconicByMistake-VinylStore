package org.ibm.dao.impl;

import org.ibm.dao.DetalleVentaDAO;
import org.ibm.model.DetalleVenta;
import org.ibm.utils.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaImpl implements DetalleVentaDAO {

    @Override
    public boolean insertar(DetalleVenta detalleVenta) {
        String sql = "{call sp_insertardetalleventa(?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, detalleVenta.getNoVenta());
            cs.setString(2, detalleVenta.getCodigoBarras());
            cs.setInt(3, detalleVenta.getCantidad());
            cs.setDouble(4, detalleVenta.getPrecioUnitario());
            cs.setDouble(5, detalleVenta.getsubTotal());
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error [Insertar Detalle Venta]: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<DetalleVenta> listar() {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT dv.id_detalle, dv.id_venta, dv.codigo_barras, v.titulo_album, dv.cantidad, dv.precio_unitario, dv.subtotal "
                   + "FROM detalle_venta dv "
                   + "LEFT JOIN vinilos v ON dv.codigo_barras = v.codigo_barras";

        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                DetalleVenta dv = new DetalleVenta();
                dv.setIdDetalleventa(rs.getInt("id_detalle"));
                dv.setNoVenta(rs.getInt("id_venta"));
                dv.setCodigoBarras(rs.getString("codigo_barras"));
                dv.setTituloAlbum(rs.getString("titulo_album"));
                dv.setCantidad(rs.getInt("cantidad"));
                dv.setPrecioUnitario(rs.getDouble("precio_unitario"));
                dv.setSubTotal(rs.getDouble("subtotal"));
                lista.add(dv);
            }
        } catch (SQLException e) {
            System.err.println("Error [Listar Detalles Ventas]: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public DetalleVenta buscar(int idDetalleventa) {
        String sql = "SELECT dv.id_detalle, dv.id_venta, dv.codigo_barras, v.titulo_album, dv.cantidad, dv.precio_unitario, dv.subtotal "
                   + "FROM detalle_venta dv "
                   + "LEFT JOIN vinilos v ON dv.codigo_barras = v.codigo_barras "
                   + "WHERE dv.id_detalle = ?";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, idDetalleventa);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new DetalleVenta(
                            rs.getInt("id_detalle"),
                            rs.getInt("id_venta"),
                            rs.getString("codigo_barras"),
                            rs.getString("titulo_album"),
                            rs.getInt("cantidad"),
                            rs.getDouble("precio_unitario")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error [Buscar Detalle Venta]: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean actualizar(DetalleVenta objeto) {
        String sql = "UPDATE detalle_venta SET id_venta = ?, codigo_barras = ?, cantidad = ?, precio_unitario = ?, subtotal = ? WHERE id_detalle = ?";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, objeto.getNoVenta());
            cs.setString(2, objeto.getCodigoBarras());
            cs.setInt(3, objeto.getCantidad());
            cs.setDouble(4, objeto.getPrecioUnitario());
            cs.setDouble(5, objeto.getsubTotal());
            cs.setInt(6, objeto.getIdDetalleventa());
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error [Actualizar Detalle Venta]: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int idDetalleventa) {
        String sql = "DELETE FROM detalle_venta WHERE id_detalle = ?";
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