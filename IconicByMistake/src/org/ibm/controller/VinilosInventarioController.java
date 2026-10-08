package org.ibm.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.ibm.dao.ViniloDAO;
import org.ibm.dao.impl.ViniloDAOImpl;
import org.ibm.model.Vinilo;

public class VinilosInventarioController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(VinilosInventarioController.class.getName());

    @FXML
    private TableView<Vinilo> tablaVinilos;
    @FXML
    private TableColumn<Vinilo, String> colSku;
    @FXML
    private TableColumn<Vinilo, String> colTitulo;
    @FXML
    private TableColumn<Vinilo, Double> colPrecio;
    @FXML
    private TableColumn<Vinilo, Integer> colStock;
    @FXML
    private TextField txtBuscar;
    
    @FXML
    private ImageView imgVinilo;

    private final ViniloDAO viniloDAO = new ViniloDAOImpl();
    private final ObservableList<Vinilo> listaVinilos = FXCollections.observableArrayList();
    private final FilteredList<Vinilo> vinilosFiltrados = new FilteredList<>(listaVinilos, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarTabla();
        tablaVinilos.setItems(vinilosFiltrados);
        configurarBusqueda();
        seleccionarViniloTabla();
        
        // Lanza la alerta de stock crítico al abrir el inventario
        verificarStockCritico();
    }

    private void verificarStockCritico() {
        try {
            List<Vinilo> criticos = viniloDAO.listarTodos().stream()
                    .filter(v -> v.getStockActual() <= 10)
                    .collect(Collectors.toList());

            if (!criticos.isEmpty()) {
                StringBuilder sb = new StringBuilder("Los siguientes vinilos tienen stock crítico (<= 10 unidades):\n");
                for (Vinilo v : criticos) {
                    sb.append("• ").append(v.getTituloAlbum())
                      .append(" (SKU/Código: ").append(v.getCodigoBarras())
                      .append(") - Unidades: ").append(v.getStockActual()).append("\n");
                }

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alerta de Inventario");
                alert.setHeaderText(null);
                alert.setContentText(sb.toString());
                alert.showAndWait();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al verificar stock crítico en inventario", e);
        }
    }

    public void configurarTabla() {
        // Se apunta a "codigoBarras" en lugar de "sku" para coincidir con la propiedad del modelo Vinilo
        colSku.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        // Se apunta a "tituloAlbum" en lugar de "titulo"
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("tituloAlbum"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        // Se apunta a "stockActual" en lugar de "stock"
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
    }

    private void cargarTabla() {
        try {
            listaVinilos.setAll(viniloDAO.listarTodos());
        } catch (Exception e) {
            mostrarError("Error al cargar el inventario de vinilos: " + e.getMessage());
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> filtrarVinilos());
    }

    private void filtrarVinilos() {
        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        if (busqueda.isEmpty()) {
            vinilosFiltrados.setPredicate(p -> true);
        } else {
            vinilosFiltrados.setPredicate(vinilo ->
                    (vinilo.getCodigoBarras() != null && vinilo.getCodigoBarras().toLowerCase().contains(busqueda))
                    || (vinilo.getTituloAlbum() != null && vinilo.getTituloAlbum().toLowerCase().contains(busqueda))
                    || String.valueOf(vinilo.getPrecio()).contains(busqueda)
                    || String.valueOf(vinilo.getStockActual()).contains(busqueda));
        }
    }

    private void seleccionarViniloTabla() {
        tablaVinilos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarImagenVinilo(newSelection);
            }
        });
    }

    private void mostrarImagenVinilo(Vinilo vinilo) {
        if (vinilo == null) {
            cargarImagenPorDefecto();
            return;
        }

        Image imagen = null;
        try {
            // 1. PRIORIDAD MÁXIMA: Si el vinilo tiene una ruta de foto guardada directamente
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

            // 2. RESPALDO INTELIGENTE: Búsqueda flexible por código de barras o palabras clave del título
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

            // 3. Establecer la imagen final o la predeterminada
            if (imagen != null && !imagen.isError()) {
                imgVinilo.setImage(imagen);
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
        try {
            InputStream is = getClass().getResourceAsStream("/org/ibm/images/default_vinilo.png");
            if (is != null) {
                imgVinilo.setImage(new Image(is));
            } else {
                imgVinilo.setImage(null);
            }
        } catch (Exception e) {
            imgVinilo.setImage(null);
        }
    }

    @FXML
    public void handleVolverMenu(ActionEvent event) {
        LOGGER.info("Navegando de regreso al menú principal de vinilos.");
        try {
            Stage escenarioPrincipal = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/ibm/view/DashboardBodegaView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            escenarioPrincipal.setTitle("ViniloStore - Menú Principal");
            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al intentar cargar la vista del menú principal", e);
            mostrarError("No se pudo cargar la vista del menú: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
