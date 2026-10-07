package org.ibm.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.ibm.dao.UsuarioDAO;
import org.ibm.dao.impl.UsuarioDAOImpl;
import org.ibm.model.Usuario;

public class GestionUsuariosController implements Initializable {


    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private ComboBox<String> cbRol;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnVolver;



    @FXML
    private TableView<Usuario> tblUsuarios;
    @FXML
    private TableColumn<Usuario, Integer> colId;
    @FXML
    private TableColumn<Usuario, String> colUsuario;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colApellido;
    @FXML
    private TableColumn<Usuario, String> colCorreo;
    @FXML
    private TableColumn<Usuario, String> colRol;
    @FXML
    private TableColumn<Usuario, Boolean> colEstado;


    private UsuarioDAO usuarioDAO;

    private ObservableList<Usuario> listaUsuarios;

    private Usuario usuarioSeleccionado;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        usuarioDAO = new UsuarioDAOImpl();

        configurarComboBox();
        configurarTabla();
        cargarUsuarios();
        configurarSeleccionTabla();
    }

    private void configurarComboBox() {

        cbRol.setItems(FXCollections.observableArrayList(
                "admin",
                "empleado",
                "cajero"
        ));

        cbEstado.setItems(FXCollections.observableArrayList(
                "Activo",
                "Inactivo"
        ));
    }


    private void configurarTabla() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("idUsuario")
        );

        colUsuario.setCellValueFactory(
                new PropertyValueFactory<>("nombreUsuario")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colApellido.setCellValueFactory(
                new PropertyValueFactory<>("apellido")
        );

        colCorreo.setCellValueFactory(
                new PropertyValueFactory<>("correo")
        );

        colRol.setCellValueFactory(
                new PropertyValueFactory<>("rol")
        );

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );
    }


    private void cargarUsuarios() {

        try {

            listaUsuarios = FXCollections.observableArrayList(
                    usuarioDAO.listarTodos()
            );

            tblUsuarios.setItems(listaUsuarios);

        } catch (Exception e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudieron cargar los usuarios."
            );

            e.printStackTrace();
        }
    }


    private void configurarSeleccionTabla() {

        tblUsuarios
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {

                    if (seleccionado != null) {

                        usuarioSeleccionado = seleccionado;

                        cargarDatosUsuario(seleccionado);
                    }
                });
    }

    private void cargarDatosUsuario(Usuario usuario) {

        txtUsuario.setText(
                usuario.getNombreUsuario()
        );

        txtNombre.setText(
                usuario.getNombre()
        );

        txtApellido.setText(
                usuario.getApellido()
        );

        txtCorreo.setText(
                usuario.getCorreo()
        );

        txtContrasena.clear();

        cbRol.setValue(
                usuario.getRol()
        );

        cbEstado.setValue(
                usuario.isEstado()
                        ? "Activo"
                        : "Inactivo"
        );
    }


    @FXML
    private void guardarUsuario() {

        if (!validarCampos(true)) {
            return;
        }

        try {

            Usuario usuario = new Usuario();

            usuario.setNombreUsuario(
                    txtUsuario.getText().trim()
            );

            usuario.setNombre(
                    txtNombre.getText().trim()
            );

            usuario.setApellido(
                    txtApellido.getText().trim()
            );

            usuario.setCorreo(
                    txtCorreo.getText().trim()
            );

            usuario.setContraseña(
                    txtContrasena.getText()
            );

            usuario.setRol(
                    cbRol.getValue()
            );

            usuario.setEstado(
                    "Activo".equals(cbEstado.getValue())
            );

            boolean registrado = usuarioDAO.insertar(usuario);

            if (registrado) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Usuario registrado",
                        "El usuario fue registrado correctamente."
                );

                cargarUsuarios();
                limpiarFormulario();

            } else {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo registrar el usuario."
                );
            }

        } catch (Exception e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Ocurrió un error al registrar el usuario."
            );

            e.printStackTrace();
        }
    }


    @FXML
    private void actualizarUsuario() {

        if (usuarioSeleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccione un usuario",
                    "Debe seleccionar un usuario de la tabla."
            );

            return;
        }

        if (!validarCampos(false)) {
            return;
        }

        try {

            usuarioSeleccionado.setNombreUsuario(
                    txtUsuario.getText().trim()
            );

            usuarioSeleccionado.setNombre(
                    txtNombre.getText().trim()
            );

            usuarioSeleccionado.setApellido(
                    txtApellido.getText().trim()
            );

            usuarioSeleccionado.setCorreo(
                    txtCorreo.getText().trim()
            );

            usuarioSeleccionado.setRol(
                    cbRol.getValue()
            );

            usuarioSeleccionado.setEstado(
                    "Activo".equals(cbEstado.getValue())
            );

            boolean actualizado = usuarioDAO.actualizar(
                    usuarioSeleccionado
            );

            if (actualizado) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Usuario actualizado",
                        "El usuario fue actualizado correctamente."
                );

                cargarUsuarios();
                limpiarFormulario();

            } else {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo actualizar el usuario."
                );
            }

        } catch (Exception e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Ocurrió un error al actualizar el usuario."
            );

            e.printStackTrace();
        }
    }


    @FXML
    private void eliminarUsuario() {

        if (usuarioSeleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccione un usuario",
                    "Debe seleccionar un usuario de la tabla."
            );

            return;
        }

        try {

            boolean eliminado = usuarioDAO.eliminar(
                    usuarioSeleccionado.getIdUsuario()
            );

            if (eliminado) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Usuario eliminado",
                        "El usuario fue eliminado correctamente."
                );

                cargarUsuarios();
                limpiarFormulario();

            } else {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo eliminar el usuario."
                );
            }

        } catch (Exception e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Ocurrió un error al eliminar el usuario."
            );

            e.printStackTrace();
        }
    }

    @FXML
    private void limpiarCampos() {

        limpiarFormulario();
    }

    private void limpiarFormulario() {

        txtUsuario.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtCorreo.clear();
        txtContrasena.clear();

        cbRol.getSelectionModel().clearSelection();
        cbEstado.getSelectionModel().clearSelection();

        tblUsuarios.getSelectionModel().clearSelection();

        usuarioSeleccionado = null;
    }

    private boolean validarCampos(boolean requiereContrasena) {

        if (txtUsuario.getText() == null
                || txtUsuario.getText().trim().isEmpty()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo requerido",
                    "Ingrese el nombre de usuario."
            );

            txtUsuario.requestFocus();

            return false;
        }

        if (txtNombre.getText() == null
                || txtNombre.getText().trim().isEmpty()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo requerido",
                    "Ingrese el nombre."
            );

            txtNombre.requestFocus();

            return false;
        }

        if (txtApellido.getText() == null
                || txtApellido.getText().trim().isEmpty()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo requerido",
                    "Ingrese el apellido."
            );

            txtApellido.requestFocus();

            return false;
        }

        if (txtCorreo.getText() == null
                || txtCorreo.getText().trim().isEmpty()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo requerido",
                    "Ingrese el correo."
            );

            txtCorreo.requestFocus();

            return false;
        }

        if (requiereContrasena
                && (txtContrasena.getText() == null
                || txtContrasena.getText().isEmpty())) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo requerido",
                    "Ingrese una contraseña."
            );

            txtContrasena.requestFocus();

            return false;
        }

        if (cbRol.getValue() == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo requerido",
                    "Seleccione un rol."
            );

            cbRol.requestFocus();

            return false;
        }

        if (cbEstado.getValue() == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo requerido",
                    "Seleccione un estado."
            );

            cbEstado.requestFocus();

            return false;
        }

        return true;
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

            stage.setScene(
                    new Scene(root)
            );

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