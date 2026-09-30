package org.ibm.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TextField;
import org.ibm.dao.UsuarioDAO;
import org.ibm.model.Usuario;
import org.ibm.utils.ConexionSingleton;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario autenticar(String nombreUsuario, String contraseña) {
        Usuario usuario = null;
        String consulta = "{call sp_autenticarusuario(?, ?)}";
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consultaCall = conexion.prepareCall(consulta)) {
            consultaCall.setString(1, nombreUsuario);
            consultaCall.setString(2, contraseña);
            try (ResultSet tablaResultado = consultaCall.executeQuery()) {
                if (tablaResultado.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(tablaResultado.getInt("id_usuario"));
                    usuario.setNombreUsuario(tablaResultado.getString("nombre_usuario"));
                    usuario.setCorreo(tablaResultado.getString("correo"));
                    usuario.setRol(tablaResultado.getString("rol"));
                    usuario.setEstado(tablaResultado.getBoolean("activo"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar Usuario: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String consulta = "{call sp_listarusuarios()}";

        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consultaCall = conexion.prepareCall(consulta);
             ResultSet rs = consultaCall.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setRol(rs.getString("rol"));
                usuario.setEstado(rs.getBoolean("activo"));

                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    @Override
    public Usuario buscarUsuario(int idUsuario) {
        Usuario usuario = new Usuario();
        String consultaSQL = "{call sp_buscarusuario(?)}";
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consultaCall = conexion.prepareCall(consultaSQL)) {
            consultaCall.setInt(1, idUsuario);
            try (ResultSet tablaResultado = consultaCall.executeQuery()) {
                if (tablaResultado.next()) {
                    usuario.setIdUsuario(tablaResultado.getInt("id_usuario"));
                    usuario.setNombreUsuario(tablaResultado.getString("nombre_usuario"));
                    usuario.setCorreo(tablaResultado.getString("correo"));
                    usuario.setRol(tablaResultado.getString("rol"));
                    usuario.setEstado(tablaResultado.getBoolean("activo"));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Usuario: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public boolean insertar(Usuario usuario) {
        String sql = "{call sp_insertarusuario(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = ConexionSingleton.getConexion();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, usuario.getNombreUsuario());
            cs.setString(2, usuario.getNombre());
            cs.setString(3, usuario.getApellido());
            cs.setString(4, usuario.getCorreo());
            cs.setString(5, usuario.getContraseña());
            cs.setString(6, usuario.getRol().toLowerCase());
            cs.setBoolean(7, usuario.isEstado()); 

            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear Usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        String consulta = "{call sp_actualizarusuario(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consultaCall = conexion.prepareCall(consulta)) {
            
            consultaCall.setInt(1, usuario.getIdUsuario());
            consultaCall.setString(2, usuario.getNombreUsuario());
            consultaCall.setString(3, usuario.getNombre());
            consultaCall.setString(4, usuario.getApellido());
            consultaCall.setString(5, usuario.getCorreo());
            consultaCall.setString(6, usuario.getRol().toLowerCase());
            consultaCall.setBoolean(7, usuario.isEstado());
            
            return consultaCall.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int idUsuario) {
        String consulta = "{call sp_eliminarusuario(?)}";
        try (Connection conexion = ConexionSingleton.getConexion();
             CallableStatement consultaCall = conexion.prepareCall(consulta)) {
            consultaCall.setInt(1, idUsuario);
            return consultaCall.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validarContrasenaActual(int idUsuario, String contrasenaIngresada) {
        String sql = "SELECT 1 FROM usuarios WHERE id_usuario = ? AND contrasena = SHA2(?, 256)";
        try (Connection conn = ConexionSingleton.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.setString(2, contrasenaIngresada);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al validar contraseña actual: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizarPassword(int idUsuario, String nuevaPassword) {
        String sql = "UPDATE usuarios SET contrasena = SHA2(?, 256) WHERE id_usuario = ?";
        try (Connection conn = ConexionSingleton.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevaPassword);
            pstmt.setInt(2, idUsuario);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Usuario autenticar(TextField txtUsuario, String passText) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}