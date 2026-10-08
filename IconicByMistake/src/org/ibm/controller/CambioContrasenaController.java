package org.ibm.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import org.ibm.dao.UsuarioDAO;
import org.ibm.dao.impl.UsuarioDAOImpl;
import org.ibm.model.Usuario;
import org.ibm.utils.SesionUsuario;

public class CambioContrasenaController {

    @FXML
    private PasswordField txtContrasenaActual;
    @FXML
    private PasswordField txtNuevaContrasena;
    @FXML
    private PasswordField txtConfirmarContrasena;
    @FXML
    private Button btnCambiar;
    @FXML
    private Button btnVolver;
    private UsuarioDAO usuarioDAO;
    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAOImpl();
    
    }

    @FXML
    private void cambiarContrasena() {

        String actual = txtContrasenaActual.getText();
        String nueva = txtNuevaContrasena.getText();
        String confirmar = txtConfirmarContrasena.getText();

        if (actual == null || actual.isBlank()
                || nueva == null || nueva.isBlank()
                || confirmar == null || confirmar.isBlank()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campos requeridos",
                    "Debe completar todos los campos."
            );

            return;
        }

        if (!nueva.equals(confirmar)) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Contraseñas diferentes",
                    "La nueva contraseña y la confirmación no coinciden."
            );

            return;
        }

        if (nueva.length() < 6) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Contraseña inválida",
                    "La nueva contraseña debe tener al menos 6 caracteres."
            );

            return;
        }

        Usuario usuario = SesionUsuario
                .getInstancia()
                .getUsuarioActual();

        if (usuario == null) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Sesión inválida",
                    "No existe un usuario autenticado."
            );

            return;
        }

        boolean contrasenaValida
                = usuarioDAO.validarContrasenaActual(
                        usuario.getIdUsuario(),
                        actual
                );

        if (!contrasenaValida) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Contraseña incorrecta",
                    "La contraseña actual no es correcta."
            );

            return;
        }

        boolean actualizado
                = usuarioDAO.actualizarPassword(
                        usuario.getIdUsuario(),
                        nueva
                );

        if (actualizado) {

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Contraseña actualizada",
                    "La contraseña fue actualizada correctamente."
            );

            limpiarCampos();

        } else {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo actualizar la contraseña."
            );
        }
    }

    private void limpiarCampos() {
        txtContrasenaActual.clear();
        txtNuevaContrasena.clear();
        txtConfirmarContrasena.clear();
    }

    @FXML
    private void volverAlDashboard() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/org/ibm/view/DashboardAdminView.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage) btnVolver
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));

            stage.setTitle(
                    "Iconic By Mistake - Panel de Administración"
            );

            stage.centerOnScreen();
            stage.show();

        } catch (IOException | NullPointerException e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo regresar al panel de administración."
            );

            e.printStackTrace();
        }
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}