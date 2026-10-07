
package org.ibm.manager;

import org.ibm.model.Usuario;

public class SesionContext {

    private static SesionContext instancia;
    private Usuario usuarioActual;

    private SesionContext() {
    }

    public static synchronized SesionContext getInstancia() {
        if (instancia == null) {
            instancia = new SesionContext();
        }
        return instancia;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }
}
