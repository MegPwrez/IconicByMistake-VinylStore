package org.ibm.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.ibm.utils.SesionUsuario;

public class DashboardAdminController implements Initializable {

    @FXML
    private Label lblBienvenida;
    @FXML
    private Label lblRol;
    @FXML
    private Label lblVentasDia;
    @FXML
    private Label lblVentasMes;
    @FXML
    private Label lblViniloMasVendido;
    @FXML
    private Button btnUsuarios;
    @FXML
    private Button btnReportes;
    @FXML
    private Button btnCatalogo;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private Button btnCambiarContrasena;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        lblBienvenida.setText("Panel de Administración");
        lblRol.setText("Administrador");

        // Valores temporales hasta conectar los reportes con la BD.
        lblVentasDia.setText("Q0.00");
        lblVentasMes.setText("Q0.00");
        lblViniloMasVendido.setText("Sin datos");
    }

    @FXML
    private void irAUsuarios() {
        cambiarVista(
                "/org/ibm/view/GestionUsuariosView.fxml",
                "Iconic By Mistake - Gestión de Usuarios"
        );
    }

    @FXML
    private void irAReportes() {
        System.out.println("Ir a reportes");
    }

    @FXML
    private void irACatalogo() {
        System.out.println("Ir a catálogo");
    }

    @FXML
    private void cerrarSesion() {
        SesionUsuario.getInstancia().cerrarSesion();

        cambiarVista(
                "/org/ibm/view/LoginView.fxml",
                "Iconic By Mistake - Inicio de Sesión"
        );
    }
    
    @FXML
    private void irACambiarContrasena() {
    cambiarVista(
            "/org/ibm/view/CambioContrasenaView.fxml",
            "Iconic By Mistake - Cambiar contraseña"
        );
    }

    private void cambiarVista(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(rutaFXML)
            );

            Parent root = loader.load();

            Stage stage = (Stage) btnUsuarios.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException | NullPointerException e) {
            mostrarError(
                    "No se pudo cargar la vista:\n" + rutaFXML
            );

            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error al cargar la vista");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}