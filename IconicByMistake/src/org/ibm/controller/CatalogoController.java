package org.ibm.controller;

import java.io.File;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.ibm.dao.ViniloDAO;
import org.ibm.dao.impl.ViniloDAOImpl;
import org.ibm.model.Vinilo;
import org.ibm.model.Usuario;
import org.ibm.utils.SesionUsuario;

public class CatalogoController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(CatalogoController.class.getName());

    @FXML
    private TextField txtBuscar;
    @FXML
    private TilePane tileCatalogo;
    @FXML
    private ImageView imgDetalle;
    @FXML
    private Label lblDetalleTitulo;
    @FXML
    private Label lblDetalleCodigo;
    @FXML
    private Label lblDetallePrecio;
    @FXML
    private Label lblDetalleStock;
    @FXML
    private Label lblDetalleArtista;
    @FXML
    private Label lblDetalleMensaje;

    private final ViniloDAO viniloDAO = new ViniloDAOImpl();
    private final ObservableList<Vinilo> listaVinilos = FXCollections.observableArrayList();
    private final FilteredList<Vinilo> vinilosFiltrados = new FilteredList<>(listaVinilos, p -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCatalogo();
        configurarBusqueda();

        // Seleccionar y mostrar el detalle del primer vinilo automáticamente al abrir si existe
        if (!vinilosFiltrados.isEmpty()) {
            mostrarDetalle(vinilosFiltrados.get(0));
        } else {
            if (imgDetalle != null) {
                imgDetalle.setImage(null);
            }
            if (lblDetalleTitulo != null) {
                lblDetalleTitulo.setText("");
            }
            if (lblDetalleCodigo != null) {
                lblDetalleCodigo.setText("");
            }
            if (lblDetallePrecio != null) {
                lblDetallePrecio.setText("");
            }
            if (lblDetalleStock != null) {
                lblDetalleStock.setText("");
            }
            if (lblDetalleArtista != null) {
                lblDetalleArtista.setText("");
            }
            if (lblDetalleMensaje != null) {
                lblDetalleMensaje.setText("Seleccione un vinilo de la cuadrícula para ver su detalle.");
            }
        }
    }

    private void cargarCatalogo() {
        try {
            listaVinilos.setAll(viniloDAO.listarTodos());
            renderizar();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al cargar el catálogo de vinilos", e);
            mostrarError("Error al cargar el catálogo: " + e.getMessage());
        }
    }

    private void configurarBusqueda() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> {
                String busqueda = newValue == null ? "" : newValue.trim().toLowerCase();
                if (busqueda.isEmpty()) {
                    vinilosFiltrados.setPredicate(p -> true);
                } else {
                    vinilosFiltrados.setPredicate(vinilo -> {
                        boolean coincideTitulo = vinilo.getTituloAlbum() != null && vinilo.getTituloAlbum().toLowerCase().contains(busqueda);
                        boolean coincideCodigo = vinilo.getCodigoBarras() != null && vinilo.getCodigoBarras().toLowerCase().contains(busqueda);
                        return coincideTitulo || coincideCodigo;
                    });
                }
                renderizar();
            });
        }
    }

    private void renderizar() {
        if (tileCatalogo == null) {
            return;
        }
        tileCatalogo.getChildren().clear();
        for (Vinilo vinilo : vinilosFiltrados) {
            ImageView imagen = new ImageView(cargarImagenVinilo(vinilo));
            imagen.setFitWidth(110);
            imagen.setFitHeight(150);
            imagen.setPreserveRatio(true);

            Label lblTitulo = new Label(vinilo.getTituloAlbum());
            lblTitulo.setWrapText(true);
            lblTitulo.setMaxWidth(130);
            lblTitulo.setAlignment(Pos.CENTER);
            lblTitulo.setStyle("-fx-font-weight: bold;");

            Label lblPrecio = new Label(String.format("$%.2f", vinilo.getPrecio()));
            lblPrecio.setAlignment(Pos.CENTER);

            VBox card = new VBox(imagen, lblTitulo, lblPrecio);
            card.setPrefWidth(150);
            card.setAlignment(Pos.CENTER);
            card.setSpacing(6);
            card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-cursor: hand;");
            card.setOnMouseClicked(e -> mostrarDetalle(vinilo));
            tileCatalogo.getChildren().add(card);
        }
    }

    /**
     * Lógica de búsqueda flexible e inteligente de imágenes replicada de
     * Inventario.
     */
    private Image cargarImagenVinilo(Vinilo vinilo) {
        if (vinilo == null) {
            return crearImagenPlaceholder();
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
                        File carpetaGeneral = new File("C:/ge/imagenes");
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

                File carpetaImagenes = new File("C:/ge/imagenes");
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
                return imagen;
            }

        } catch (Exception e) {
            // Ignorar y retornar placeholder por defecto
        }

        return crearImagenPlaceholder();
    }

    private Image crearImagenPlaceholder() {
        WritableImage placeholder = new WritableImage(200, 260);
        PixelWriter pixelWriter = placeholder.getPixelWriter();
        Color gris = Color.web("#e0e0e0");
        for (int y = 0; y < 260; y++) {
            for (int x = 0; x < 200; x++) {
                pixelWriter.setColor(x, y, gris);
            }
        }
        return placeholder;
    }

    private void mostrarDetalle(Vinilo vinilo) {
        if (imgDetalle != null) {
            imgDetalle.setImage(cargarImagenVinilo(vinilo));
        }
        if (lblDetalleTitulo != null) {
            lblDetalleTitulo.setText(vinilo.getTituloAlbum());
        }
        if (lblDetalleCodigo != null) {
            lblDetalleCodigo.setText("Código: " + vinilo.getCodigoBarras());
        }
        if (lblDetallePrecio != null) {
            lblDetallePrecio.setText("Precio: $" + String.format("%.2f", vinilo.getPrecio()));
        }
        if (lblDetalleStock != null) {
            lblDetalleStock.setText("Stock: " + vinilo.getStockActual());
        }

        if (lblDetalleArtista != null) {
            String nombreArtista = (vinilo.getArtista() != null) ? vinilo.getArtista().getNombreArtistico() : "Desconocido";
            lblDetalleArtista.setText("Artista: " + nombreArtista);
        }

        if (lblDetalleMensaje != null) {
            lblDetalleMensaje.setText("");
        }
    }

    @FXML
    private void handleVolver(ActionEvent evento) {
        try {
            Usuario usuario = SesionUsuario.getInstancia().getUsuarioActual();
            if (usuario == null || usuario.getRol() == null) {
                mostrarError("No se encontró una sesión de usuario válida.");
                return;
            }
            String rol = usuario.getRol().trim().toLowerCase();
            String ruta;
            String titulo;

            switch (rol) {
                case "admin":
                case "administrador":
                    ruta = "/org/ibm/view/DashboardAdminView.fxml";
                    titulo = "Iconic By Mistake - Dashboard Administrador";
                    break;
                case "cajero":
                    ruta = "/org/ibm/view/CajeroDashboardView.fxml";
                    titulo = "Iconic By Mistake - Dashboard Cajero";
                    break;
                case "empleado":
                    ruta = "/org/ibm/view/DashboardBodegaView.fxml";
                    titulo = "Iconic By Mistake - Dashboard Bodega";
                    break;
                default:
                    // Si el rol no es reconocido, mandamos al login o mostramos un error claro en lugar de un AssertionError
                    mostrarError("Rol de usuario no reconocido: " + usuario.getRol());
                    return;
            }

            Stage escenarioPrincipal = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            escenarioPrincipal.setTitle(titulo);
            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.show();
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al volver al dashboard", e);
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
}
