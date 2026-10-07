package org.ibm.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.ibm.Main;
import org.ibm.dao.DetalleVentaDAO;
import org.ibm.dao.VentaDAO;
import org.ibm.dao.ViniloDAO;
import org.ibm.dao.impl.DetalleVentaImpl;
import org.ibm.dao.impl.VentaDAOImpl;
import org.ibm.dao.impl.ViniloDAOImpl;
import org.ibm.exception.DaoException;
import org.ibm.exception.ValidacionException;
import org.ibm.model.DetalleVenta;
import org.ibm.model.Venta;
import org.ibm.model.Vinilo;

public class DetalleVentaController implements Initializable {

    @FXML private ComboBox<Venta> cmbVenta;
    @FXML private ComboBox<Vinilo> cmbVinilo;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecio;
    @FXML private Label lblMensaje;
    @FXML private TableView<DetalleVenta> tablaDetalleVenta;

    @FXML private TableColumn<DetalleVenta, Integer> colIdDetalleVenta;
    @FXML private TableColumn<DetalleVenta, Integer> colNoVenta;
    @FXML private TableColumn<DetalleVenta, String> colcodigoBarras;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleVenta, Double> colPrecio;

    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnPrimero;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnUltimo;
    @FXML private TextField txtBuscar;

    private boolean Editar = false;
    private DetalleVenta Editando;

    private final DetalleVentaDAO detalleVentaDAO = new DetalleVentaImpl();
    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final ViniloDAO viniloDAO = new ViniloDAOImpl();

    private final ObservableList<DetalleVenta> listaDetalles = FXCollections.observableArrayList();
    private final FilteredList<DetalleVenta> detallesFiltrados = new FilteredList<>(listaDetalles, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarTabla();
        cargarCombos();
        tablaDetalleVenta.setItems(detallesFiltrados);
        seleccionarFila();
        configurarBusqueda();
        configurarSeleccionVinilo();
    }

    public void configurarTabla() {
        colIdDetalleVenta.setCellValueFactory(new PropertyValueFactory<>("idDetalleventa"));
        colNoVenta.setCellValueFactory(new PropertyValueFactory<>("noVenta"));
        colcodigoBarras.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
    }

    private void cargarTabla() {
        try {
            listaDetalles.setAll(detalleVentaDAO.listar());
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al cargar la tabla de detalles: " + e.getMessage());
        }
    }

    private void cargarCombos() {
        try {
            cmbVenta.setItems(FXCollections.observableArrayList(ventaDAO.listarTodos()));
            cmbVenta.setConverter(new StringConverter<Venta>() {
                @Override
                public String toString(Venta venta) {
                    return venta == null ? "" : "Venta #" + venta.getIdVenta();
                }

                @Override
                public Venta fromString(String string) {
                    return null;
                }
            });

            cmbVinilo.setItems(FXCollections.observableArrayList(viniloDAO.listarTodos()));
            cmbVinilo.setConverter(new StringConverter<Vinilo>() {
                @Override
                public String toString(Vinilo vinilo) {
                    return vinilo == null ? "" : vinilo.getCodigoBarras() + " - " + vinilo.getTituloAlbum();
                }

                @Override
                public Vinilo fromString(String string) {
                    return null;
                }
            });
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al cargar combos: " + e.getMessage());
        }
    }

