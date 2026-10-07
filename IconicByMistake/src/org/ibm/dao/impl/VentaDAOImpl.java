package org.ibm.dao.impl;

import org.ibm.dao.VentaDAO;
import org.ibm.model.Venta;
import org.ibm.utils.Conexion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class VentaDAOImpl implements VentaDAO {

    @Override
    public boolean crear(Venta objeto) {
        String sql = "{call sp_insertarventa(?, ?, ?, ?, ?, ?)}"; 
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            double subtotalNum = Double.parseDouble(objeto.getSubTotal().replace(",", "."));

            cs.setDouble(1, subtotalNum);
            cs.setDouble(2, objeto.getDescuento());
            cs.setDouble(3, objeto.getTotalVenta());
            cs.setLong(4, objeto.getCuiCliente());
            cs.setInt(5, objeto.getId_usuario());
            cs.registerOutParameter(6, Types.INTEGER);
            
            int filasAfectadas = cs.executeUpdate();
            if (filasAfectadas > 0) {
                objeto.setIdVenta(cs.getInt(6));
                return true;
            }
            return false;
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error [Crear Venta]: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Venta> listarTodos() {
        List<Venta> lista = new ArrayList<>();
        String sql = "{call sp_listarventas()}";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            
            while (rs.next()) {
                Venta venta = new Venta();
                venta.setIdVenta(rs.getInt("id_venta"));
                venta.setFechaVenta(rs.getTimestamp("fecha_venta"));
                venta.setSubTotal(String.valueOf(rs.getDouble("subtotal")));
                venta.setDescuento(rs.getDouble("descuento"));
                venta.setTotalVenta(rs.getDouble("total"));
                venta.setEstado(rs.getString("estado"));
                venta.setCuiCliente(rs.getLong("cui_cliente"));
                venta.setId_usuario(rs.getInt("id_usuario"));
                lista.add(venta);
            }
        } catch (SQLException e) {
            System.err.println("Error [Listar Ventas]: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Venta buscarPorId(Integer id) {
        Venta venta = null;
        String sql = "{call sp_buscarventa(?)}";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    venta = new Venta();
                    venta.setIdVenta(rs.getInt("id_venta"));
                    venta.setFechaVenta(rs.getTimestamp("fecha_venta"));
                    venta.setSubTotal(String.valueOf(rs.getDouble("subtotal")));
                    venta.setDescuento(rs.getDouble("descuento"));
                    venta.setTotalVenta(rs.getDouble("total"));
                    venta.setEstado(rs.getString("estado"));
                    venta.setCuiCliente(rs.getLong("cui_cliente"));
                    venta.setId_usuario(rs.getInt("id_usuario"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error [Buscar Venta por ID]: " + e.getMessage());
        }
        return venta;
    }

    @Override
    public boolean actualizar(Venta objeto) {
        String sql = "UPDATE ventas SET estado = ? WHERE id_venta = ?";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setString(1, objeto.getEstado());
            cs.setInt(2, objeto.getIdVenta());
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error [Actualizar Venta]: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "UPDATE ventas SET estado = 'ANULADA', fecha_anulacion = NOW() WHERE id_venta = ?";
        try (Connection con = Conexion.getInstancia().conectar();
             CallableStatement cs = con.prepareCall(sql)) {
            
            cs.setInt(1, id);
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error [Anular Venta]: " + e.getMessage());
            return false;
        }
    }
}