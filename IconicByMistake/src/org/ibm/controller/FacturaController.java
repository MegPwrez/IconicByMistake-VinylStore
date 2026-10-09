package org.ibm.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.ibm.Main;
import org.ibm.dao.FacturaDAO;
import org.ibm.dao.impl.FacturaDAOImpl;
import org.ibm.exception.DaoException;
import org.ibm.model.DetalleVenta;
import org.ibm.model.Factura;

public class FacturaController implements Initializable {

    @FXML
    private TextArea txtAreaFactura; 
    
   @FXML
    private Button btnfacturapdf;
   
    private static int noVentaSeleccionada;
    private final FacturaDAO facturaDAO = new FacturaDAOImpl();

    public static int getNoVentaSeleccionada() {
        return noVentaSeleccionada;
    }

    public static void setNoVentaSeleccionada(int noVenta) {
        noVentaSeleccionada = noVenta;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (txtAreaFactura != null) {
            txtAreaFactura.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        }
        cargarFacturaFormateada();
    }
private void cargarFacturaFormateada() {
    try {
        int idVenta = getNoVentaSeleccionada();
        
        List<Factura> lista = facturaDAO.buscarFactura(idVenta);
        if (lista == null || lista.isEmpty()) {
            mostrarError("No se encontraron los datos de la factura.");
            return;
        }
        Factura factura = lista.get(0);
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("          ICONIC BY MISTAKE             \n");
        sb.append("            VINYL STORE                 \n");
        sb.append("========================================\n");
        sb.append("Factura No.  : # ").append(factura.getNumeroFactura()).append("\n");
        sb.append("Fecha        : ").append(factura.getFechaEmision()).append("\n");
        sb.append("Cliente      : ").append(factura.getNombreCliente()).append("\n");
        sb.append("CUI          : ").append(factura.getCuiCliente()).append("\n");
        sb.append("Correo       : ").append(factura.getCorreoCliente()).append("\n");
        sb.append("Atendido por : ").append(factura.getUsuarioAtendio()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-18s %-5s %-10s %-8s\n", "Álbum", "Cant", "P.Unit", "Subtotal"));
        sb.append("----------------------------------------\n");
        if (factura.getDetalles() != null) {
            for (DetalleVenta item : factura.getDetalles()) {
                String titulo = item.getTituloAlbum(); 
                if (titulo != null && titulo.length() > 18) {
                    titulo = titulo.substring(0, 15) + "...";
                }
                sb.append(String.format("%-18s %-5d Q%-9.2f Q%-7.2f\n",
                        titulo != null ? titulo : "",
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        item.getSubTotal()));
            }
        }
        sb.append("----------------------------------------\n");
        sb.append("TOTAL A PAGAR: Q ").append(String.format("%.2f", factura.getGranTotal())).append("\n");
        sb.append("========================================\n");
        sb.append("    ¡GRACIAS POR SU COMPRA EN IBM!      \n");
        sb.append("========================================\n");

        txtAreaFactura.setText(sb.toString());

    } catch (DaoException e) {
        mostrarError(e.getMessage());
    } catch (Exception e) {
        mostrarError("Error al generar el formato de factura: " + e.getMessage());
    }
}
    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            Main.cambiarVista("/org/ibm/view/CajeroDashboardView.fxml");
        } catch (Exception e) {
            mostrarError("Error al volver al menú: " + e.getMessage());
        }
    }
 @FXML
    private void handleExportarpdf() {
        String contenidoFactura = txtAreaFactura.getText();
        if (contenidoFactura == null || contenidoFactura.isEmpty()) {
            mostrarError("No hay contenido en la factura para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Factura como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        
        int idVenta = FacturaController.getNoVentaSeleccionada();
        fileChooser.setInitialFileName("Factura_" + idVenta + ".pdf");

        File file = fileChooser.showSaveDialog(btnfacturapdf.getScene().getWindow());

        if (file != null) {
            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                
                com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.COURIER, 10);
                document.add(new Paragraph(contenidoFactura, font));
                
                document.close();

                mostrarMensajeExito("Factura exportada exitosamente en:\n" + file.getAbsolutePath());

            } catch (DocumentException | java.io.FileNotFoundException e) {
                mostrarError("Error al generar el PDF: " + e.getMessage());
            }
        }
    }
   
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Alert alertError = new Alert(Alert.AlertType.ERROR);
        alertError.setTitle("Error");
        alertError.setHeaderText(null);
        alertError.setContentText(mensaje);
        alertError.showAndWait();
    }
        private void mostrarMensajeExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}