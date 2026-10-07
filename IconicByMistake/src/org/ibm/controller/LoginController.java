
package org.ibm.controller;



import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.ibm.dao.UsuarioDAO;
import org.ibm.dao.impl.UsuarioDAOImpl;
import org.ibm.model.Usuario;
import org.ibm.utils.ControlAcceso;
import org.ibm.utils.SesionUsuario;

public class LoginController {

    private static final Logger log = Logger.getLogger(LoginController.class.getName());

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnIngresar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @FXML
    private void handleLogin(ActionEvent event) {
        String correoText = txtUsuario.getText() != null ? txtUsuario.getText().trim() : "";
        String passText = txtContrasena.getText() != null ? txtContrasena.getText() : "";

        log.info("Intento de inicio de sesión registrado para el usuario: " + correoText);

        if (correoText.isEmpty() || passText.isEmpty()) {
            log.warning("Intento de login fallido: Uno o más campos se encuentran vacíos.");
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor, ingrese su correo/usuario y contraseña.");
            return;
        }

        Usuario usuarioLogueado = usuarioDAO.autenticar(correoText, passText);

        if (usuarioLogueado == null) {
            log.warning("Acceso denegado para el correo/usuario: " + correoText + " (Credenciales inválidas o usuario inactivo).");
            mostrarAlerta(Alert.AlertType.ERROR, "Acceso Denegado", "Correo o contraseña incorrectos, o usuario inactivo.");
            return;
        }

        ControlAcceso.setUsuarioLogueado(usuarioLogueado);
        SesionUsuario.getInstancia().iniciarSesion(usuarioLogueado);

        String fxmlPath;
        String tituloVentana;
        String rol = usuarioLogueado.getRol() != null ? usuarioLogueado.getRol().toLowerCase() : "";

        log.info("Autenticación exitosa. Usuario: " + correoText + " | Rol asignado: " + rol);

        switch (rol) {
            case "admin":
            case "administrador":
                fxmlPath = "/org/lsa/ibm/DashboardAdminView.fxml";
                tituloVentana = "Librería Saturno - Panel de Administración";
                break;
            case "cajero":
                fxmlPath = "/org/ibm/view/CajeroDashboardView.fxml";
                tituloVentana = "Librería Saturno - Módulo de Ventas";
                break;
            case "empleado":
            case "bodega":
                fxmlPath ="/org/ibm/view/DashboardBodegaView.fxml";
                tituloVentana = "Iconic By Mistake- Panel  Bodega";
                break;
            default:
                log.severe("El rol '" + rol + "' asignado al usuario " + correoText + " no tiene una vista FXML configurada.");
                mostrarAlerta(Alert.AlertType.ERROR, "Rol no autorizado", "El rol asignado (" + rol + ") no tiene una interfaz configurada.");
                return;
        }

        try {
            log.info("Cargando la interfaz desde: " + fxmlPath);
            Stage escenarioPrincipal = (Stage) btnIngresar.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            escenarioPrincipal.setTitle(tituloVentana);
            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.centerOnScreen();
            escenarioPrincipal.show();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error de I/O al cargar la vista FXML: " + fxmlPath, e);
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de carga", "No se pudo abrir la vista:\n" + fxmlPath + "\n\nCausa: El archivo FXML interno tiene un error de componentes.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        log.info("Mostrando alerta [Tipo: " + tipo + "]: " + titulo);
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}