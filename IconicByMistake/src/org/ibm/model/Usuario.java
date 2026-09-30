
package org.ibm.model;

public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String correo;
    private String contraseña;
    private String rol;
    private String Apellido;
    private String Nombre;
    private boolean estado;
 
    public Usuario() {
    }
 
    public Usuario(int idUsuario, String nombreUsuario, String correo, String contraseña, String rol, String Apellido, String Nombre, boolean estado) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.contraseña = contraseña;
        this.rol = rol;
        this.Apellido = Apellido;
        this.Nombre = Nombre;
        this.estado = estado;
    }
 
    public int getIdUsuario() {
        return idUsuario;
    }
 
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
 
    public String getNombreUsuario() {
        return nombreUsuario;
    }
 
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
 
    public String getCorreo() {
        return correo;
    }
 
    public void setCorreo(String correo) {
        this.correo = correo;
    }
 
    public String getContraseña() {
        return contraseña;
    }
 
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
 
    public String getRol() {
        return rol;
    }
 
    public void setRol(String rol) {
        this.rol = rol;
    }
    public String getApellido() {
        return Apellido;
    }
 
    public void setApellido(String Apellido) {
        this.Apellido = Apellido;
    }
 
    public String getNombre() {
        return Nombre;
    }
 
    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }
 
    public boolean isEstado() {
        return estado;
    }
 
    public void setEstado(boolean estado) {
        this.estado = estado;
    }
 
}