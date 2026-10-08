package org.ibm.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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

    @Override
    public Vinilo buscarPorId(String codigoBarras) {
        Vinilo vinilo = null;
        String sql = "{call sp_buscarlinilo(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, codigoBarras);
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
            consulta.setString(1, vinilo.getCodigoBarras());
            consulta.setString(2, vinilo.getTituloAlbum());
            consulta.setDate(3, vinilo.getFechaLanzamiento());
            consulta.setDouble(4, vinilo.getPrecio());
            consulta.setInt(5, vinilo.getStockActual());
            consulta.setInt(6, vinilo.getStockMinimo() > 0 ? vinilo.getStockMinimo() : 2); // stock_minimo
            consulta.setInt(7, vinilo.getGenero() != null ? vinilo.getGenero().getIdGenero() : 1);
            consulta.setString(8, vinilo.getProductor() != null ? vinilo.getProductor().getIdProductor() : "DISQ-01");
            consulta.setInt(9, 1); // id_proveedor por defecto
            
            // Se pasa la URL de la foto de forma correcta
            if (vinilo.getUrlFoto() != null && !vinilo.getUrlFoto().isEmpty()) {
                consulta.setString(10, vinilo.getUrlFoto());
            } else {
                consulta.setNull(10, Types.VARCHAR);
            }
            
            boolean ejecutado = consulta.executeUpdate() > 0;
            
            if (ejecutado && vinilo.getArtista() != null && vinilo.getArtista().getIdArtista() > 0) {
                asociarArtista(vinilo.getCodigoBarras(), vinilo.getArtista().getIdArtista(), conexion);
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
            consulta.setString(1, vinilo.getCodigoBarras());
            consulta.setString(2, vinilo.getTituloAlbum());
            consulta.setDate(3, vinilo.getFechaLanzamiento());
            consulta.setDouble(4, vinilo.getPrecio());
            consulta.setInt(5, vinilo.getStockActual());
            consulta.setInt(6, vinilo.getStockMinimo() > 0 ? vinilo.getStockMinimo() : 2); // stock_minimo
            consulta.setInt(7, vinilo.getGenero() != null ? vinilo.getGenero().getIdGenero() : 1);
            consulta.setString(8, vinilo.getProductor() != null ? vinilo.getProductor().getIdProductor() : "DISQ-01");
            consulta.setInt(9, 1); // id_proveedor
            
            // Se pasa la URL de la foto de forma correcta
            if (vinilo.getUrlFoto() != null && !vinilo.getUrlFoto().isEmpty()) {
                consulta.setString(10, vinilo.getUrlFoto());
            } else {
                consulta.setNull(10, Types.VARCHAR);
            }
            
            boolean actualizado = consulta.executeUpdate() > 0;
            
            if (actualizado && vinilo.getArtista() != null && vinilo.getArtista().getIdArtista() > 0) {
                actualizarAsociacionArtista(vinilo.getCodigoBarras(), vinilo.getArtista().getIdArtista(), conexion);
            }
            return actualizado;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar vinilo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(String codigoBarras) {
        String sql = "{call sp_eliminarvinilo(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            consulta.setString(1, codigoBarras);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar vinilo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Vinilo> buscar(String criterio) {
        List<Vinilo> lista = new ArrayList<>();
        String criterioLower = criterio.toLowerCase();
        for (Vinilo v : listarTodos()) {
            boolean coincideTitulo = v.getTituloAlbum() != null && v.getTituloAlbum().toLowerCase().contains(criterioLower);
            boolean coincideCodigo = v.getCodigoBarras() != null && v.getCodigoBarras().toLowerCase().contains(criterioLower);
            if (coincideTitulo || coincideCodigo) {
                lista.add(v);
            }
        }
        return lista;
    }

    private void asociarArtista(String codigoBarras, int idArtista, Connection conexion) {
        try (CallableStatement cs = conexion.prepareCall("{call sp_insertarartistasvinilo(?, ?)}")) {
            cs.setInt(1, idArtista);
            cs.setString(2, codigoBarras);
            cs.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private void actualizarAsociacionArtista(String codigoBarras, int idArtista, Connection conexion) {
        try (java.sql.Statement st = conexion.createStatement()) {
            st.executeUpdate("DELETE FROM artistas_vinilo WHERE codigo_barras = '" + codigoBarras + "'");
        } catch (SQLException ignored) {}
        asociarArtista(codigoBarras, idArtista, conexion);
    }

    private Vinilo mapearVinilo(ResultSet rs) throws SQLException {
        Vinilo vinilo = new Vinilo();
        vinilo.setCodigoBarras(rs.getString("codigo_barras"));
        vinilo.setTituloAlbum(rs.getString("titulo_album"));
        vinilo.setFechaLanzamiento(rs.getDate("fecha_lanzamiento"));
        vinilo.setPrecio(rs.getDouble("precio"));
        vinilo.setStockActual(rs.getInt("stock_actual"));
        
        // Mapeo del campo url_foto
        try {
            vinilo.setUrlFoto(rs.getString("url_foto"));
        } catch (SQLException ignored) {}

        try {
            int idGen = rs.getInt("id_genero");
            if (!rs.wasNull()) {
                Genero genero = new Genero();
                genero.setIdGenero(idGen);
                genero.setNombre(rs.getString("nombre_genero"));
                vinilo.setGenero(genero);
            }
        } catch (SQLException ignored) {}

        try {
            String nitDisq = rs.getString("nit_disquera");
            if (nitDisq != null) {
                Productor productor = new Productor();
                productor.setIdProductor(nitDisq);
                productor.setNombreProductor(rs.getString("nombre_productor"));
                vinilo.setProductor(productor);
            }
        } catch (SQLException ignored) {}

        try {
            int idArt = rs.getInt("id_artista");
            if (!rs.wasNull() && idArt > 0) {
                Artista artista = new Artista();
                artista.setIdArtista(idArt);
                artista.setNombreArtistico(rs.getString("nombre_artista"));
                // Corregido: uso de setters para cumplir con el encapsulamiento
                artista.setNacionalidad(rs.getString("nacionalidad"));
                artista.setBiografia(rs.getString("biografia"));
                vinilo.setArtista(artista);
            }
        } catch (SQLException ignored) {}

        return vinilo;
    }
}