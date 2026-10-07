package org.ibm.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ibm.dao.ArtistaDAO;
import org.ibm.exception.DaoException;
import org.ibm.model.Artista;
import org.ibm.utils.Conexion;

public class ArtistaDAOImpl implements ArtistaDAO {
    private static final Logger LOGGER = Logger.getLogger(ArtistaDAOImpl.class.getName());

    @Override
    public List<Artista> listarTodos() {
        List<Artista> lista = new ArrayList<>();
        String sql = "{call sp_listarartistas()}";
        
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql);
             ResultSet rs = consulta.executeQuery()) {
            
            while (rs.next()) {
                Artista artista = new Artista(
                    rs.getInt("id_artista"),
                    rs.getString("nombre_artista"),
                    rs.getString("nacionalidad"),
                    rs.getString("biografia")
                );
                lista.add(artista);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar artistas", e);
            throw new DaoException("Error al listar artistas: " + e.getMessage(), e);
        }
        return lista;
    }

    public Artista buscarPorId(Integer idArtista) {
        if (idArtista == null) return null;
        Artista artista = null;
        String sql = "{call sp_buscarartista(?)}";

        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {

            consulta.setInt(1, idArtista);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    artista = new Artista(
                        rs.getInt("id_artista"),
                        rs.getString("nombre_artista"),
                        rs.getString("nacionalidad"),
                        rs.getString("biografia")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar artista por ID", e);
            throw new DaoException("Error al buscar artista: " + e.getMessage(), e);
        }

        return artista;
    }

    @Override
    public boolean crear(Artista artista) {
        // Corregido el espacio en blanco en "sp_insertarartista"
        String sql = "{call sp_insertarartista(?, ?, ?)}"; 
        
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, artista.getNombreArtistico());
            consulta.setString(2, artista.getNacionalidad());
            consulta.setString(3, artista.getBiografia());
            
            return consulta.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear artista", e);
            return false;
        }
    }

    @Override
    public boolean actualizar(Artista artista) {
        String sql = "{call sp_actualizarartista(?, ?, ?, ?)}";
        
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setInt(1, artista.getIdArtista());
            consulta.setString(2, artista.getNombreArtistico());
            consulta.setString(3, artista.getNacionalidad());
            consulta.setString(4, artista.getBiografia());
            
            return consulta.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar artista", e);
            return false;
        }
    }

    public boolean eliminar(Integer idArtista) {
        String sql = "{call sp_eliminarartista(?)}";
        
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setInt(1, idArtista);
            return consulta.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar artista", e);
            return false;
        }
    }
}