package org.ibm.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.ibm.dao.ViniloDAO;
import org.ibm.model.Artista;
import org.ibm.model.Genero;
import org.ibm.model.Productor;
import org.ibm.model.Vinilo;
import org.ibm.utils.ConexionSingleton;

public class ViniloDAOImpl implements ViniloDAO {

    @Override
    public List<Vinilo> listar() {
        List<Vinilo> listaVinilos = new ArrayList<>();
        String sql = "SELECT v.id_vinilo, v.sku, v.titulo, v.anio_lanzamiento, v.precio, v.stock, " +
                     "a.id_artista, a.nombre_artistico, a.pais_origen, " +
                     "g.id_genero, g.nombre AS genero_nombre, " +
                     "p.id_productor, p.nombre_productor, p.sello_discografico " +
                     "FROM vinilo v " +
                     "INNER JOIN artista a ON v.id_artista = a.id_artista " +
                     "INNER JOIN genero g ON v.id_genero = g.id_genero " +
                     "INNER JOIN productor p ON v.id_productor = p.id_productor";

        try (Connection conn = ConexionSingleton.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                listaVinilos.add(mapearVinilo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaVinilos;
    }

    @Override
    public List<Vinilo> buscar(String criterio) {
        List<Vinilo> listaVinilos = new ArrayList<>();
        String sql = "SELECT v.id_vinilo, v.sku, v.titulo, v.anio_lanzamiento, v.precio, v.stock, " +
                     "a.id_artista, a.nombre_artistico, a.pais_origen, " +
                     "g.id_genero, g.nombre AS genero_nombre, " +
                     "p.id_productor, p.nombre_productor, p.sello_discografico " +
                     "FROM vinilo v " +
                     "INNER JOIN artista a ON v.id_artista = a.id_artista " +
                     "INNER JOIN genero g ON v.id_genero = g.id_genero " +
                     "INNER JOIN productor p ON v.id_productor = p.id_productor " +
                     "WHERE v.sku LIKE ? OR v.titulo LIKE ? OR a.nombre_artistico LIKE ?";

        try (Connection conn = ConexionSingleton.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String parametroBusqueda = "%" + criterio + "%";
            pstmt.setString(1, parametroBusqueda);
            pstmt.setString(2, parametroBusqueda);
            pstmt.setString(3, parametroBusqueda);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listaVinilos.add(mapearVinilo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaVinilos;
    }

    @Override
    public boolean insertar(Vinilo vinilo) {
        String sql = "INSERT INTO vinilo (sku, titulo, anio_lanzamiento, precio, stock, id_artista, id_genero, id_productor) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionSingleton.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vinilo.getSku());
            pstmt.setString(2, vinilo.getTitulo());
            pstmt.setInt(3, vinilo.getAnioLanzamiento());
            pstmt.setDouble(4, vinilo.getPrecio());
            pstmt.setInt(5, vinilo.getStock());
            pstmt.setInt(6, vinilo.getArtista().getIdArtista());
            pstmt.setInt(7, vinilo.getGenero().getIdGenero());
            pstmt.setInt(8, vinilo.getProductor().getIdProductor());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Vinilo vinilo) {
        String sql = "UPDATE vinilo SET sku = ?, titulo = ?, anio_lanzamiento = ?, precio = ?, stock = ?, " +
                     "id_artista = ?, id_genero = ?, id_productor = ? WHERE id_vinilo = ?";

        try (Connection conn = ConexionSingleton.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vinilo.getSku());
            pstmt.setString(2, vinilo.getTitulo());
            pstmt.setInt(3, vinilo.getAnioLanzamiento());
            pstmt.setDouble(4, vinilo.getPrecio());
            pstmt.setInt(5, vinilo.getStock());
            pstmt.setInt(6, vinilo.getArtista().getIdArtista());
            pstmt.setInt(7, vinilo.getGenero().getIdGenero());
            pstmt.setInt(8, vinilo.getProductor().getIdProductor());
            pstmt.setInt(9, vinilo.getIdVinilo());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Vinilo mapearVinilo(ResultSet rs) throws SQLException {
        Vinilo vinilo = new Vinilo();
        vinilo.setIdVinilo(rs.getInt("id_vinilo"));
        vinilo.setSku(rs.getString("sku"));
        vinilo.setTitulo(rs.getString("titulo"));
        vinilo.setAnioLanzamiento(rs.getInt("anio_lanzamiento"));
        vinilo.setPrecio(rs.getDouble("precio"));
        vinilo.setStock(rs.getInt("stock"));

        Artista artista = new Artista(
            rs.getInt("id_artista"),
            rs.getString("nombre_artistico"),
            rs.getString("pais_origen")
        );

        Genero genero = new Genero(
            rs.getInt("id_genero"),
            rs.getString("genero_nombre")
        );

        Productor productor = new Productor(
            rs.getInt("id_productor"),
            rs.getString("nombre_productor"),
            rs.getString("sello_discografico")
        );

        vinilo.setArtista(artista);
        vinilo.setGenero(genero);
        vinilo.setProductor(productor);

        return vinilo;
    }
}