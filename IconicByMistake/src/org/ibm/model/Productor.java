package org.ibm.model;

public class Productor {
    private String idProductor;
    private String nombreProductor;
    private String selloDiscografico;

    public Productor() {
    }

    public Productor(String idProductor, String nombreProductor, String selloDiscografico) {
        this.idProductor = idProductor;
        this.nombreProductor = nombreProductor;
        this.selloDiscografico = selloDiscografico;
    }

    public String getIdProductor() {
        return idProductor;
    }

    public void setIdProductor(String idProductor) {
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