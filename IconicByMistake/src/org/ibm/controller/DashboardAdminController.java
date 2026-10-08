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

import org.ibm.dao.DashboardAdminDAO;
import org.ibm.dao.impl.DashboardAdminDAOImpl;
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

    private DashboardAdminDAO dashboardAdminDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        dashboardAdminDAO = new DashboardAdminDAOImpl();

        cargarDatosIniciales();
        cargarIndicadores();
    }

    private void cargarDatosIniciales() {

        lblBienvenida.setText("Panel de Administración");
        lblRol.setText("Administrador");
    }

    private void cargarIndicadores() {

        try {

            double ventasDia
                    = dashboardAdminDAO.obtenerVentasDia();

            double ventasMes
                    = dashboardAdminDAO.obtenerVentasMes();

            String viniloMasVendido
                    = dashboardAdminDAO.obtenerViniloMasVendido();

            lblVentasDia.setText(
                    String.format("Q%.2f", ventasDia)
            );

            lblVentasMes.setText(
                    String.format("Q%.2f", ventasMes)
            );

            if (viniloMasVendido == null
                    || viniloMasVendido.isBlank()) {

                lblViniloMasVendido.setText("Sin datos");

            } else {

                lblViniloMasVendido.setText(
                        viniloMasVendido
                );
            }

        } catch (Exception e) {

            lblVentasDia.setText("Q0.00");
            lblVentasMes.setText("Q0.00");
            lblViniloMasVendido.setText("Sin datos");

            System.err.println(
                    "Error al cargar indicadores del Dashboard Admin: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
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

       cambiarVista(
                "/org/ibm/view/CatalogoView.fxml",
                "Iconic By Mistake - Cambiar contraseña"
        );
    }

    @FXML
    private void irACambiarContrasena() {

        cambiarVista(
                "/org/ibm/view/CambioContrasenaView.fxml",
                "Iconic By Mistake - Cambiar contraseña"
        );
    }

    @FXML
    private void cerrarSesion() {

        SesionUsuario.getInstancia().cerrarSesion();

        cambiarVista(
                "/org/ibm/view/LoginView.fxml",
                "Iconic By Mistake - Inicio de Sesión"
        );
    }

    private void cambiarVista(
            String rutaFXML,
            String titulo) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(rutaFXML)
            );

            Parent root = loader.load();

            Stage stage = (Stage) btnUsuarios
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException | NullPointerException e) {

            mostrarError(
                    "No se pudo cargar la vista:\n"
                    + rutaFXML
            );

            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {

        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

        alert.setTitle("Error");
        alert.setHeaderText(
                "Error al cargar la vista"
        );

        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}