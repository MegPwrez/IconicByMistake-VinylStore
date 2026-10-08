package org.ibm.dao;

import java.util.List;
import org.ibm.model.Usuario;

public interface UsuarioDAO {

    Usuario autenticar(String correo, String contrasena);

    List<Usuario> listarTodos();

    Usuario buscarUsuario(int idUsuario);

    boolean insertar(Usuario usuario);

    boolean actualizar(Usuario usuario);

    boolean eliminar(int idUsuario);

    boolean validarContrasenaActual(int idUsuario, String contrasenaIngresada);

    boolean actualizarPassword(int idUsuario, String nuevaPassword);
}