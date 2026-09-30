package org.ibm.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.ibm.manager.SesionContext;
import org.ibm.model.Usuario;
import org.ibm.utils.SesionUsuario;

public class DashboardBodegaController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(DashboardBodegaController.class.getName());

    @FXML private Label lblBienvenida;
    @FXML private Label lblRol;
    @FXML private Button btnCerrarSesion;
    @FXML private Circle avatarCircle;

    @FXML private Button btnInventario;
    @FXML private Button btnVinilo;
    @FXML private Button btnCatalogo;
    @FXML private Button btnArtista;
    @FXML private Button btnGeneros;
    @FXML private Button btnProductores;

    @FXML private VBox cardVerInventario;
    @FXML private VBox cardNuevoVinilo;
    @FXML private VBox cardNuevoArtista;
    @FXML private VBox cardNuevoGenero;
    @FXML private VBox cardNuevoProductor;
    @FXML private VBox cardNuevoCliente;

    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioActual = SesionContext.getInstancia().getUsuarioActual();
        if (usuarioActual != null) {
            lblBienvenida.setText("Bienvenido, " + usuarioActual.getNombreUsuario());
            
            String iniciales = usuarioActual.getNombreUsuario()
                    .substring(0, Math.min(2, usuarioActual.getNombreUsuario().length()))
                    .toUpperCase();
            lblRol.setText(iniciales + " · " + capitalize(usuarioActual.getRol()));
        } else {
            lblBienvenida.setText("Bienvenido, Invitado");
            lblRol.setText("?? · Sin sesión");
        }
    }

    private String capitalize(String texto) {
        if (texto == null || texto.isEmpty()) return "";
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    @FXML
    public void cerrarSesion(ActionEvent evento) {
        LOGGER.info("Cerrando sesión desde el menú de bodega.");
        SesionUsuario.getInstancia().cerrarSesion();
        navegar(evento, "/org/ibm/view/LoginView.fxml", "Librería Saturno - Inicio de Sesión");
    }

    @FXML
    public void irAInventario(ActionEvent evento) {
        navegar(evento, "/org/ibm/view/InventarioView.fxml", "Librería Saturno - Inventario");
    }

    @FXML
    public void irAVinilo(ActionEvent evento) {
        navegar(evento, "/org/ibm/view/ViniloView.fxml", "Librería Saturno - Vinilos");
    }

    @FXML
    public void irACatalogo(ActionEvent evento) {
        navegar(evento, "/org/ibm/view/CatalogoView.fxml", "Librería Saturno - Catálogo");
    }

    @FXML
    public void irAArtista(ActionEvent evento) {
        navegar(evento, "/org/ibm/view/ArtistaView.fxml", "Librería Saturno - Artistas");
    }

    @FXML
    public void irAGeneros(ActionEvent evento) {
        navegar(evento, "/org/ibm/view/GeneroView.fxml", "Librería Saturno - Géneros");
    }

    @FXML
    public void irAProductores(ActionEvent evento) {
        navegar(evento, "/org/ibm/view/ProductorView.fxml", "Librería Saturno - Productores");
    }

    @FXML
    public void verInventario(MouseEvent evento) {
        navegar(evento, "/org/ibm/view/InventarioView.fxml", "Librería Saturno - Inventario");
    }

    @FXML
    public void nuevoVinilo(MouseEvent evento) {
        navegar(evento, "/org/ibm/view/ViniloView.fxml", "Librería Saturno - Nuevo Vinilo");
    }

    @FXML
    public void nuevoArtista(MouseEvent evento) {
        navegar(evento, "/org/ibm/view/ArtistaView.fxml", "Librería Saturno - Nuevo Artista");
    }

    @FXML
    public void nuevoGenero(MouseEvent evento) {
        navegar(evento, "/org/ibm/view/GeneroView.fxml", "Librería Saturno - Nuevo Género");
    }

    @FXML
    public void nuevoProductor(MouseEvent evento) {
        navegar(evento, "/org/ibm/view/ProductorView.fxml", "Librería Saturno - Nuevo Productor");
    }

    @FXML
    public void nuevoCliente(MouseEvent evento) {
        navegar(evento, "/org/ibm/view/ClienteView.fxml", "Librería Saturno - Nuevo Cliente");
    }

    // Método de navegación genérico unificado para ActionEvent y MouseEvent
    private void navegar(Event evento, String ruta, String titulo) {
        try {
            URL archivoFxml = getClass().getResource(ruta);
            if (archivoFxml == null) {
                throw new IOException("No se encontró el archivo FXML en la ruta especificada: " + ruta);
            }

            Stage escenarioPrincipal = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(archivoFxml);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            escenarioPrincipal.setTitle(titulo);
            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.show();
        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.WARNING, "Error al cargar la ruta: " + ruta, e);
            mostrarAlertaConstruccion(ruta);
        }
    }

    private void mostrarAlertaConstruccion(String ruta) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Aviso");
        alerta.setHeaderText("Vista no disponible");
        alerta.setContentText("No se pudo cargar el archivo FXML en la ruta: " + ruta + "\nVerifica que el archivo exista en el paquete view.");
        alerta.showAndWait();
    }
}