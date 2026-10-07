
package org.ibm.model;


public class DetalleVenta {

    private int idDetalleventa;
    private int noVenta;
    private String isbn;
    private String titulo;
    private int cantidad;
    private double precioUnitario;
    private double subTotalDetalle;

    public DetalleVenta() {
    }
    public DetalleVenta(String isbn, String titulo, double precioUnitario, int cantidad) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.subTotalDetalle = precioUnitario * cantidad;
    }
    public DetalleVenta(int idDetalleventa, int noVenta, String isbn, String titulo, int cantidad, double precioUnitario) {
        this.idDetalleventa = idDetalleventa;
        this.noVenta = noVenta;
        this.isbn = isbn;
        this.titulo = titulo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subTotalDetalle = precioUnitario * cantidad;
    }
    public int getIdDetalleventa() {
        return idDetalleventa;
    }
    public void setIdDetalleventa(int idDetalleventa) {
        this.idDetalleventa = idDetalleventa;
    }

    public int getNoVenta() {
        return noVenta;
    }

    public void setNoVenta(int noVenta) {
        this.noVenta = noVenta;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubTotalDetalle() {
        return subTotalDetalle;
    }

    public void setSubTotalDetalle(double subTotalDetalle) {
        this.subTotalDetalle = subTotalDetalle;
    }

  
}