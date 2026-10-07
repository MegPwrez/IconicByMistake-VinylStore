package org.ibm.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

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
        System.out.println("Ir a gestión de usuarios");
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
        System.out.println("Cerrar sesión");
    }
}