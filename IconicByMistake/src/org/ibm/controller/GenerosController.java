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

import org.ibm.dao.GeneroDAO;
import org.ibm.dao.impl.GeneroDAOImpl;
import org.ibm.model.Genero;

public class GenerosController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(GenerosController.class.getName());

    @FXML
    private TextField txtIdGenero;
    @FXML
    private TextField txtNombre;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<Genero> tablaGeneros;
    @FXML
    private TableColumn<Genero, Integer> colIdGenero;
    @FXML
    private TableColumn<Genero, String> colNombreGenero;
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
    private final GeneroDAO generoDAO = new GeneroDAOImpl();
    private final ObservableList<Genero> listaGeneros = FXCollections.observableArrayList();
    private final FilteredList<Genero> generosFiltrados = new FilteredList<>(listaGeneros, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarTabla();
        tablaGeneros.setItems(generosFiltrados);
        seleccionarFila();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colIdGenero.setCellValueFactory(new PropertyValueFactory<>("idGenero"));
        colNombreGenero.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    private void cargarTabla() {
        try {
            listaGeneros.setAll(generoDAO.listarTodos());
        } catch (Exception e) {
            mostrarError("Error al cargar la lista de géneros: " + e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarGeneros());
    }

    private void filtrarGeneros() {
        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        if (busqueda.isEmpty()) {
            generosFiltrados.setPredicate(p -> true);
        } else {
            generosFiltrados.setPredicate(genero ->
                    String.valueOf(genero.getIdGenero()).contains(busqueda)
                    || (genero.getNombre() != null && genero.getNombre().toLowerCase().contains(busqueda)));
        }
    }

    private void seleccionarFila() {
        tablaGeneros.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        if (txtIdGenero != null) {
                            txtIdGenero.setText(String.valueOf(newSelection.getIdGenero()));
                        }
                        txtNombre.setText(newSelection.getNombre());
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";

            if (nombre.isEmpty()) {
                mostrarAdvertencia("El nombre del género es obligatorio.");
                lblMensaje.setText("Ingrese el nombre del género.");
                return;
            }

            Genero genero = new Genero();
            genero.setNombre(nombre);

            if (modoEdicion && txtIdGenero != null && !txtIdGenero.getText().isEmpty()) {
                genero.setIdGenero(Integer.parseInt(txtIdGenero.getText().trim()));
            }

            boolean guardado;
            if (modoEdicion) {
                guardado = generoDAO.actualizar(genero);
            } else {
                guardado = generoDAO.crear(genero);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Género actualizado exitosamente."
                        : "Género registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar la información del género.");
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
    private void handleNuevo() {
        modoEdicion = false;
        limpiarFormulario();
        activarFormulario();
        desactivarNavegacion();
        tablaGeneros.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        txtNombre.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Genero seleccion = tablaGeneros.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un género de la tabla para editar.");
            return;
        }
        modoEdicion = true;
        activarFormulario();
        desactivarNavegacion();
        lblMensaje.setText("");
    }

    @FXML
    private void handlePrimero() {
        if (!tablaGeneros.getItems().isEmpty()) {
            tablaGeneros.getSelectionModel().selectFirst();
            tablaGeneros.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaGeneros.getItems().isEmpty()) {
            tablaGeneros.getSelectionModel().selectPrevious();
            int index = tablaGeneros.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                tablaGeneros.scrollTo(index);
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaGeneros.getItems().isEmpty()) {
            tablaGeneros.getSelectionModel().selectNext();
            int index = tablaGeneros.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                tablaGeneros.scrollTo(index);
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaGeneros.getItems().isEmpty()) {
            tablaGeneros.getSelectionModel().selectLast();
            tablaGeneros.scrollTo(tablaGeneros.getItems().size() - 1);
        }
    }

    @FXML
    public void handleVolverMenu(ActionEvent event) {
        LOGGER.info("Navegando de regreso al menú principal.");
        try {
            Stage escenarioPrincipal = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Ajusta la ruta de tu vista de menú según corresponda en tu proyecto
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/ibm/view/DashboardBodegaView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            escenarioPrincipal.setTitle("Menú Principal");
            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al intentar cargar la vista del menú principal", e);
            mostrarError("No se pudo cargar la vista del menú: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        if (txtIdGenero != null) {
            txtIdGenero.clear();
        }
        txtNombre.clear();
    }

    private void activarFormulario() {
        txtNombre.setDisable(false);
        if (btnGuardar != null) btnGuardar.setDisable(false);
        if (btnCancelar != null) btnCancelar.setDisable(false);
    }

    private void desactivarFormulario() {
        txtNombre.setDisable(true);
        if (btnGuardar != null) btnGuardar.setDisable(true);
        if (btnCancelar != null) btnCancelar.setDisable(true);
    }

    private void activarNavegacion() {
        tablaGeneros.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaGeneros.setDisable(true);
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