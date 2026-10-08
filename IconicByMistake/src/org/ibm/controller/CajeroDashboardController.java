package org.ibm.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.ibm.Main;
import org.ibm.model.Usuario;
import org.ibm.utils.SesionUsuario;

public class CajeroDashboardController implements Initializable {

    private static final Logger log = Logger.getLogger(CajeroDashboardController.class.getName());

    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRolUsuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        Usuario usuario = SesionUsuario.getInstancia().getUsuarioActual();
        if (usuario != null) {
            String nombre = (usuario.getNombre() != null && !usuario.getNombre().trim().isEmpty()) 
                    ? usuario.getNombre().trim() 
                    : (usuario.getNombreUsuario() != null ? usuario.getNombreUsuario() : "");
            
            String apellido = (usuario.getApellido() != null) ? usuario.getApellido().trim() : "";
            String rol = (usuario.getRol() != null && !usuario.getRol().trim().isEmpty()) ? usuario.getRol() : "Cajero";

            if (lblNombreUsuario != null) {
                lblNombreUsuario.setText(nombre);
            }

            if (lblRolUsuario != null) {
                String inicialNombre = !nombre.isEmpty() ? nombre.substring(0, 1).toUpperCase() : "U";
                String inicialApellido = !apellido.isEmpty() ? apellido.substring(0, 1).toUpperCase() : "";
                
                if (!inicialApellido.isEmpty()) {
                    lblRolUsuario.setText(inicialNombre + inicialApellido + " · " + rol);
                } else {
                    lblRolUsuario.setText(inicialNombre + " · " + rol);
                }
            }
        }
    }

    @FXML
    public void handleNuevaVenta(ActionEvent event) {
        log.info("Navegando a la pantalla de Nueva Venta.");
        navegar("/org/ibm/view/NuevaVentaView.fxml");
    }

    @FXML
    public void handleResumenDia(ActionEvent event) {
        log.info("Navegando a la pantalla de Resumen del Día.");
        navegar("/org/ibm/view/ResumenDelDíaView.fxml");
    }

    @FXML
    public void handleDetalleVentas(ActionEvent event) {
        log.info("Navegando a la pantalla de Detalle de Venta.");
        navegar("/org/ibm/view/DetalleVentaView.fxml");
    }

    @FXML
    public void handleListaVentas(ActionEvent event) {
        log.info("Navegando a Lista de Ventas.");
        navegar("/org/ibm/view/ListaVentasView.fxml");
    }

    @FXML
    public void handleClientes(ActionEvent event) {
        log.info("Navegando a la pantalla de Clientes.");
        navegar("/org/ibm/view/ClienteView.fxml");
    }

    @FXML
    public void handleCerrarSesion(ActionEvent event) {
        log.info("Cerrando sesión de usuario e intentando volver al Login.");
        SesionUsuario.getInstancia().cerrarSesion();
        navegar("/org/ibm/view/LoginView.fxml");
    }

    private void navegar(String rutaFxml) {
        try {
            Main.cambiarVista(rutaFxml);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error al intentar cargar la vista: " + rutaFxml, e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error de interfaz", "No se pudo cargar la vista seleccionada: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}