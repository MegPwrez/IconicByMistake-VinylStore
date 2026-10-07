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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.ibm.dao.ArtistaDAO;
import org.ibm.dao.impl.ArtistaDAOImpl;
import org.ibm.model.Artista;

public class ArtistasController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(ArtistasController.class.getName());
    
    @FXML
    private TextField txtNombreArtistico;
    @FXML
    private TextField txtNacionalidad;
    @FXML
    private TextArea txtBiografia;
    @FXML
    private Label lblMensaje;
    @FXML
    private TableView<Artista> tablaArtistas;
    @FXML
    private TableColumn<Artista, Integer> colIdArtista;
    @FXML
    private TableColumn<Artista, String> colNombreArtistico;
    @FXML
    private TableColumn<Artista, String> colNacionalidad;
    @FXML
    private TableColumn<Artista, String> colBiografia;
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
    private Artista enEdicion;
    private final ArtistaDAO artistaDAO = new ArtistaDAOImpl();
    private final ObservableList<Artista> listaArtistas = FXCollections.observableArrayList();
    private final FilteredList<Artista> artistasFiltrados = new FilteredList<>(listaArtistas, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTabla();
        tablaArtistas.setItems(artistasFiltrados);
        seleccionarFila();
        configurarTabla();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colIdArtista.setCellValueFactory(new PropertyValueFactory<>("idArtista"));
        colNombreArtistico.setCellValueFactory(new PropertyValueFactory<>("nombreArtistico"));
        colNacionalidad.setCellValueFactory(new PropertyValueFactory<>("nacionalidad"));
        colBiografia.setCellValueFactory(new PropertyValueFactory<>("biografia"));
    }

    private void cargarTabla() {
        try {
            listaArtistas.setAll(artistaDAO.listarTodos());
        } catch (Exception e) {
            mostrarError("Error al cargar la lista de artistas: " + e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarArtistas());
    }

    private void filtrarArtistas() {
        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        if (busqueda.isEmpty()) {
            artistasFiltrados.setPredicate(p -> true);
        } else {
            artistasFiltrados.setPredicate(artista ->
                    String.valueOf(artista.getIdArtista()).contains(busqueda)
                    || (artista.getNombreArtistico() != null && artista.getNombreArtistico().toLowerCase().contains(busqueda))
                    || (artista.getNacionalidad() != null && artista.getNacionalidad().toLowerCase().contains(busqueda))
                    || (artista.getBiografia() != null && artista.getBiografia().toLowerCase().contains(busqueda)));
        }
    }

    private void seleccionarFila() {
        tablaArtistas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtNombreArtistico.setText(newSelection.getNombreArtistico());
                        txtNacionalidad.setText(newSelection.getNacionalidad());
                        txtBiografia.setText(newSelection.getBiografia());
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            if (txtNombreArtistico.getText().trim().isEmpty() 
                    || txtNacionalidad.getText().trim().isEmpty()) {
                mostrarAdvertencia("Los campos Nombre Artístico y Nacionalidad son obligatorios.");
                lblMensaje.setText("Por favor complete los campos requeridos.");
                return;
            }

            Artista artista = new Artista(
                    modoEdicion ? enEdicion.getIdArtista() : 0,
                    txtNombreArtistico.getText().trim(),
                    txtNacionalidad.getText().trim(),
                    txtBiografia.getText().trim());

            boolean guardado;
            if (modoEdicion) {
                guardado = artistaDAO.actualizar(artista);
            } else {
                guardado = artistaDAO.crear(artista);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Artista actualizado exitosamente."
                        : "Artista registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar la información del artista (verifique si los métodos DAO están implementados).");
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
        enEdicion = null;
        lblMensaje.setText("");
    }

    @FXML
    private void handleNuevo() {
        modoEdicion = false;
        enEdicion = null;
        limpiarFormulario();
        activarFormulario();
        desactivarNavegacion();
        tablaArtistas.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        txtNombreArtistico.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Artista seleccion = tablaArtistas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un artista de la tabla para editar.");
            return;
        }
        modoEdicion = true;
        enEdicion = seleccion;
        activarFormulario();
        desactivarNavegacion();
        lblMensaje.setText("");
    }

    @FXML
    private void handlePrimero() {
        if (!tablaArtistas.getItems().isEmpty()) {
            tablaArtistas.getSelectionModel().selectFirst();
            tablaArtistas.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaArtistas.getItems().isEmpty()) {
            tablaArtistas.getSelectionModel().selectPrevious();
            if (tablaArtistas.getSelectionModel().getSelectedIndex() >= 0) {
                tablaArtistas.scrollTo(tablaArtistas.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaArtistas.getItems().isEmpty()) {
            tablaArtistas.getSelectionModel().selectNext();
            if (tablaArtistas.getSelectionModel().getSelectedIndex() >= 0) {
                tablaArtistas.scrollTo(tablaArtistas.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaArtistas.getItems().isEmpty()) {
            tablaArtistas.getSelectionModel().selectLast();
            tablaArtistas.scrollTo(tablaArtistas.getItems().size() - 1);
        }
    }

    @FXML
    public void handleVolverMenu(ActionEvent event) {
        LOGGER.info("Navegando de regreso al menú principal.");
        try {
            Stage escenarioPrincipal = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Cambia la ruta ayan iti pagturongam ti Menu/Dashboardyo
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/ibm/view/DashboardBodegaView.fxml"));
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
        txtNombreArtistico.clear();
        txtNacionalidad.clear();
        txtBiografia.clear();
    }

    private void activarFormulario() {
        txtNombreArtistico.setDisable(false);
        txtNacionalidad.setDisable(false);
        txtBiografia.setDisable(false);
        btnGuardar.setDisable(false);
        btnCancelar.setDisable(false);
    }

    private void desactivarFormulario() {
        txtNombreArtistico.setDisable(true);
        txtNacionalidad.setDisable(true);
        txtBiografia.setDisable(true);
        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);
    }

    private void activarNavegacion() {
        tablaArtistas.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaArtistas.setDisable(true);
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