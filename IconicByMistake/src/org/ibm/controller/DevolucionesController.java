package org.ibm.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.ibm.Main;
import org.ibm.dao.impl.DevolucionDAOImpl;
import org.ibm.dao.impl.UsuarioDAOImpl;
import org.ibm.model.Usuario;
import org.ibm.utils.SesionUsuario;

public class DevolucionesController {
    @FXML private TextField txtIdVenta;
    @FXML private TextField txtAdministrador;
    @FXML private PasswordField txtContrasena;
    @FXML private TextArea txtMotivo;

    @FXML private void devolver() {
        try {
            Usuario sesion = SesionUsuario.getInstancia().getUsuarioActual();
            if (sesion == null) throw new IllegalArgumentException("Inicie sesion.");
            Usuario admin = new UsuarioDAOImpl().autenticar(
                    txtAdministrador.getText().trim(), txtContrasena.getText());
            if (admin == null || !"admin".equalsIgnoreCase(admin.getRol()) || !admin.isEstado()) {
                throw new IllegalArgumentException("Credenciales de administrador invalidas o inactivas.");
            }
            int id = Integer.parseInt(txtIdVenta.getText().trim());
            new DevolucionDAOImpl().devolverVenta(id, admin.getIdUsuario(), txtMotivo.getText());
            txtContrasena.clear();
            new Alert(Alert.AlertType.INFORMATION, "Devolucion registrada; stock restituido.").showAndWait();
            volver();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Ingrese un numero de venta valido.").showAndWait();
        } catch (Exception e) {
            txtContrasena.clear();
            new Alert(Alert.AlertType.ERROR, "No se proceso la devolucion: " + e.getMessage()).showAndWait();
        }
    }
    @FXML private void volver() {
        try { Main.cambiarVista("/org/ibm/view/DashboardAdminView.fxml"); }
        catch (Exception e) { new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait(); }
    }
}
