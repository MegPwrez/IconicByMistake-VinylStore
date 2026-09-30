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
        String sql = "{call sp_listar_productores()}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql);
             ResultSet rs = consulta.executeQuery()) {
            
            while (rs.next()) {
                Productor p = new Productor();
                p.setIdProductor(rs.getString("id_productor"));
                p.setNombreProductor(rs.getString("nombre_productor"));
                p.setSelloDiscografico(rs.getString("sello_discografico"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar productores: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Productor buscarPorId(String idProductor) {
        Productor p = null;
        String sql = "{call sp_buscar_productor_por_id(?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, idProductor);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    p = new Productor();
                    p.setIdProductor(rs.getString("id_productor"));
                    p.setNombreProductor(rs.getString("nombre_productor"));
                    p.setSelloDiscografico(rs.getString("sello_discografico"));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar productor: " + e.getMessage(), e);
        }
        return p;
    }

    @Override
    public boolean crear(Productor productor) {
        String sql = "{call sp_crear_productor(?,?,?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, productor.getIdProductor());
            consulta.setString(2, productor.getNombreProductor());
            consulta.setString(3, productor.getSelloDiscografico());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al insertar productor: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Productor productor) {
        String sql = "{call sp_actualizar_productor(?,?,?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, productor.getIdProductor());
            consulta.setString(2, productor.getNombreProductor());
            consulta.setString(3, productor.getSelloDiscografico());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar productor: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(String idProductor) {
        String sql = "{call sp_eliminar_productor(?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, idProductor);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar productor: " + e.getMessage(), e);
        }
    }
}