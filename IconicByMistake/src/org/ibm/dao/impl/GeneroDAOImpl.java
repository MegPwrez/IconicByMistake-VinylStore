package org.ibm.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ibm.dao.GeneroDAO;
import org.ibm.exception.DaoException;
import org.ibm.model.Genero;
import org.ibm.utils.ConexionSingleton;

public class GeneroDAOImpl implements GeneroDAO {

    public List<Genero> listarTodos() {
        List<Genero> lista = new ArrayList<>();
        String sql = "{call sp_listar_generos()}"; // Ajusta el nombre de tu SP si es distinto
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql);
             ResultSet rs = consulta.executeQuery()) {
            
            while (rs.next()) {
                Genero g = new Genero();
                g.setIdGenero(rs.getInt("id_genero"));
                g.setNombre(rs.getString("nombre_genero")); // Ajusta según el campo de tu BD
                lista.add(g);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar géneros: " + e.getMessage(), e);
        }
        return lista;
    }

    public Genero buscarPorId(Integer idGenero) {
        Genero g = null;
        String sql = "{call sp_buscar_genero_por_id(?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setInt(1, idGenero);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    g = new Genero();
                    g.setIdGenero(rs.getInt("id_genero"));
                    g.setNombre(rs.getString("nombre_genero"));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar género: " + e.getMessage(), e);
        }
        return g;
    }

    public boolean crear(Genero genero) {
        String sql = "{call sp_crear_genero(?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, genero.getNombre());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al insertar género: " + e.getMessage(), e);
        }
    }

    public boolean actualizar(Genero genero) {
        String sql = "{call sp_actualizar_genero(?,?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setInt(1, genero.getIdGenero());
            consulta.setString(2, genero.getNombre());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar género: " + e.getMessage(), e);
        }
    }

    
    public boolean eliminar(Integer idGenero) {
        String sql = "{call sp_eliminar_genero(?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setInt(1, idGenero);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar género: " + e.getMessage(), e);
        }
    }
}