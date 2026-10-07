package org.ibm.model;

import java.sql.Date;

public class Vinilo {

    private String codigoBarras;
    private String tituloAlbum;
    private Date fechaLanzamiento;
    private double precio;
    private int stockActual;
    private int stockMinimo;
    private boolean activo;

    // Llaves foráneas y Relaciones
    private int idGenero;
    private String nitDisquera;
    private Integer idProveedor;

    private Artista artista;
    private Genero genero;
    private Productor productor;

    public Vinilo() {
    }

    public Vinilo(String codigoBarras, String tituloAlbum, Date fechaLanzamiento, double precio, 
                  int stockActual, int stockMinimo, boolean activo, int idGenero, 
                  String nitDisquera, Integer idProveedor) {
        this.codigoBarras = codigoBarras;
        this.tituloAlbum = tituloAlbum;
        this.fechaLanzamiento = fechaLanzamiento;
        this.precio = precio;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.activo = activo;
        this.idGenero = idGenero;
        this.nitDisquera = nitDisquera;
        this.idProveedor = idProveedor;
    }

    // Getters y Setters alineados con tu controlador y base de datos
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

    public Date getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(Date fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(int idGenero) {
        this.idGenero = idGenero;
    }

    public String getNitDisquera() {
        return nitDisquera;
    }

    public void setNitDisquera(String nitDisquera) {
        this.nitDisquera = nitDisquera;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public Productor getProductor() {
        return productor;
    }

    public void setProductor(Productor productor) {
        this.productor = productor;
    }

    @Override
    public String toString() {
        return codigoBarras + " - " + tituloAlbum;
    }
}