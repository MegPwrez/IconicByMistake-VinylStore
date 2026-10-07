package org.ibm.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ibm.dao.ViniloDAO;
import org.ibm.utils.Conexion;
import org.ibm.exception.DaoException;
import org.ibm.model.Artista;
import org.ibm.model.Genero;
import org.ibm.model.Productor;
import org.ibm.model.Vinilo;

public class ViniloDAOImpl implements ViniloDAO {

    @Override
    public List<Vinilo> listarTodos() {
        List<Vinilo> lista = new ArrayList<>();
        String sql = "{call sp_listarvinilos()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql);
             ResultSet rs = consulta.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearVinilo(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar vinilos: " + e.getMessage(), e);
        }
        return lista;
    }

    public Vinilo buscarPorId(String sku) {
        Vinilo vinilo = null;
        String sql = "{call sp_buscarlinilo(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, sku);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    vinilo = mapearVinilo(rs);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar vinilo: " + e.getMessage(), e);
        }
        return vinilo;
    }

    @Override
    public boolean crear(Vinilo vinilo) {
        String sql = "{call sp_insertarvinilo(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, vinilo.getSku());
            consulta.setString(2, vinilo.getTitulo());
            consulta.setString(3, vinilo.getAnioLanzamiento());
            consulta.setDouble(4, vinilo.getPrecio());
            consulta.setInt(5, vinilo.getStock());
            consulta.setInt(6, 2); // stock_minimo por defecto
            consulta.setInt(7, vinilo.getGenero() != null ? vinilo.getGenero().getIdGenero() : 1);
            consulta.setString(8, vinilo.getProductor() != null ? vinilo.getProductor().getIdProductor() : "DISQ-01");
            consulta.setInt(9, 1); // id_proveedor por defecto
            consulta.setString(10, vinilo.getUrlFoto());
            
            boolean ejecutado = consulta.executeUpdate() > 0;
            
            if (ejecutado && vinilo.getArtista() != null && vinilo.getArtista().getIdArtista() > 0) {
                asociarArtista(vinilo.getSku(), vinilo.getArtista().getIdArtista(), conexion);
            }
            return ejecutado;
        } catch (SQLException e) {
            throw new DaoException("Error al insertar vinilo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Vinilo vinilo) {
        String sql = "{call sp_actualizarvinilo(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, vinilo.getSku());
            consulta.setString(2, vinilo.getTitulo());
            consulta.setString(3, vinilo.getAnioLanzamiento());
            consulta.setDouble(4, vinilo.getPrecio());
            consulta.setInt(5, vinilo.getStock());
            consulta.setInt(6, 2); // stock_minimo
            consulta.setInt(7, vinilo.getGenero() != null ? vinilo.getGenero().getIdGenero() : 1);
            consulta.setString(8, vinilo.getProductor() != null ? vinilo.getProductor().getIdProductor() : "DISQ-01");
            consulta.setInt(9, 1); // id_proveedor
            consulta.setString(10, vinilo.getUrlFoto());
            
            boolean actualizado = consulta.executeUpdate() > 0;
            
            if (actualizado && vinilo.getArtista() != null && vinilo.getArtista().getIdArtista() > 0) {
                actualizarAsociacionArtista(vinilo.getSku(), vinilo.getArtista().getIdArtista(), conexion);
            }
            return actualizado;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar vinilo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(String sku) {
        String sql = "{call sp_eliminarvinilo(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, sku);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar vinilo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Vinilo> buscar(String criterio) {
        List<Vinilo> lista = new ArrayList<>();
        for (Vinilo v : listarTodos()) {
            if (v.getTitulo().toLowerCase().contains(criterio.toLowerCase()) || 
                v.getSku().toLowerCase().contains(criterio.toLowerCase())) {
                lista.add(v);
            }
        }
        return lista;
    }

    private void asociarArtista(String sku, int idArtista, Connection conexion) {
        try (CallableStatement cs = conexion.prepareCall("{call sp_insertarartistasvinilo(?, ?)}")) {
            cs.setInt(1, idArtista);
            cs.setString(2, sku);
            cs.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private void actualizarAsociacionArtista(String sku, int idArtista, Connection conexion) {
        try (java.sql.Statement st = conexion.createStatement()) {
            st.executeUpdate("DELETE FROM artistas_vinilo WHERE codigo_barras = '" + sku + "'");
        } catch (SQLException ignored) {}
        asociarArtista(sku, idArtista, conexion);
    }

    private Vinilo mapearVinilo(ResultSet rs) throws SQLException {
        Vinilo vinilo = new Vinilo();
        vinilo.setSku(rs.getString("codigo_barras"));
        vinilo.setTitulo(rs.getString("titulo_album"));
        vinilo.setAnioLanzamiento(rs.getString("fecha_lanzamiento"));
        vinilo.setPrecio(rs.getDouble("precio"));
        vinilo.setStock(rs.getInt("stock_actual"));
        vinilo.setUrlFoto(rs.getString("url_foto"));

        // Mapeo correcto del Género
        try {
            int idGen = rs.getInt("id_genero");
            if (!rs.wasNull()) {
                Genero genero = new Genero();
                genero.setIdGenero(idGen);
                vinilo.setGenero(genero);
            }
        } catch (SQLException ignored) {}

        // Mapeo correcto del Productor / Disquera
        try {
            String nitDisq = rs.getString("nit_disquera");
            if (nitDisq != null) {
                Productor productor = new Productor();
                productor.setIdProductor(nitDisq);
                vinilo.setProductor(productor);
            }
        } catch (SQLException ignored) {}

        // Mapeo del Artista
        try {
            int idArt = rs.getInt("id_artista");
            if (!rs.wasNull() && idArt > 0) {
                Artista artista = new Artista();
                artista.setIdArtista(idArt);
                artista.setNombreArtistico(rs.getString("nombre_artista"));
                artista.setNacionalidad(rs.getString("nacionalidad"));
                artista.setBiografia(rs.getString("biografia"));
                vinilo.setArtista(artista);
            }
        } catch (SQLException ignored) {}

        return vinilo;
    }
}