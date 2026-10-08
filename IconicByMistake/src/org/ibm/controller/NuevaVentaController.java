package org.ibm.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;
import org.ibm.Main;
import org.ibm.Ventaservice.VentaService;
import org.ibm.dao.ClienteDAO;
import org.ibm.dao.ViniloDAO;
import org.ibm.dao.impl.ClienteDAOImpl;
import org.ibm.dao.impl.ViniloDAOImpl;
import org.ibm.exception.ValidacionException;
import org.ibm.model.Cliente;
import org.ibm.model.DetalleVenta;
import org.ibm.model.Usuario;
import org.ibm.model.Venta;
import org.ibm.model.Vinilo;
import org.ibm.utils.SesionUsuario;

public class NuevaVentaController implements Initializable {

    private static final Logger log = Logger.getLogger(NuevaVentaController.class.getName());

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<Vinilo> cmbVinilo;
    @FXML private ImageView imgVinilo;
    @FXML private Spinner<Integer> spCantidad;
    @FXML private Button btnAgregar;
    @FXML private Button btnRegistrar;
    @FXML private Button btnQuitar;
    @FXML private Button btnVaciar;
    @FXML private TableView<DetalleVenta> tablaLineas;
    @FXML private TableColumn<DetalleVenta, String> colCodigoBarras;
    @FXML private TableColumn<DetalleVenta, String> colTitulo;
    @FXML private TableColumn<DetalleVenta, Double> colPrecio;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleVenta, Double> colsubtotal;
    @FXML private Label lblTotal;
    @FXML private Label lblMensaje;

    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final ViniloDAO viniloDAO = new ViniloDAOImpl();
    private final VentaService ventaService = new VentaService();
    private final ObservableList<DetalleVenta> lineasVenta = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCombos();
        tablaLineas.setItems(lineasVenta);
        configurarTabla();
        configurarSpinner();
        calcularTotal();