    private void configurarSeleccionVinilo() {
        cmbVinilo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !Editar) {
                txtPrecio.setText(String.valueOf(newVal.getPrecio()));
            }
        });
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarDetalles());
    }

    private void filtrarDetalles() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            detallesFiltrados.setPredicate(p -> true);
        } else {
            detallesFiltrados.setPredicate(detalle ->
                    String.valueOf(detalle.getIdDetalleventa()).contains(busqueda)
                    || String.valueOf(detalle.getNoVenta()).contains(busqueda)
                    || (detalle.getCodigoBarras() != null && detalle.getCodigoBarras().toLowerCase().contains(busqueda))
                    || String.valueOf(detalle.getCantidad()).contains(busqueda)
                    || String.valueOf(detalle.getPrecioUnitario()).contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaDetalleVenta.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        cmbVenta.setValue(null);
                        for (Venta venta : cmbVenta.getItems()) {
                            if (venta.getIdVenta() == newSelection.getNoVenta()) {
                                cmbVenta.setValue(venta);
                                break;
                            }
                        }

                        cmbVinilo.setValue(null);
                        for (Vinilo vinilo : cmbVinilo.getItems()) {
                            if (vinilo.getCodigoBarras().equals(newSelection.getCodigoBarras())) {
                                cmbVinilo.setValue(vinilo);
                                break;
                            }
                        }

                        txtCantidad.setText(String.valueOf(newSelection.getCantidad()));
                        txtPrecio.setText(String.valueOf(newSelection.getPrecioUnitario()));
                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            ValidacionException.validarNoNulo(cmbVenta.getValue(), "Seleccione una venta.");
            ValidacionException.validarNoNulo(cmbVinilo.getValue(), "Seleccione un vinilo.");
            ValidacionException.validarNoVacio(txtCantidad.getText(), "cantidad");
            ValidacionException.validarPositivo(txtCantidad.getText(), "cantidad");
            ValidacionException.validarNoVacio(txtPrecio.getText(), "precio");
            ValidacionException.validarDecimal(txtPrecio.getText(), "precio");

            DetalleVenta detalle = new DetalleVenta(
                    Editar ? Editando.getIdDetalleventa() : 0,
                    cmbVenta.getValue().getIdVenta(),
                    cmbVinilo.getValue().getCodigoBarras(),
                    cmbVinilo.getValue().getTituloAlbum(),
                    Integer.parseInt(txtCantidad.getText().trim()),
                    Double.parseDouble(txtPrecio.getText().trim())
            );

            boolean guardado;
            if (Editar) {
                guardado = detalleVentaDAO.actualizar(detalle);
            } else {
                guardado = detalleVentaDAO.insertar(detalle);
            }

            if (guardado) {
                lblMensaje.setText(Editar
                        ? "Detalle de venta actualizado exitosamente."
                        : "Detalle de venta registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                Editar = false;
            } else {
                mostrarError("No se pudo guardar el detalle de venta.");
            }
        } catch (ValidacionException e) {
            mostrarAdvertencia(e.getMessage()); 
            lblMensaje.setText(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        limpiarFormulario();
        desactivarFormulario();
        activarNavegacion();
        Editar = false;
        Editando = null;
        lblMensaje.setText("");
    }

    @FXML
    private void handleNuevo() {
        Editar = false;
        Editando = null;
        limpiarFormulario();
        activarFormulario();
        desactivarNavegacion();
        tablaDetalleVenta.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        cmbVenta.requestFocus();
    }

    @FXML
    private void handleEditar() {
        DetalleVenta seleccion = tablaDetalleVenta.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione un detalle de venta de la tabla para editar.");
            return;
        }
        Editar = true;
        Editando = seleccion;
        activarFormulario();
        desactivarNavegacion();
        lblMensaje.setText("");
    }

    @FXML
    private void handlePrimero() {
        if (!tablaDetalleVenta.getItems().isEmpty()) {
            tablaDetalleVenta.getSelectionModel().selectFirst();
            tablaDetalleVenta.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaDetalleVenta.getItems().isEmpty()) {
            tablaDetalleVenta.getSelectionModel().selectPrevious();
            if (tablaDetalleVenta.getSelectionModel().getSelectedIndex() >= 0) {
                tablaDetalleVenta.scrollTo(tablaDetalleVenta.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaDetalleVenta.getItems().isEmpty()) {
            tablaDetalleVenta.getSelectionModel().selectNext();
            if (tablaDetalleVenta.getSelectionModel().getSelectedIndex() >= 0) {
                tablaDetalleVenta.scrollTo(tablaDetalleVenta.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaDetalleVenta.getItems().isEmpty()) {
            tablaDetalleVenta.getSelectionModel().selectLast();
            tablaDetalleVenta.scrollTo(tablaDetalleVenta.getItems().size() - 1);
        }
    }

    @FXML
    private void handleVolver() {
        try {
            Main.cambiarVista("/org/ibm/view/CajeroDashboardView.fxml");
        } catch (Exception e) {
            mostrarError("Error al volver al menú: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        cmbVenta.setValue(null);
        cmbVinilo.setValue(null);
        txtCantidad.clear();
        txtPrecio.clear();
    }

    private void activarFormulario() {
        cmbVenta.setDisable(false);
        cmbVinilo.setDisable(false);
        txtCantidad.setDisable(false);
        txtPrecio.setDisable(false);
    }

    private void desactivarFormulario() {
        cmbVenta.setDisable(true);
        cmbVinilo.setDisable(true);
        txtCantidad.setDisable(true);
        txtPrecio.setDisable(true);
    }

    private void activarNavegacion() {
        tablaDetalleVenta.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaDetalleVenta.setDisable(true);
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