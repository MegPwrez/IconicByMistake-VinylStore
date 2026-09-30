
package org.ibm.utils;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.ibm.model.Usuario;

public class Navegador {

    public static void redirigirSegunRol(Stage stage) {
        Usuario usuario = SesionUsuario.getInstancia().getUsuarioActual();

        if (usuario == null) {
            cargarVista(stage, "/org/lsa/view/Login.fxml", "Inicio de Sesión");
            return;
        }

        String rol = usuario.getRol() != null ? usuario.getRol().toLowerCase() : "";
        switch (rol) {
            case "admin":
                cargarVista(stage, "/org/lsa/view/DashboardAdminController.fxml", "Panel Administración");
                break;
            case "bodega":
                cargarVista(stage, "/org/lsa/view/DashboardBodegaController.fxml", "Panel Bodega");
                break;
            case "cajero":
                cargarVista(stage, "/org/lsa/view/DashboardCajeroController.fxml", "Panel Caja");
                break;
            default:
                mostrarAlertaAccesoDenegado();
                SesionUsuario.getInstancia().cerrarSesion();
                cargarVista(stage, "/org/lsa/view/LoginView.fxml", "Inicio de Sesión");
                break;
        }
    }

    public static void cargarVista(Stage stage, String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(fxmlPath));
            Parent root = loader.load();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void mostrarAlertaAccesoDenegado() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Acceso Denegado");
        alert.setHeaderText(null);
        alert.setContentText("No tiene permisos para acceder a esta área.");
        alert.showAndWait();
    }
}