        cmbVinilo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mostrarImagenVinilo(newValue);
        });

        cargarImagenPorDefecto();
    }

    private void cargarCombos() {
        try {
            cmbCliente.setItems(FXCollections.observableArrayList(clienteDAO.listar()));
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
        } catch (Exception e) {
            mostrarError("Error al cargar combos: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        colCodigoBarras.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("tituloAlbum"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colsubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
    }

    private void configurarSpinner() {
        spCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
    }

    private double calcularTotal() {
        double total = 0;
        for (DetalleVenta linea : lineasVenta) {
            total += linea.getSubTotal();
        }
        lblTotal.setText(String.format("Total: Q%.2f", total));
        return total;
    }

    private void mostrarImagenVinilo(Vinilo vinilo) {
        if (vinilo == null) {
            cargarImagenPorDefecto();
            return;
        }

        Image imagen = null;
        try {
            if (vinilo.getUrlFoto() != null && !vinilo.getUrlFoto().trim().isEmpty()) {
                String rutaFoto = vinilo.getUrlFoto().trim();
                File archivoDirecto = new File(rutaFoto);
                
                if (archivoDirecto.exists()) {
                    imagen = new Image(archivoDirecto.toURI().toString());
                } else {
                    File archivoRelativo = new File(System.getProperty("user.dir"), rutaFoto);
                    if (archivoRelativo.exists()) {
                        imagen = new Image(archivoRelativo.toURI().toString());
                    } else {
                        File carpetaGeneral = new File("C:/Ge/imagenes");
                        if (!carpetaGeneral.exists()) {
                            carpetaGeneral = new File(System.getProperty("user.dir"), "imagenes");
                        }
                        File archivoEnCarpeta = new File(carpetaGeneral, rutaFoto);
                        if (archivoEnCarpeta.exists()) {
                            imagen = new Image(archivoEnCarpeta.toURI().toString());
                        }
                    }
                }
            }

            if (imagen == null || imagen.isError()) {
                String skuBusqueda = vinilo.getCodigoBarras() != null ? vinilo.getCodigoBarras().trim().toLowerCase().replaceAll("[^a-z0-9]", "") : "";
                String tituloBusqueda = vinilo.getTituloAlbum() != null ? vinilo.getTituloAlbum().trim().toLowerCase().replaceAll("[^a-z0-9]", "") : "";
                
                String palabraClaveTitulo = "";
                if (vinilo.getTituloAlbum() != null && !vinilo.getTituloAlbum().trim().isEmpty()) {
                    String[] palabras = vinilo.getTituloAlbum().trim().toLowerCase().split("[^a-z0-9]+");
                    for (String p : palabras) {
                        if (p.length() > 2 && !p.equals("the") && !p.equals("and") && !p.equals("for")) {
                            palabraClaveTitulo = p;
                            break;
                        }
                    }
                }

                File carpetaImagenes = new File("C:/Ge/imagenes");
                if (!carpetaImagenes.exists()) {
                    carpetaImagenes = new File(System.getProperty("user.dir"), "imagenes");
                }

                File imagenEncontrada = null;
                if (carpetaImagenes.exists() && carpetaImagenes.isDirectory()) {
                    File[] archivos = carpetaImagenes.listFiles();
                    if (archivos != null) {
                        for (File archivo : archivos) {
                            String nombreCompleto = archivo.getName().toLowerCase();
                            String nombreSinExt = nombreCompleto.contains(".") ? nombreCompleto.substring(0, nombreCompleto.lastIndexOf('.')) : nombreCompleto;
                            String nombreArchivoLimpio = nombreSinExt.replaceAll("[^a-z0-9]", "");
                            
                            boolean coincideSku = !skuBusqueda.isEmpty() && nombreArchivoLimpio.contains(skuBusqueda);
                            boolean coincideTitulo = !tituloBusqueda.isEmpty() && (nombreArchivoLimpio.contains(tituloBusqueda) || tituloBusqueda.contains(nombreArchivoLimpio));
                            boolean coincidePalabra = !palabraClaveTitulo.isEmpty() && nombreArchivoLimpio.contains(palabraClaveTitulo);

                            if (coincideSku || coincideTitulo || coincidePalabra) {
                                imagenEncontrada = archivo;
                                if (coincideSku || coincideTitulo) {
                                    break;
                                }
                            }
                        }
                    }
                }

                if (imagenEncontrada != null && imagenEncontrada.exists()) {
                    imagen = new Image(imagenEncontrada.toURI().toString());
                }
            }

            if (imagen != null && !imagen.isError()) {
                if (imgVinilo != null) {
                    imgVinilo.setImage(imagen);
                }
            } else {
                System.out.println("⚠️ No se encontró imagen para el vinilo: " + vinilo.getTituloAlbum() + " (Código: " + vinilo.getCodigoBarras() + ")");
                cargarImagenPorDefecto();
            }

        } catch (Exception e) {
            System.err.println("❌ Error al procesar la imagen del vinilo [" + vinilo.getTituloAlbum() + "]: " + e.getMessage());
            cargarImagenPorDefecto();
        }
    }

    private void cargarImagenPorDefecto() {
        if (imgVinilo == null) return;
        try {
            URL defaultUrl = getClass().getResource("/org/ibm/assets/default_album.png");
            if (defaultUrl != null) {
                imgVinilo.setImage(new Image(defaultUrl.toExternalForm()));
            } else {
                imgVinilo.setImage(null);
            }
        } catch (Exception e) {
            imgVinilo.setImage(null);
        }
    }

    @FXML
    private void handleAgregarLinea() {
        Vinilo vinilo = cmbVinilo.getValue();
        if (vinilo == null) {
            mostrarAdvertencia("Seleccione un vinilo para agregar a la venta.");
            return;
        }
        int cantidadNueva = spCantidad.getValue();
        int cantidadAcumulada = 0;

        DetalleVenta itemExistente = null;
        for (DetalleVenta item : lineasVenta) {
            if (item.getCodigoBarras().equals(vinilo.getCodigoBarras())) {
                itemExistente = item;
                cantidadAcumulada = item.getCantidad();
                break;
            }
        }
        if (vinilo.getStockActual() < (cantidadAcumulada + cantidadNueva)) {
            mostrarAdvertencia("Stock insuficiente. Disponible: " + vinilo.getStockActual() + ".");
            return;
        }
        
        if (itemExistente != null) {
            lineasVenta.remove(itemExistente);
            int nuevaCantidadTotal = cantidadAcumulada + cantidadNueva;
            DetalleVenta itemActualizado = new DetalleVenta(
                vinilo.getCodigoBarras(),
                vinilo.getTituloAlbum(),
                vinilo.getPrecio(),
                nuevaCantidadTotal
            );
            lineasVenta.add(itemActualizado);
        } else {
            DetalleVenta nuevoItem = new DetalleVenta(
                vinilo.getCodigoBarras(),
                vinilo.getTituloAlbum(),
                vinilo.getPrecio(),
                cantidadNueva
            );
            lineasVenta.add(nuevoItem);
        }

        calcularTotal();
        lblMensaje.setText("");
        cmbVinilo.setValue(null);
        spCantidad.getValueFactory().setValue(1);
    }

    @FXML
    private void handleQuitarLinea() {
        DetalleVenta seleccion = tablaLineas.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAdvertencia("Seleccione una línea de la tabla para quitar.");
            return;
        }
        lineasVenta.remove(seleccion);
        calcularTotal();
    }

    @FXML
    private void handleVaciar() {
        lineasVenta.clear();
        calcularTotal();
        lblMensaje.setText("");
    }

    @FXML
    private void handleRegistrarVenta() {
        try {
            Usuario usuarioActual = SesionUsuario.getInstancia().getUsuarioActual();
            if (usuarioActual == null) {
                throw new ValidacionException("No hay una sesión de usuario activa. Inicie sesión nuevamente.");
            }
            if (cmbCliente.getValue() == null) {
                throw new ValidacionException("Seleccione el cliente de la venta.");
            }
            if (lineasVenta.isEmpty()) {
                throw new ValidacionException("Agregue al menos un vinilo a la venta.");
            }

            int idUsuario = usuarioActual.getIdUsuario();
            long cuiCliente = cmbCliente.getValue().getCui();
            double totalCalculado = calcularTotal();

            Venta nuevaVenta = new Venta();
            nuevaVenta.setSubTotal(String.valueOf(totalCalculado));
            nuevaVenta.setDescuento(0.00);
            nuevaVenta.setTotalVenta(totalCalculado);
            nuevaVenta.setCuiCliente(cuiCliente);
            nuevaVenta.setId_usuario(idUsuario);

            boolean exito = ventaService.procesarVenta(nuevaVenta, lineasVenta);
            if (!exito) {
                mostrarError("No se pudo registrar la venta. Verifique el stock.");
                return;
            }
            FacturaController.setNoVentaSeleccionada(nuevaVenta.getIdVenta());
            Main.cambiarVista("/org/ibm/view/FacturaView.fxml");

        } catch (ValidacionException e) {
            mostrarAdvertencia(e.getMessage());
            lblMensaje.setText(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al registrar la venta: " + e.getMessage());
        }
    }

    private void limpiarVenta() {
        lineasVenta.clear();
        cmbCliente.setValue(null);
        cmbVinilo.setValue(null);
        spCantidad.getValueFactory().setValue(1);
        calcularTotal();
    }

    @FXML
    public void handleVolver(ActionEvent event) {
        try {
            Main.cambiarVista("/org/ibm/view/CajeroDashboardView.fxml");
        } catch (Exception e) {
            mostrarError("Error al volver al menú: " + e.getMessage());
        }
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