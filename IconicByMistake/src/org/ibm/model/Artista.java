package org.ibm.model;

public class Artista {
    private int idArtista;
    private String nombreArtistico;
    private String paisOrigen;

    public Artista() {
    }

    public Artista(int idArtista, String nombreArtistico, String paisOrigen) {
        this.idArtista = idArtista;
        this.nombreArtistico = nombreArtistico;
        this.paisOrigen = paisOrigen;
    }

    public int getIdArtista() {
        return idArtista;
    }

    public void setIdArtista(int idArtista) {
        this.idArtista = idArtista;
    }

    public String getNombreArtistico() {
        return nombreArtistico;
    }

    public void setNombreArtistico(String nombreArtistico) {
        this.nombreArtistico = nombreArtistico;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    @Override
    public String toString() {
        return nombreArtistico;
    }
}