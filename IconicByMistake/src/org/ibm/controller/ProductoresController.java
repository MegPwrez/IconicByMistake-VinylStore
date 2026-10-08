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

import org.ibm.dao.ProductorDAO;
import org.ibm.dao.impl.ProductorDAOImpl;
import org.ibm.model.Productor;

public class ProductoresController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ProductoresController.class.getName());

    @FXML private TextField txtIdProductor;
    @FXML private TextField txtNombreProductor;
    @FXML private TextField txtSelloDiscografico;
    @FXML private Label lblMensaje;
    @FXML private TextField txtBuscar;
    
    @FXML private TableView<Productor> tablaProductores;
    @FXML private TableColumn<Productor, String> colIdProductor;
    @FXML private TableColumn<Productor, String> colNombreProductor;
    @FXML private TableColumn<Productor, String> colSelloDiscografico;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnPrimero;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnUltimo;

    private boolean modoEdicion = false;
    private final ProductorDAO productorDAO = new ProductorDAOImpl();
    private final ObservableList<Productor> listaProductores = FXCollections.observableArrayList();
    private final FilteredList<Productor> productoresFiltrados = new FilteredList<>(listaProductores, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarTabla();
        tablaProductores.setItems(productoresFiltrados);
        seleccionarFila();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colIdProductor.setCellValueFactory(new PropertyValueFactory<>("idProductor"));
        colNombreProductor.setCellValueFactory(new PropertyValueFactory<>("nombreProductor"));
        colSelloDiscografico.setCellValueFactory(new PropertyValueFactory<>("selloDiscografico"));
    }

    private void cargarTabla() {
        try {
            listaProductores.setAll(productorDAO.listarTodos());
        } catch (Exception e) {
            mostrarError("Error al cargar la tabla: " + e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarProductores());
    }

    private void filtrarProductores() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            productoresFiltrados.setPredicate(p -> true);
        } else {
            productoresFiltrados.setPredicate(productor ->
                    productor.getIdProductor().toLowerCase().contains(busqueda)
                    || productor.getNombreProductor().toLowerCase().contains(busqueda)
                    || productor.getSelloDiscografico().toLowerCase().contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaProductores.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtIdProductor.setText(newSelection.getIdProductor());
                        txtNombreProductor.setText(newSelection.getNombreProductor());
                        txtSelloDiscografico.setText(newSelection.getSelloDiscografico());
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            if (txtIdProductor.getText().trim().isEmpty() || 
                txtNombreProductor.getText().trim().isEmpty() || 
                txtSelloDiscografico.getText().trim().isEmpty()) {
                
                mostrarAdvertencia("Por favor, llene todos los campos obligatorios.");
                lblMensaje.setText("Campos incompletos.");
                return;
            }

            Productor productor = new Productor();
            productor.setIdProductor(txtIdProductor.getText().trim());
            productor.setNombreProductor(txtNombreProductor.getText().trim());
            productor.setSelloDiscografico(txtSelloDiscografico.getText().trim());

            boolean guardado;
            if (modoEdicion) {
                guardado = productorDAO.actualizar(productor);
            } else {
                guardado = productorDAO.crear(productor);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion ? "Productor actualizado exitosamente." : "Productor registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar el productor en la base de datos.");
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
        tablaProductores.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleNuevo() {
        modoEdicion = false;
        limpiarFormulario();
        activarFormulario();
        desactivarNavegacion();
        tablaProductores.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        txtIdProductor.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Productor seleccion = tablaProductores.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un productor de la tabla para editar.");
            return;
        }
        modoEdicion = true;
        activarFormulario();
        desactivarNavegacion();
        txtIdProductor.setDisable(true); 
        lblMensaje.setText("");
    }

    @FXML
    private void handlePrimero() {
        if (!tablaProductores.getItems().isEmpty()) {
            tablaProductores.getSelectionModel().selectFirst();
            tablaProductores.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaProductores.getItems().isEmpty()) {
            tablaProductores.getSelectionModel().selectPrevious();
            if (tablaProductores.getSelectionModel().getSelectedIndex() >= 0) {
                tablaProductores.scrollTo(tablaProductores.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaProductores.getItems().isEmpty()) {
            tablaProductores.getSelectionModel().selectNext();
            if (tablaProductores.getSelectionModel().getSelectedIndex() >= 0) {
                tablaProductores.scrollTo(tablaProductores.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaProductores.getItems().isEmpty()) {
            tablaProductores.getSelectionModel().selectLast();
            tablaProductores.scrollTo(tablaProductores.getItems().size() - 1);
        }
    }

    @FXML
    public void handleVolverMenu(ActionEvent event) {
        LOGGER.info("Navegando de regreso al menú principal.");
        try {
            Stage escenarioPrincipal = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
        txtIdProductor.clear();
        txtNombreProductor.clear();
        txtSelloDiscografico.clear();
    }

    private void activarFormulario() {
        txtIdProductor.setDisable(false);
        txtNombreProductor.setDisable(false);
        txtSelloDiscografico.setDisable(false);
        if (btnGuardar != null) btnGuardar.setDisable(false);
        if (btnCancelar != null) btnCancelar.setDisable(false);
    }

    private void desactivarFormulario() {
        txtIdProductor.setDisable(true);
        txtNombreProductor.setDisable(true);
        txtSelloDiscografico.setDisable(true);
        if (btnGuardar != null) btnGuardar.setDisable(true);
        if (btnCancelar != null) btnCancelar.setDisable(true);
    }

    private void activarNavegacion() {
        tablaProductores.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaProductores.setDisable(true);
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