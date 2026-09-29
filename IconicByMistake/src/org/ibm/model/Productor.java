package org.ibm.model;

public class Productor {
    private int idProductor;
    private String nombreProductor;
    private String selloDiscografico;

    public Productor() {
    }

    public Productor(int idProductor, String nombreProductor, String selloDiscografico) {
        this.idProductor = idProductor;
        this.nombreProductor = nombreProductor;
        this.selloDiscografico = selloDiscografico;
    }

    public int getIdProductor() {
        return idProductor;
    }

    public void setIdProductor(int idProductor) {
        this.idProductor = idProductor;
    }

    public String getNombreProductor() {
        return nombreProductor;
    }

    public void setNombreProductor(String nombreProductor) {
        this.nombreProductor = nombreProductor;
    }

    public String getSelloDiscografico() {
        return selloDiscografico;
    }

    public void setSelloDiscografico(String selloDiscografico) {
        this.selloDiscografico = selloDiscografico;
    }

    @Override
    public String toString() {
        return nombreProductor + " (" + selloDiscografico + ")";
    }
}