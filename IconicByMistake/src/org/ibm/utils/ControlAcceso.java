
package org.ibm.utils;

import org.ibm.model.Usuario;

/**
 *
 * @author Gregory Jerónimo
 */
public class ControlAcceso {

    private static Usuario usuarioLogueado;

    public static Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public static void setUsuarioLogueado(Usuario usuario) {
        usuarioLogueado = usuario;
    }

    public static void cerrarSesion() {
        usuarioLogueado = null;
    }

    public static boolean tienePermiso(Usuario usuario, String vistaDestino) {
        if (usuario == null || !usuario.isEstado()) {
            return false;
        }

        String rol = usuario.getRol().toLowerCase();

        switch (vistaDestino) {
            case "DashboardAdminController.fxml":
                return rol.equals("admin") || rol.equals("administrador");
            case "DashboardBodegaController.fxml":
                return rol.equals("admin") || rol.equals("administrador") || rol.equals("bodega") || rol.equals("empleado");
            case "DashboardCajeroController.fxml":
                return rol.equals("admin") || rol.equals("administrador") || rol.equals("cajero");
            default:
                return false;
        }
    }
}