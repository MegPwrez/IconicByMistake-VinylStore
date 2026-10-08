package org.ibm.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.ibm.dao.ClienteDAO;
import org.ibm.dao.impl.ClienteDAOImpl;
import org.ibm.model.Cliente;


public class ClienteController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ClienteController.class.getName());

    @FXML
    private TextField txtCui;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCorreo;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, Long> colCUI;
    @FXML
    private TableColumn<Cliente, String> colNombreCliente;
    @FXML
    private TableColumn<Cliente, String> colApellidoCliente;
    @FXML
    private TableColumn<Cliente, String> colCorreoElectronico;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnPrimero;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;
    @FXML
    private Button btnUltimo;
    @FXML
    private TextField txtBuscar;

    private boolean modoEdicion = false;
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private final FilteredList<Cliente> clientesFiltrados = new FilteredList<>(listaClientes, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarTabla();
        tablaClientes.setItems(clientesFiltrados);
        seleccionarFila();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colCUI.setCellValueFactory(new PropertyValueFactory<>("cui"));
        colNombreCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colApellidoCliente.setCellValueFactory(new PropertyValueFactory<>("apellidoCliente"));
        colCorreoElectronico.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
    }

    private void cargarTabla() {
        try {
            listaClientes.setAll(clienteDAO.listar());
        } catch (Exception e) {
            mostrarError("Error al cargar la lista de clientes: " + e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarClientes());
    }

    private void filtrarClientes() {
        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        if (busqueda.isEmpty()) {
            clientesFiltrados.setPredicate(p -> true);
        } else {
            clientesFiltrados.setPredicate(cliente ->
                    String.valueOf(cliente.getCui()).contains(busqueda)
                    || (cliente.getNombreCliente() != null && cliente.getNombreCliente().toLowerCase().contains(busqueda))
                    || (cliente.getApellidoCliente() != null && cliente.getApellidoCliente().toLowerCase().contains(busqueda))
                    || (cliente.getCorreoElectronico() != null && cliente.getCorreoElectronico().toLowerCase().contains(busqueda)));
        }
    }

    private void seleccionarFila() {
        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtCui.setText(String.valueOf(newSelection.getCui()));
                        txtNombre.setText(newSelection.getNombreCliente());
                        txtApellido.setText(newSelection.getApellidoCliente());
                        txtCorreo.setText(newSelection.getCorreoElectronico());
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            String cuiTexto = txtCui.getText() != null ? txtCui.getText().trim() : "";
            String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
            String apellido = txtApellido.getText() != null ? txtApellido.getText().trim() : "";
            String correo = txtCorreo.getText() != null ? txtCorreo.getText().trim() : "";

            if (cuiTexto.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty()) {
                mostrarAdvertencia("Todos los campos son obligatorios.");
                lblMensaje.setText("Complete los campos obligatorios.");
                return;
            }

            if (!cuiTexto.matches("\\d+")) {
                mostrarAdvertencia("El CUI debe contener únicamente números.");
                return;
            }

            if (cuiTexto.length() != 13) {
                mostrarAdvertencia("El CUI debe tener exactamente 13 dígitos.");
                return;
            }

            if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                mostrarAdvertencia("El correo electrónico no tiene un formato válido.");
                return;
            }

            Cliente cliente = new Cliente();
            cliente.setCui(Long.parseLong(cuiTexto));
            cliente.setNombreCliente(nombre);
            cliente.setApellidoCliente(apellido);
            cliente.setCorreoElectronico(correo);

            boolean guardado;
            if (modoEdicion) {
                guardado = clienteDAO.actualizar(cliente);
            } else {
                guardado = clienteDAO.crear(cliente);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Cliente actualizado exitosamente."
                        : "Cliente registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar la información del cliente.");
            }
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        limpiarFormulario();
        desactivarFormulario();
        activarNavegacion();
        modoEdicion = false;
        lblMensaje.setText("");
    }

    @FXML
    private void handleNuevoCliente() {
        modoEdicion = false;
        limpiarFormulario();
        activarFormulario();
        desactivarNavegacion();
        tablaClientes.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        txtCui.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Cliente seleccion = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un cliente de la tabla para editar.");
            return;
        }
        modoEdicion = true;
        activarFormulario();
        txtCui.setDisable(true);
        desactivarNavegacion();
        lblMensaje.setText("");
    }

    @FXML
    private void handlePrimero() {
        if (!tablaClientes.getItems().isEmpty()) {
            tablaClientes.getSelectionModel().selectFirst();
            tablaClientes.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaClientes.getItems().isEmpty()) {
            tablaClientes.getSelectionModel().selectPrevious();
            if (tablaClientes.getSelectionModel().getSelectedIndex() >= 0) {
                tablaClientes.scrollTo(tablaClientes.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaClientes.getItems().isEmpty()) {
            tablaClientes.getSelectionModel().selectNext();
            if (tablaClientes.getSelectionModel().getSelectedIndex() >= 0) {
                tablaClientes.scrollTo(tablaClientes.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaClientes.getItems().isEmpty()) {
            tablaClientes.getSelectionModel().selectLast();
            tablaClientes.scrollTo(tablaClientes.getItems().size() - 1);
        }
    }

    @FXML
    public void handleVolverMenu(ActionEvent event) {
        LOGGER.info("Navegando de regreso al menú principal.");
        try {
            Stage escenarioPrincipal = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/ibm/view/CajeroDashboardView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            escenarioPrincipal.setTitle("Librería Saturno - Menú");
            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al intentar cargar la vista del menú principal", e);
            mostrarError("No se pudo cargar la vista del menú: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtCui.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtCorreo.clear();
    }

    private void activarFormulario() {
        txtCui.setDisable(false);
        txtNombre.setDisable(false);
        txtApellido.setDisable(false);
        txtCorreo.setDisable(false);
        if (btnGuardar != null) btnGuardar.setDisable(false);
        if (btnCancelar != null) btnCancelar.setDisable(false);
    }

    private void desactivarFormulario() {
        txtCui.setDisable(true);
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        txtCorreo.setDisable(true);
        if (btnGuardar != null) btnGuardar.setDisable(true);
        if (btnCancelar != null) btnCancelar.setDisable(true);
    }

    private void activarNavegacion() {
        tablaClientes.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaClientes.setDisable(true);
        btnNuevo.setDisable(true);
        btnEditar.setDisable(true);
        btnPrimero.setDisable(true);
        btnAnterior.setDisable(true);
        btnSiguiente.setDisable(true);
        btnUltimo.setDisable(true);
        txtBuscar.setDisable(true);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}