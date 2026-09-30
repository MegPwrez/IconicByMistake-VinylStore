package org.ibm.model;

public class Artista {
    private int idArtista;
    private String nombreArtistico;
    private String nacionalidad;
    private String biografia;

    public Artista() {
    }

    public Artista(int idArtista, String nombreArtistico, String nacionalidad, String biografia) {
        this.idArtista = idArtista;
        this.nombreArtistico = nombreArtistico;
        this.nacionalidad = nacionalidad;
        this.biografia = biografia;
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

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    @Override
    public String toString() {
        return nombreArtistico;
    }
}