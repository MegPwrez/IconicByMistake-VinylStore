package org.lsa.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.lsa.model.Usuario;
import org.lsa.utils.SesionUsuario;

public class DashboardCajeroController implements Initializable {

    private static final Logger log = Logger.getLogger(DashboardCajeroController.class.getName());

    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRolUsuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatosUsuario();
    }
private void cargarDatosUsuario() {
    Usuario usuario = SesionUsuario.getInstancia().getUsuarioActual();
    if (usuario != null) {
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : usuario.getNombreUsuario();
        String apellido = usuario.getApellido() != null ? usuario.getApellido() : "";
        String rol = usuario.getRol() != null ? usuario.getRol() : "Cajero";
        if (lblNombreUsuario != null) {
            lblNombreUsuario.setText(nombre);
        }
        if (lblRolUsuario != null) {
            String inicialNombre = !nombre.isEmpty() ? nombre.substring(0, 1).toUpperCase() : "U";
            String inicialApellido = !apellido.isEmpty() ? apellido.substring(0, 1).toUpperCase() : "";
            lblRolUsuario.setText(inicialNombre + inicialApellido + " · " + rol);
        }
    }
}
    @FXML
    public void handleNuevaVenta(ActionEvent event) {
        log.info("Navegando a la pantalla de Nueva Venta.");
        navegar(event, "/org/lsa/view/NuevaVentaView.fxml", "Registro de Venta");
    }

    @FXML
    public void handleResumenDia(ActionEvent event) {
         log.info("Navegando a la pantalla de Nueva Venta.");
        navegar(event, "/org/lsa/view/ResumenDelDiaView.fxml", "Registro de Venta");
    }

    @FXML
    public void handleDetalleVentas(ActionEvent event) {
       log.info("Navegando a la pantalla de Nueva Venta.");
        navegar(event, "/org/lsa/view/DetalleVentaview.fxml", "Registro de Venta");
    }

    @FXML
    public void handleListaVentas(ActionEvent event) {
        log.info("Navegando a Lista de Ventas.");
      navegar(event, "/org/lsa/view/ListaVentasView.fxml", "Listado de Venta");
    }

    @FXML
    public void handleCerrarSesion(ActionEvent event) {
        log.info("Cerrando sesión de usuario e intentando volver al Login.");
        SesionUsuario.getInstancia().cerrarSesion();
        navegar(event, "/org/lsa/view/LoginView.fxml", "Inicio de Sesión");
    }

    
    @FXML    
    public void handleClientes (ActionEvent evento) {       
        navegar(evento, "/org/lsa/view/ClienteView.fxml", "Librería Saturno - Clientes");    }     

 
 

    private void navegar(ActionEvent event, String rutaFxml, String tituloVista) {
        try {
            Stage escenarioPrincipal = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            escenarioPrincipal.setTitle(tituloVista);
            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.show();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error al intentar cargar la vista: " + rutaFxml, e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error de interfaz", "No se pudo cargar la vista seleccionada.");
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