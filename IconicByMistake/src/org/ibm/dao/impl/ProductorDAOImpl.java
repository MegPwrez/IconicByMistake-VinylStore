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
        // CORREGIDO: Se cambia 'sp_listardisqueras' por el procedimiento correcto 'sp_listar_productores'
        String sql = "{call sp_listar_productores()}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql);
             ResultSet rs = consulta.executeQuery()) {
            
            while (rs.next()) {
                Productor p = new Productor();
                // CORREGIDO: Usamos los alias correctos definidos en sp_listar_productores
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
        // NOTA: Asegúrate de que este SP devuelva también los alias correspondientes
        String sql = "{call sp_buscar_disquera_por_id(?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, idProductor);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    p = new Productor();
                    p.setIdProductor(rs.getString("nit_disquera"));
                    p.setNombreProductor(rs.getString("nombre_disquera"));
                    // CORREGIDO: Extraemos la dirección real que representa al sello discográfico
                    p.setSelloDiscografico(rs.getString("direccion_disquera"));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar disquera: " + e.getMessage(), e);
        }
        return p;
    }

    @Override
    public boolean crear(Productor productor) {
        // Estructura original en SQL: nit_disquera, nombre_disquera, direccion_disquera, telefono_disquera
        String sql = "{call sp_crear_disquera(?,?,?,?)}";
        
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consulta = conexion.prepareCall(sql)) {
            
            consulta.setString(1, productor.getIdProductor());
            consulta.setString(2, productor.getNombreProductor());
            // CORREGIDO: Guardamos el 'Sello Discográfico' en la columna de la dirección física
            consulta.setString(3, productor.getSelloDiscografico()); 
            // CORREGIDO: Mandamos un teléfono genérico o vacío en vez de "N/A" para la dirección
            consulta.setString(4, ""); 
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
            // CORREGIDO: Actualizamos el 'Sello Discográfico' en su columna respectiva
            consulta.setString(3, productor.getSelloDiscografico());
            consulta.setString(4, "");
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
