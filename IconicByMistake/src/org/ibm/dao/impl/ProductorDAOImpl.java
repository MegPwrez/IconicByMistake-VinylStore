package org.ibm.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ibm.dao.ProductorDAO;
import org.ibm.exception.DaoException;
import org.ibm.model.Productor;
import org.ibm.utils.ConexionSingleton;

public class ProductorDAOImpl implements ProductorDAO {

    @Override
    public List<Productor> listarTodos() {
        ArrayList<Productor> lista = new ArrayList<>();
        String sql = "{call sp_listar_disqueras()}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql);
             ResultSet rs = consulta.executeQuery()) {
            
            while (rs.next()) {
                Productor p = new Productor();
                p.setIdProductor(rs.getString("nit_disquera"));
                p.setNombreProductor(rs.getString("nombre_disquera"));
                p.setSelloDiscografico(rs.getString("telefono_disquera"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar disqueras: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Productor buscarPorId(String idProductor) {
        Productor p = null;
        String sql = "{call sp_buscar_disquera_por_id(?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, idProductor);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    p = new Productor();
                    p.setIdProductor(rs.getString("nit_disquera"));
                    p.setNombreProductor(rs.getString("nombre_disquera"));
                    p.setSelloDiscografico(rs.getString("telefono_disquera"));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar disquera: " + e.getMessage(), e);
        }
        return p;
    }

    @Override
    public boolean crear(Productor productor) {
        // Recibe: nit, nombre, telefono, direccion (pasamos el sello como teléfono y un texto por defecto para dirección si es necesario)
        String sql = "{call sp_crear_disquera(?,?,?,?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, productor.getIdProductor());
            consulta.setString(2, productor.getNombreProductor());
            consulta.setString(3, productor.getSelloDiscografico()); // Usado como teléfono
            consulta.setString(4, "N/A"); // Dirección por defecto o puedes agregar un campo extra si lo deseas
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al insertar disquera: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Productor productor) {
        String sql = "{call sp_actualizar_disquera(?,?,?,?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, productor.getIdProductor());
            consulta.setString(2, productor.getNombreProductor());
            consulta.setString(3, productor.getSelloDiscografico());
            consulta.setString(4, "N/A");
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar disquera: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(String idProductor) {
        String sql = "{call sp_eliminar_disquera(?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, idProductor);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar disquera: " + e.getMessage(), e);
        }
    }
}