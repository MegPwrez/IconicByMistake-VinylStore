
package org.ibm.dao.impl;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.ibm.dao.ClienteDAO;
import org.ibm.utils.Conexion;
import org.ibm.model.Cliente;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public List<Cliente> listar() throws Exception {
        List<Cliente> lista = new ArrayList<>();
        String sql = "{call sp_listarclientes()}";
        
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql);
             ResultSet rs = consulta.executeQuery()) {
            
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setCui(rs.getLong("cui"));
                c.setNombreCliente(rs.getString("nombre_cliente"));
                c.setApellidoCliente(rs.getString("apellido_cliente"));
                c.setCorreoElectronico(rs.getString("correo_electronico"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error sp_listarclientes: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(Long cui) throws Exception {
        Cliente c = null;
        String sql = "{call sp_buscarcliente(?)}";
        
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setLong(1, cui);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    c = new Cliente();
                    c.setCui(rs.getLong("cui"));
                    c.setNombreCliente(rs.getString("nombre_cliente"));
                    c.setApellidoCliente(rs.getString("apellido_cliente"));
                    c.setCorreoElectronico(rs.getString("correo_electronico"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error sp_buscarcliente: " + e.getMessage());
            throw e;
        }
        return c;
    }

    @Override
    public boolean crear(Cliente cliente) throws Exception {
        String sql = "{call sp_insertarcliente(?,?,?,?)}";
        
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setLong(1, cliente.getCui());
            consulta.setString(2, cliente.getNombreCliente());
            consulta.setString(3, cliente.getApellidoCliente());
            consulta.setString(4, cliente.getCorreoElectronico());
            
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error sp_insertarcliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean actualizar(Cliente cliente) throws Exception {
        String sql = "{call sp_actualizarcliente(?,?,?,?)}";
        
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setLong(1, cliente.getCui());
            consulta.setString(2, cliente.getNombreCliente());
            consulta.setString(3, cliente.getApellidoCliente());
            consulta.setString(4, cliente.getCorreoElectronico());
            
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error sp_actualizarcliente: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean eliminar(Long cui) throws Exception {
        String sql = "{call sp_eliminarcliente(?)}";
        
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setLong(1, cui);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error sp_eliminarcliente: " + e.getMessage());
            throw e;
        }
    }
}