package org.ibm.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.ibm.Main;
import org.ibm.dao.ClienteDAO;
import org.ibm.dao.UsuarioDAO;
import org.ibm.dao.VentaDAO;
import org.ibm.dao.impl.ClienteDAOImpl;
import org.ibm.dao.impl.UsuarioDAOImpl;
import org.ibm.dao.impl.VentaDAOImpl;
import org.ibm.exception.ValidacionException;
import org.ibm.model.Cliente;
import org.ibm.model.Usuario;
import org.ibm.model.Venta;
import org.ibm.utils.ControlAcceso;
import org.ibm.utils.SesionUsuario;

public class ListaVentasController implements Initializable {

    private static final Logger log = Logger.getLogger(ListaVentasController.class.getName());

    // Componentes del FXML - Panel Izquierdo
    @FXML private TextField txtTotal;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<Usuario> cmbUsuario;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Label lblMensaje;

    // Componentes del FXML - Panel Central
    @FXML private TextField txtBuscar;
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, Integer> colNoVenta;
    @FXML private TableColumn<Venta, Timestamp> colFechaVenta;
    @FXML private TableColumn<Venta, Double> colTotalVenta;
    @FXML private TableColumn<Venta, Long> colCuiCliente;
    @FXML private TableColumn<Venta, Integer> colUsuario;

    // Botones de Navegación y Reportes
    @FXML private Button btnPrimero;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnUltimo;
    @FXML private Button btnObtenerReporte;

    // Variables de Estado y Persistencia
    private boolean modoEdicion = false;
    private Venta enEdicion;
    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();
    private final FilteredList<Venta> ventasFiltradas = new FilteredList<>(listaVentas, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        configurarFormatosCombo();
        cargarClientes();
        cargarUsuarios();
        cargarTabla();

        tablaVentas.setItems(ventasFiltradas);
        seleccionarFila();
        configurarBusqueda();
    }

