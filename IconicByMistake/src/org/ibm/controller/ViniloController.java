package org.ibm.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List; // 👈 Asegúrate de importar List
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors; // 👈 Asegúrate de importar Collectors
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.ibm.dao.ArtistaDAO;
import org.ibm.dao.GeneroDAO;
import org.ibm.dao.ProductorDAO;
import org.ibm.dao.ViniloDAO;
import org.ibm.dao.impl.ArtistaDAOImpl;
import org.ibm.dao.impl.GeneroDAOImpl;
import org.ibm.dao.impl.ProductorDAOImpl;
import org.ibm.dao.impl.ViniloDAOImpl;
import org.ibm.model.Artista;
import org.ibm.model.Genero;
import org.ibm.model.Vinilo;
import org.ibm.model.Productor;

public class ViniloController implements Initializable {
    private static final Logger log = Logger.getLogger(ViniloController.class.getName());

    @FXML private TextField txtCodigoVinilo;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtFechaLanzamiento; 
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private ComboBox<Artista> cmbArtista; 
    @FXML private ComboBox<Genero> cmbGenero;
    @FXML private ComboBox<Productor> cmbProductor;
    @FXML private Label lblMensaje;
    
    @FXML private TableView<Vinilo> tablaVinilos;
    @FXML private TableColumn<Vinilo, String> colCodigo;
    @FXML private TableColumn<Vinilo, String> colTitulo;
    @FXML private TableColumn<Vinilo, String> colFecha; 
    @FXML private TableColumn<Vinilo, Double> colPrecio;
    @FXML private TableColumn<Vinilo, Integer> colStock;
    @FXML private TableColumn<Vinilo, String> colArtista;
    @FXML private TableColumn<Vinilo, String> colGenero;
    @FXML private TableColumn<Vinilo, String> colProductor;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnPrimero;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnUltimo;
    @FXML private TextField txtBuscar;

    private static final String DIRECTORIO_FOTOS = "src/imagenes";
    @FXML private ImageView imgPortada;
    @FXML private Button btnCambiarFoto;
    private File archivoFotoSeleccionado = null;
    private String urlFotoActual = null;

    private boolean modoEdicion = false;
    private final ViniloDAO viniloDAO = new ViniloDAOImpl();
    private final ArtistaDAO artistaDAO = new ArtistaDAOImpl();
    private final GeneroDAO generoDAO = new GeneroDAOImpl();
    private final ProductorDAO productorDAO = new ProductorDAOImpl();
    private final ObservableList<Vinilo> listaVinilos = FXCollections.observableArrayList();
    private final FilteredList<Vinilo> vinilosFiltrados = new FilteredList<>(listaVinilos, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarTabla();
        cargarCombos();
        tablaVinilos.setItems(vinilosFiltrados);
        seleccionarFila();
        configurarBusqueda();
        
        // 🌟 Agregado: Lanza la alerta de stock crítico al iniciar
        verificarStockCritico();
    }

    // 🌟 NUEVO MÉTODO DE ALERTA PARA VINILO CONTROLLER
    private void verificarStockCritico() {
        try {
            List<Vinilo> criticos = viniloDAO.listarTodos().stream()
                    .filter(v -> v.getStock() <= 10)
                    .collect(Collectors.toList());

            if (!criticos.isEmpty()) {
                StringBuilder sb = new StringBuilder("Los siguientes vinilos tienen stock crítico (<= 10 unidades):\n");
                for (Vinilo v : criticos) {
                    sb.append("• ").append(v.getTitulo())
                      .append(" (SKU: ").append(v.getSku())
                      .append(") - Unidades: ").append(v.getStock()).append("\n");
                }

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alerta de Inventario");
                alert.setHeaderText(null);
                alert.setContentText(sb.toString());
                alert.showAndWait();
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "Error al verificar stock crítico de vinilos", e);
        }
    }

