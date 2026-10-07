package org.ibm.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.ibm.dao.ArtistaDAO;
import org.ibm.utils.Conexion;
import org.ibm.exception.DaoException;
import org.ibm.model.Artista;

public class ArtistaDAOImpl implements ArtistaDAO {

    @Override
    public List<Artista> listarTodos() {
        List<Artista> lista = new ArrayList<>();
        String sql = "{call sp_listarartistas()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql);
             ResultSet rs = consulta.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearArtista(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar artistas: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Artista buscarPorId(Integer idArtista) {
        Artista artista = null;
        String sql = "{call sp_buscarartista(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, idArtista != null ? idArtista : 0);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    artista = mapearArtista(rs);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar artista: " + e.getMessage(), e);
        }
        return artista;
    }

    @Override
    public boolean crear(Artista artista) {
        String sql = "{call sp_insertarartista(?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, artista.getNombreArtistico());
            consulta.setString(2, artista.getNacionalidad());
            consulta.setString(3, artista.getBiografia());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al insertar artista: " + e.getMessage(), e);
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
            throw new DaoException("Error al actualizar artista: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer idArtista) {
        String sql = "{call sp_eliminarartista(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setInt(1, idArtista != null ? idArtista : 0);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar artista: " + e.getMessage(), e);
        }
    }

    /**
     * Mapeo seguro dinámico para evitar errores de columnas no encontradas.
     */
    private Artista mapearArtista(ResultSet rs) throws SQLException {
        Artista artista = new Artista();
        Set<String> columnas = obtenerColumnasDisponibles(rs);

        // ID Artista
        if (columnas.contains("id_artista")) artista.setIdArtista(rs.getInt("id_artista"));
        else if (columnas.contains("id")) artista.setIdArtista(rs.getInt("id"));

        // Nombre Artístico
        if (columnas.contains("nombre_artistico")) artista.setNombreArtistico(rs.getString("nombre_artistico"));
        else if (columnas.contains("nombre")) artista.setNombreArtistico(rs.getString("nombre"));
        else artista.setNombreArtistico("Desconocido");

        // Nacionalidad
        if (columnas.contains("nacionalidad")) artista.setNacionalidad(rs.getString("nacionalidad"));
        else artista.setNacionalidad("");

        // Biografía
        if (columnas.contains("biografia")) artista.setBiografia(rs.getString("biografia"));
        else artista.setBiografia("");

        return artista;
    }

    private Set<String> obtenerColumnasDisponibles(ResultSet rs) throws SQLException {
        Set<String> set = new HashSet<>();
        ResultSetMetaData md = rs.getMetaData();
        int count = md.getColumnCount();
        for (int i = 1; i <= count; i++) {
            set.add(md.getColumnLabel(i).toLowerCase());
        }
        return set;
    }
}