
package org.ibm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Logger log = Logger.getLogger(Main.class.getName());
    private static Stage escenarioPrincipal;

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        log.info("Iniciando aplicación Librería Saturno...");
        this.escenarioPrincipal = escenarioPrincipal;
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/ibm/view/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            escenarioPrincipal.setTitle("Vinyl Store- Iconic By Mistake");
            escenarioPrincipal.setScene(scene);
            escenarioPrincipal.show();
            Main.escenarioPrincipal = escenarioPrincipal;     
            
            cambiarEscena("/org/ibm/view/LoginView.fxml");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Fallo crítico al inicializar la ventana principal en start()", e);
            throw e;
        }
    }

    public static void cambiarVista(String fxmlPath) throws Exception {
        log.info("Cambiando vista a: " + fxmlPath);
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            escenarioPrincipal.setScene(new Scene(root));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error al cambiar vista hacia: " + fxmlPath, e);
            throw e;
        }
    }
    
    public static void cambiarEscena(String rutaFXML) throws IOException {
        log.info("Cargando escena desde: " + rutaFXML);
        try {
            Parent raiz = FXMLLoader.load(Main.class.getResource(rutaFXML));                
            Scene escena = new Scene(raiz); 
            escenarioPrincipal.setScene(escena);
            escenarioPrincipal.sizeToScene();
            escenarioPrincipal.centerOnScreen();
            escenarioPrincipal.show();        
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error de I/O al cargar la escena FXML: " + rutaFXML, e);
            throw e;
        }
    }

    public static void main(String[] args) {
        log.info("Lanzando JavaFX Application...");
        launch(args);
    }
}