    public void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("sku")); 
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("anioLanzamiento")); 
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        colArtista.setCellValueFactory(cellData -> {
            Vinilo vinilo = cellData.getValue();
            if (vinilo != null && vinilo.getArtista() != null) {
                return new javafx.beans.property.SimpleStringProperty(vinilo.getArtista().getNombreArtistico());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colGenero.setCellValueFactory(cellData -> {
            Vinilo vinilo = cellData.getValue();
            if (vinilo != null && vinilo.getGenero() != null) {
                return new javafx.beans.property.SimpleStringProperty(vinilo.getGenero().getNombre());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colProductor.setCellValueFactory(cellData -> {
            Vinilo vinilo = cellData.getValue();
            if (vinilo != null && vinilo.getProductor() != null) {
                return new javafx.beans.property.SimpleStringProperty(vinilo.getProductor().getNombreProductor());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
    }

    private void cargarTabla() {
        try {
            listaVinilos.setAll(viniloDAO.listarTodos());
        } catch (Exception e) {
            mostrarError("Error al cargar la tabla de vinilos: " + e.getMessage());
        }
    }

    private void cargarCombos() {
        try {
            cmbArtista.setItems(FXCollections.observableArrayList(artistaDAO.listarTodos()));
            cmbGenero.setItems(FXCollections.observableArrayList(generoDAO.listarTodos()));
            cmbProductor.setItems(FXCollections.observableArrayList(productorDAO.listarTodos()));
        } catch (Exception e) {
            mostrarError("Error al cargar los catálogos: " + e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarVinilos());
    }

    private void filtrarVinilos() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            vinilosFiltrados.setPredicate(p -> true);
        } else {
            vinilosFiltrados.setPredicate(vinilo ->
                    (vinilo.getSku() != null && vinilo.getSku().toLowerCase().contains(busqueda))
                    || (vinilo.getTitulo() != null && vinilo.getTitulo().toLowerCase().contains(busqueda))
                    || String.valueOf(vinilo.getPrecio()).contains(busqueda)
                    || String.valueOf(vinilo.getStock()).contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaVinilos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtCodigoVinilo.setText(newSelection.getSku());
                        txtTitulo.setText(newSelection.getTitulo());
                        txtFechaLanzamiento.setText(newSelection.getAnioLanzamiento() != null ? newSelection.getAnioLanzamiento() : "");
                        txtPrecio.setText(String.valueOf(newSelection.getPrecio()));
                        txtStock.setText(String.valueOf(newSelection.getStock()));
                        
                        cmbArtista.setValue(null);
                        if (newSelection.getArtista() != null) {
                            for (Artista artista : cmbArtista.getItems()) {
                                if (artista.getIdArtista() == newSelection.getArtista().getIdArtista()) {
                                    cmbArtista.setValue(artista);
                                    break;
                                }
                            }
                        }

                        cmbGenero.setValue(null);
                        if (newSelection.getGenero() != null) {
                            for (Genero genero : cmbGenero.getItems()) {
                                if (genero.getIdGenero() == newSelection.getGenero().getIdGenero()) {
                                    cmbGenero.setValue(genero);
                                    break;
                                }
                            }
                        }

                        cmbProductor.setValue(null);
                        if (newSelection.getProductor() != null) {
                            for (Productor prod : cmbProductor.getItems()) {
                                if (prod.getIdProductor().equals(newSelection.getProductor().getIdProductor())) {
                                    cmbProductor.setValue(prod);
                                    break;
                                }
                            }
                        }

                        urlFotoActual = newSelection.getUrlFoto();
                        archivoFotoSeleccionado = null;
                        if (urlFotoActual != null && !urlFotoActual.isEmpty()) {
                            File foto = new File("src", urlFotoActual);
                            if (foto.exists()) {
                                imgPortada.setImage(new Image(foto.toURI().toString()));
                            } else {
                                imgPortada.setImage(null);
                            }
                        } else {
                            imgPortada.setImage(null);
                        }
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleCambiarFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar portada del vinilo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (*.jpg, *.jpeg, *.png, *.gif, *.bmp)",
                        "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*"));
        File elegido = fileChooser.showOpenDialog(btnCambiarFoto.getScene().getWindow());
        if (elegido != null) {
            archivoFotoSeleccionado = elegido;
            imgPortada.setImage(new Image(elegido.toURI().toString()));
        }
    }

    @FXML
    private void handleGuardar() {
        try {
            if (txtCodigoVinilo.getText().trim().isEmpty() || txtTitulo.getText().trim().isEmpty()) {
                mostrarAdvertencia("Los campos Código y Título son obligatorios.");
                return;
            }

            String codigo = txtCodigoVinilo.getText().trim();
            String urlFoto = urlFotoActual;
            
            if (archivoFotoSeleccionado != null) {
                String nombreOriginal = archivoFotoSeleccionado.getName();
                String extension = nombreOriginal.contains(".")
                        ? nombreOriginal.substring(nombreOriginal.lastIndexOf('.') + 1)
                        : "jpg";
                String nombreFoto = codigo.replaceAll("[^a-zA-Z0-9]", "_") + "." + extension;
                File destino = new File(DIRECTORIO_FOTOS, nombreFoto);
                Files.createDirectories(destino.getParentFile().toPath());
                Files.copy(archivoFotoSeleccionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                urlFoto = "imagenes/" + nombreFoto;
            }

            Vinilo vinilo = new Vinilo();
            vinilo.setSku(codigo);
            vinilo.setTitulo(txtTitulo.getText().trim());
            vinilo.setAnioLanzamiento(txtFechaLanzamiento.getText().trim()); 
            vinilo.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            vinilo.setStock(Integer.parseInt(txtStock.getText().trim()));
            vinilo.setArtista(cmbArtista.getValue());
            vinilo.setGenero(cmbGenero.getValue());
            vinilo.setProductor(cmbProductor.getValue());
            vinilo.setUrlFoto(urlFoto);

            boolean guardado;
            if (modoEdicion) {
                guardado = viniloDAO.actualizar(vinilo);
            } else {
                guardado = viniloDAO.crear(vinilo);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion
                        ? "Vinilo actualizado exitosamente."
                        : "Vinilo registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar el vinilo.");
            }
        } catch (NumberFormatException e) {
            mostrarAdvertencia("Verifique que los campos numéricos (precio, stock) sean válidos.");
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleVolver(ActionEvent evento) {
        try {
            Stage escenarioPrincipal = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/ibm/view/DashboardBodegaView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            escenarioPrincipal.setTitle("Iconic By Mistake - Dashboard Bodega");
            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.show();
        } catch (Exception e) {
            log.log(Level.WARNING, "Error al volver al dashboard", e);
            mostrarError("Error al volver al menú: " + e.getMessage());
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
        tablaVinilos.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        txtCodigoVinilo.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Vinilo seleccion = tablaVinilos.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un vinilo de la tabla para editar.");
            return;
        }
        modoEdicion = true;
        activarFormulario();
        txtCodigoVinilo.setDisable(true); 
        desactivarNavegacion();
        lblMensaje.setText("");
    }

    @FXML private void handlePrimero() { if (!tablaVinilos.getItems().isEmpty()) { tablaVinilos.getSelectionModel().selectFirst(); tablaVinilos.scrollTo(0); } }
    @FXML private void handleAnterior() { if (!tablaVinilos.getItems().isEmpty()) { tablaVinilos.getSelectionModel().selectPrevious(); int index = tablaVinilos.getSelectionModel().getSelectedIndex(); if (index >= 0) tablaVinilos.scrollTo(index); } }
    @FXML private void handleSiguiente() { if (!tablaVinilos.getItems().isEmpty()) { tablaVinilos.getSelectionModel().selectNext(); int index = tablaVinilos.getSelectionModel().getSelectedIndex(); if (index >= 0) tablaVinilos.scrollTo(index); } }
    @FXML private void handleUltimo() { if (!tablaVinilos.getItems().isEmpty()) { tablaVinilos.getSelectionModel().selectLast(); tablaVinilos.scrollTo(tablaVinilos.getItems().size() - 1); } }

    private void limpiarFormulario() {
        txtCodigoVinilo.clear();
        txtTitulo.clear();
        txtFechaLanzamiento.clear();
        txtPrecio.clear();
        txtStock.clear();
        cmbArtista.setValue(null);
        cmbGenero.setValue(null);
        cmbProductor.setValue(null);
        imgPortada.setImage(null);
        archivoFotoSeleccionado = null;
        urlFotoActual = null;
    }

    private void activarFormulario() {
        txtCodigoVinilo.setDisable(false);
        txtTitulo.setDisable(false);
        txtFechaLanzamiento.setDisable(false);
        txtPrecio.setDisable(false);
        txtStock.setDisable(false);
        cmbArtista.setDisable(false);
        cmbGenero.setDisable(false);
        cmbProductor.setDisable(false);
        btnCambiarFoto.setDisable(false);
    }

    private void desactivarFormulario() {
        txtCodigoVinilo.setDisable(true);
        txtTitulo.setDisable(true);
        txtFechaLanzamiento.setDisable(true);
        txtPrecio.setDisable(true);
        txtStock.setDisable(true);
        cmbArtista.setDisable(true);
        cmbGenero.setDisable(true);
        cmbProductor.setDisable(true);
        btnCambiarFoto.setDisable(true);
    }

    private void activarNavegacion() {
        tablaVinilos.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaVinilos.setDisable(true);
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