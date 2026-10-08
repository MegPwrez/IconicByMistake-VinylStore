package org.ibm.model;

import java.util.ArrayList;
import java.util.List;

public class Factura {

    private int numeroFactura;
    private String fechaEmision;
    private long cuiCliente;
    private String nombreCliente;
    private String correoCliente;
    private String usuarioAtendio;
    private double granTotal;
    
    // Lista de detalles de venta (vinilos/álbumes facturados)
    private List<DetalleVenta> detalles;

    public Factura() {
        this.detalles = new ArrayList<>();
    }

    public Factura(int numeroFactura, String fechaEmision, long cuiCliente, String nombreCliente, 
                   String correoCliente, String usuarioAtendio, double granTotal) {
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.cuiCliente = cuiCliente;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.usuarioAtendio = usuarioAtendio;
        this.granTotal = granTotal;
        this.detalles = new ArrayList<>();
    }

    // Getters y Setters
    public int getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(int numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public long getCuiCliente() {
        return cuiCliente;
    }

    public void setCuiCliente(long cuiCliente) {
        this.cuiCliente = cuiCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getUsuarioAtendio() {
        return usuarioAtendio;
    }

    public void setUsuarioAtendio(String usuarioAtendio) {
        this.usuarioAtendio = usuarioAtendio;
    }

    public double getGranTotal() {
        return granTotal;
    }

    public void setGranTotal(double granTotal) {
        this.granTotal = granTotal;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
    }

    @Override
    public String toString() {
        return "Factura No. " + numeroFactura + " - Cliente: " + nombreCliente + " - Total: Q " + granTotal;
    }
}