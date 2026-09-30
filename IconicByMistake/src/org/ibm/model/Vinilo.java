package org.ibm.model;

public class Vinilo {
    private int idVinilo;
    private String titulo;
    private String anioLanzamiento; // Cambiado a String para admitir texto libre o fechas
    private double precio;
    private int stock;
    private String Sku;
    private String urlFoto;
    
    // Relaciones
    private Artista artista;
    private Genero genero;
    private Productor productor;

    public Vinilo() {
    }

    public Vinilo(int idVinilo, String titulo, String anioLanzamiento, double precio, int stock, String Sku, String urlFoto, Artista artista, Genero genero, Productor productor) {
        this.idVinilo = idVinilo;
        this.titulo = titulo;
        this.anioLanzamiento = anioLanzamiento;
        this.precio = precio;
        this.stock = stock;
        this.Sku = Sku;
        this.urlFoto = urlFoto;
        this.artista = artista;
        this.genero = genero;
        this.productor = productor;
    }

    @Override
    public String toString() {
        return titulo + " - Q" + precio;
    }

    public int getIdVinilo() {
        return idVinilo;
    }

    public void setIdVinilo(int idVinilo) {
        this.idVinilo = idVinilo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAnioLanzamiento() {
        return anioLanzamiento;
    }

    public void setAnioLanzamiento(String anioLanzamiento) {
        this.anioLanzamiento = anioLanzamiento;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getSku() {
        return Sku;
    }

    public void setSku(String Sku) {
        this.Sku = Sku;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
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
}