package org.ibm.model;

public class DetalleVenta {

    private int idDetalleventa;
    private int noVenta;
    private String codigoBarras;
    private String tituloAlbum;
    private int cantidad;
    private double precioUnitario;
    private double subTotal;

    public DetalleVenta() {
    }

    public DetalleVenta(String codigoBarras, String tituloAlbum, double precioUnitario, int cantidad) {
        this.codigoBarras = codigoBarras;
        this.tituloAlbum = tituloAlbum;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.subTotal = precioUnitario * cantidad;
    }

    public DetalleVenta(int idDetalleventa, int noVenta, String codigoBarras, String tituloAlbum, int cantidad, double precioUnitario) {
        this.idDetalleventa = idDetalleventa;
        this.noVenta = noVenta;
        this.codigoBarras = codigoBarras;
        this.tituloAlbum = tituloAlbum; // Se corrigió la asignación del título
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subTotal = precioUnitario * cantidad;
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

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getTituloAlbum() {
        return tituloAlbum;
    }

    public void setTituloAlbum(String tituloAlbum) {
        this.tituloAlbum = tituloAlbum;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subTotal = this.precioUnitario * cantidad; // Actualiza el subtotal al cambiar cantidad
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.subTotal = precioUnitario * this.cantidad; // Actualiza el subtotal al cambiar precio
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
}