    public void configurarTabla() {
        colNoVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colFechaVenta.setCellValueFactory(new PropertyValueFactory<>("fechaVenta"));
        colTotalVenta.setCellValueFactory(new PropertyValueFactory<>("totalVenta"));
        colCuiCliente.setCellValueFactory(new PropertyValueFactory<>("cuiCliente"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("id_usuario"));
    }

    private void configurarFormatosCombo() {
        cmbUsuario.setConverter(new StringConverter<Usuario>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario == null ? "" : usuario.getIdUsuario() + " - " + usuario.getNombreUsuario();
            }

            @Override
            public Usuario fromString(String string) {
                return null;
            }
        });
        cmbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                return cliente == null ? "" : cliente.getCui() + " - " + cliente.getNombreCliente() + " " + cliente.getApellidoCliente();
            }

            @Override
            public Cliente fromString(String string) {
                return null;
            }
        });
    }

    private void cargarTabla() {
        try {
            listaVentas.setAll(ventaDAO.listarTodos());
        } catch (Exception e) {
            mostrarError("Error al cargar ventas: " + e.getMessage());
        }
    }

    private void cargarClientes() {
        try {
            cmbCliente.setItems(FXCollections.observableArrayList(clienteDAO.listar()));
        } catch (Exception e) {
            mostrarError("Error al cargar clientes: " + e.getMessage());
        }
    }

    private void cargarUsuarios() {
        try {
            cmbUsuario.setItems(FXCollections.observableArrayList(usuarioDAO.listarTodos()));
        } catch (Exception e) {
            mostrarError("Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarVentas());
    }

    private void filtrarVentas() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            ventasFiltradas.setPredicate(p -> true);
        } else {
            ventasFiltradas.setPredicate(venta ->
                    String.valueOf(venta.getIdVenta()).contains(busqueda)
                    || (venta.getFechaVenta() != null && venta.getFechaVenta().toString().toLowerCase().contains(busqueda))
                    || String.valueOf(venta.getTotalVenta()).contains(busqueda)
                    || String.valueOf(venta.getCuiCliente()).contains(busqueda)
                    || String.valueOf(venta.getId_usuario()).contains(busqueda));
        }
    }

    private void seleccionarFila() {
        tablaVentas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtTotal.setText(String.valueOf(newSelection.getTotalVenta()));

                        cmbCliente.setValue(null);
                        for (Cliente cliente : cmbCliente.getItems()) {
                            if (cliente.getCui() == newSelection.getCuiCliente()) {
                                cmbCliente.setValue(cliente);
                                break;
                            }
                        }

                        Timestamp timestamp = newSelection.getFechaVenta();
                        if (timestamp != null) {
                            dpFecha.setValue(timestamp.toLocalDateTime().toLocalDate());
                        } else {
                            dpFecha.setValue(null);
                        }

                        cmbUsuario.setValue(null);
                        for (Usuario usuario : cmbUsuario.getItems()) {
                            if (usuario.getIdUsuario() == newSelection.getId_usuario()) {
                                cmbUsuario.setValue(usuario);
                                break;
                            }
                        }

                        desactivarFormulario();
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            if (txtTotal.getText().trim().isEmpty()) {
                throw new ValidacionException("El campo total es obligatorio.");
            }
            if (cmbCliente.getValue() == null) {
                throw new ValidacionException("Seleccione un cliente.");
            }

            double totalNum = Double.parseDouble(txtTotal.getText().trim().replace(",", "."));

            Venta venta = new Venta();
            venta.setIdVenta(modoEdicion ? enEdicion.getIdVenta() : 0);

            if (dpFecha.getValue() != null) {
                venta.setFechaVenta(Timestamp.valueOf(dpFecha.getValue().atStartOfDay()));
            } else {
                venta.setFechaVenta(new Timestamp(System.currentTimeMillis()));
            }
            venta.setSubTotal(String.valueOf(totalNum));
            venta.setDescuento(0.00);
            venta.setTotalVenta(totalNum);
            venta.setCuiCliente(cmbCliente.getValue().getCui());
            if (cmbUsuario.getValue() != null) {
                venta.setId_usuario(cmbUsuario.getValue().getIdUsuario());
            } else if (ControlAcceso.getUsuarioLogueado() != null) {
                venta.setId_usuario(ControlAcceso.getUsuarioLogueado().getIdUsuario());
            } else {
                throw new ValidacionException("No hay usuario en sesión ni seleccionado.");
            }

            boolean guardado;
            if (modoEdicion) {
                guardado = ventaDAO.actualizar(venta);
            } else {
                guardado = ventaDAO.crear(venta);
            }

            if (guardado) {
                lblMensaje.setText(modoEdicion ? "Venta actualizada." : "Venta registrada.");
                cargarTabla();
                limpiarFormulario();
                desactivarFormulario();
                activarNavegacion();
                modoEdicion = false;
            } else {
                mostrarError("No se pudo guardar la venta.");
            }
        } catch (ValidacionException e) {
            mostrarAdvertencia(e.getMessage());
            lblMensaje.setText(e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAdvertencia("El formato del total no es válido.");
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
        tablaVentas.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        dpFecha.setValue(LocalDate.now());
        if (ControlAcceso.getUsuarioLogueado() != null) {
            for (Usuario u : cmbUsuario.getItems()) {
                if (u.getIdUsuario() == ControlAcceso.getUsuarioLogueado().getIdUsuario()) {
                    cmbUsuario.setValue(u);
                    break;
                }
            }
        }
        txtTotal.requestFocus();
    }

    @FXML
    private void handleEditar() {
        Venta seleccion = tablaVentas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione una venta de la tabla para editar.");
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
        if (!tablaVentas.getItems().isEmpty()) {
            tablaVentas.getSelectionModel().selectFirst();
            tablaVentas.scrollTo(0);
        }
    }

    @FXML
    private void handleAnterior() {
        if (!tablaVentas.getItems().isEmpty()) {
            tablaVentas.getSelectionModel().selectPrevious();
            if (tablaVentas.getSelectionModel().getSelectedIndex() >= 0) {
                tablaVentas.scrollTo(tablaVentas.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleSiguiente() {
        if (!tablaVentas.getItems().isEmpty()) {
            tablaVentas.getSelectionModel().selectNext();
            if (tablaVentas.getSelectionModel().getSelectedIndex() >= 0) {
                tablaVentas.scrollTo(tablaVentas.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void handleUltimo() {
        if (!tablaVentas.getItems().isEmpty()) {
            tablaVentas.getSelectionModel().selectLast();
            tablaVentas.scrollTo(tablaVentas.getItems().size() - 1);
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            Usuario usuario = SesionUsuario.getInstancia().getUsuarioActual();
            String ruta = "/org/ibm/view/CajeroDashboardView.fxml";
            if (usuario != null && "admin".equalsIgnoreCase(usuario.getRol())) {
                ruta = "/org/ibm/view/DashboardAdminView.fxml";
            }
            Main.cambiarVista(ruta);
        } catch (Exception e) {
            mostrarError("Error al volver al menú: " + e.getMessage());
        }
    }
@FXML
    private void handleFactura(ActionEvent event) {
        Venta seleccion = tablaVentas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAdvertencia("Seleccione una venta de la tabla para ver su factura.");
            return;
        }

        try {
            FacturaController.setNoVentaSeleccionada(seleccion.getIdVenta());
            Main.cambiarVista("/org/ibm/view/FacturaView.fxml");
        } catch (Exception e) {
            mostrarError("Error al abrir la factura: " + e.getMessage());
        }
    }

    @FXML
    private void handleObtenerReporte(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Ventas");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_De_Ventas.pdf");

        File file = fileChooser.showSaveDialog(tablaVentas.getScene().getWindow());
        if (file != null) {
            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, new FileOutputStream(file));
                documento.open();

                // Fuentes
                Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
                Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
                Font fontContenido = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);

                // Título del reporte
                Paragraph titulo = new Paragraph("Librería Saturno - Reporte General de Ventas", fontTitulo);
                titulo.setAlignment(Paragraph.ALIGN_CENTER);
                titulo.setSpacingAfter(20);
                documento.add(titulo);

                // Tabla PDF con 5 columnas
                PdfPTable tablaPdf = new PdfPTable(5);
                tablaPdf.setWidthPercentage(100);
                tablaPdf.setWidths(new float[]{15f, 30f, 20f, 20f, 15f});

                // Encabezados
                String[] headers = {"No. Venta", "Fecha", "Total", "CUI Cliente", "Usuario"};
                for (String header : headers) {
                    PdfPCell celda = new PdfPCell(new Phrase(header, fontEncabezado));
                    celda.setBackgroundColor(new BaseColor(41, 128, 185)); // Azul corporativo
                    celda.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    celda.setPadding(6);
                    tablaPdf.addCell(celda);
                }

                // Llenar datos (respeta los filtros aplicados en la interfaz)
                double totalGeneral = 0.0;
                for (Venta v : ventasFiltradas) {
                    tablaPdf.addCell(new PdfPCell(new Phrase(String.valueOf(v.getIdVenta()), fontContenido)));
                    tablaPdf.addCell(new PdfPCell(new Phrase(v.getFechaVenta() != null ? v.getFechaVenta().toString() : "", fontContenido)));
                    tablaPdf.addCell(new PdfPCell(new Phrase(String.format("Q %.2f", v.getTotalVenta()), fontContenido)));
                    tablaPdf.addCell(new PdfPCell(new Phrase(String.valueOf(v.getCuiCliente()), fontContenido)));
                    tablaPdf.addCell(new PdfPCell(new Phrase(String.valueOf(v.getId_usuario()), fontContenido)));

                    totalGeneral += v.getTotalVenta();
                }

                documento.add(tablaPdf);

                // Agregar Total General al pie del reporte
                Paragraph totalParrafo = new Paragraph("\nTotal General Acumulado: Q " + String.format("%.2f", totalGeneral), fontTitulo);
                totalParrafo.setAlignment(Paragraph.ALIGN_RIGHT);
                documento.add(totalParrafo);

                documento.close();
                mostrarInformacion("Reporte PDF generado exitosamente.");

            } catch (DocumentException | java.io.IOException e) {
                mostrarError("Error al generar el PDF: " + e.getMessage());
            }
        }
    }

    private void limpiarFormulario() {
        txtTotal.clear();
        dpFecha.setValue(null);
        cmbUsuario.setValue(null);
        cmbCliente.setValue(null);
    }

    private void activarFormulario() {
        txtTotal.setDisable(false);
        dpFecha.setDisable(false);
        cmbCliente.setDisable(false);
        cmbUsuario.setDisable(false);
    }

    private void desactivarFormulario() {
        txtTotal.setDisable(true);
        dpFecha.setDisable(true);
        cmbUsuario.setDisable(true);
        cmbCliente.setDisable(true);
    }

    private void activarNavegacion() {
        tablaVentas.setDisable(false);
        btnNuevo.setDisable(false);
        btnEditar.setDisable(false);
        btnPrimero.setDisable(false);
        btnAnterior.setDisable(false);
        btnSiguiente.setDisable(false);
        btnUltimo.setDisable(false);
        btnObtenerReporte.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void desactivarNavegacion() {
        tablaVentas.setDisable(true);
        btnNuevo.setDisable(true);
        btnEditar.setDisable(true);
        btnPrimero.setDisable(true);
        btnAnterior.setDisable(true);
        btnSiguiente.setDisable(true);
        btnUltimo.setDisable(true);
        btnObtenerReporte.setDisable(true);
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

    private void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
