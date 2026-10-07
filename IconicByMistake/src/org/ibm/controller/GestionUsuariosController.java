package org.ibm.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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

        cbRol.setItems(FXCollections.observableArrayList(
                "admin",
                "bodega",
                "cajero"
        ));

        cbEstado.setItems(FXCollections.observableArrayList(
                "Activo",
                "Inactivo"
        ));

        configurarTabla();
        cargarUsuarios();

        tblUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        cargarUsuarioSeleccionado(seleccionado);
                    }
                }
        );
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void cargarUsuarios() {
        listaUsuarios = FXCollections.observableArrayList(
                usuarioDAO.listarTodos()
        );

        tblUsuarios.setItems(listaUsuarios);
    }

    private void cargarUsuarioSeleccionado(Usuario usuario) {
        usuarioSeleccionado = usuario;

        txtUsuario.setText(usuario.getNombreUsuario());
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtCorreo.setText(usuario.getCorreo());
        txtContrasena.clear();

        cbRol.setValue(usuario.getRol());

        if (usuario.isEstado()) {
            cbEstado.setValue("Activo");
        } else {
            cbEstado.setValue("Inactivo");
        }
    }

    @FXML
    private void guardarUsuario() {
        if (!validarCampos(true)) {
            return;
        }

        Usuario usuario = new Usuario();

        usuario.setNombreUsuario(txtUsuario.getText().trim());
        usuario.setNombre(txtNombre.getText().trim());
        usuario.setApellido(txtApellido.getText().trim());
        usuario.setCorreo(txtCorreo.getText().trim());
        usuario.setContraseña(txtContrasena.getText());
        usuario.setRol(cbRol.getValue());
        usuario.setEstado(cbEstado.getValue().equals("Activo"));

        if (usuarioDAO.insertar(usuario)) {
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Usuario registrado",
                    "El usuario fue registrado correctamente."
            );

            cargarUsuarios();
            limpiarCampos();
        } else {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo registrar el usuario."
            );
        }
    }

    @FXML
    private void actualizarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Usuario no seleccionado",
                    "Seleccione un usuario para actualizar."
            );
            return;
        }

        if (!validarCampos(false)) {
            return;
        }

        usuarioSeleccionado.setNombreUsuario(txtUsuario.getText().trim());
        usuarioSeleccionado.setNombre(txtNombre.getText().trim());
        usuarioSeleccionado.setApellido(txtApellido.getText().trim());
        usuarioSeleccionado.setCorreo(txtCorreo.getText().trim());
        usuarioSeleccionado.setRol(cbRol.getValue());
        usuarioSeleccionado.setEstado(cbEstado.getValue().equals("Activo"));

        if (usuarioDAO.actualizar(usuarioSeleccionado)) {
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Usuario actualizado",
                    "El usuario fue actualizado correctamente."
            );

            cargarUsuarios();
            limpiarCampos();
        } else {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo actualizar el usuario."
            );
        }
    }

    @FXML
    private void eliminarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Usuario no seleccionado",
                    "Seleccione un usuario para eliminar."
            );
            return;
        }

        if (usuarioDAO.eliminar(usuarioSeleccionado.getIdUsuario())) {
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Usuario eliminado",
                    "El usuario fue eliminado correctamente."
            );

            cargarUsuarios();
            limpiarCampos();
        } else {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo eliminar el usuario."
            );
        }
    }

    @FXML
    private void limpiarCampos() {
        usuarioSeleccionado = null;

        txtUsuario.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtCorreo.clear();
        txtContrasena.clear();

        cbRol.setValue(null);
        cbEstado.setValue(null);

        tblUsuarios.getSelectionModel().clearSelection();
    }

    private boolean validarCampos(boolean validarContrasena) {
        if (txtUsuario.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()
                || txtApellido.getText().trim().isEmpty()
                || txtCorreo.getText().trim().isEmpty()
                || cbRol.getValue() == null
                || cbEstado.getValue() == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campos incompletos",
                    "Complete todos los campos obligatorios."
            );

            return false;
        }

        if (validarContrasena && txtContrasena.getText().isEmpty()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Contraseña requerida",
                    "Ingrese una contraseña para el usuario."
            );

            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}