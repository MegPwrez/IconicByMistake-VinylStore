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
            cargarVista(
                    stage,
                    "/org/ibm/view/LoginView.fxml",
                    "Inicio de Sesión"
            );
            return;
        }

        String rol = usuario.getRol() != null
                ? usuario.getRol().trim().toLowerCase()
                : "";

        switch (rol) {

            case "admin":
                cargarVista(
                        stage,
                        "/org/ibm/view/DashboardAdminView.fxml",
                        "Panel de Administración"
                );
                break;

            case "bodega":
                cargarVista(
                        stage,
                        "/org/ibm/view/DashboardBodegaView.fxml",
                        "Panel de Bodega"
                );
                break;

            case "cajero":
                cargarVista(
                        stage,
                        "/org/ibm/view/DashboardCajeroView.fxml",
                        "Panel de Caja"
                );
                break;

            default:
                mostrarAlertaAccesoDenegado();
                SesionUsuario.getInstancia().cerrarSesion();

                cargarVista(
                        stage,
                        "/org/ibm/view/LoginView.fxml",
                        "Inicio de Sesión"
                );
                break;
        }
    }

    public static void cargarVista(Stage stage, String fxmlPath, String titulo) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    Navegador.class.getResource(fxmlPath)
            );

            Parent root = loader.load();

            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException | NullPointerException e) {

            mostrarErrorCarga(fxmlPath);
            e.printStackTrace();
        }
    }

    private static void mostrarAlertaAccesoDenegado() {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Acceso denegado");
        alert.setHeaderText(null);
        alert.setContentText(
                "No tiene permisos para acceder a esta área."
        );

        alert.showAndWait();
    }

    private static void mostrarErrorCarga(String vista) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("No se pudo cargar la vista");
        alert.setContentText(
                "Vista: " + vista
        );

        alert.showAndWait();
    }
}