package org.ibm.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.ibm.Main;
import org.ibm.dao.FacturaDAO;
import org.ibm.dao.impl.FacturaDAOImpl;
import org.ibm.exception.DaoException;
import org.ibm.model.DetalleVenta;
import org.ibm.model.Factura;

public class FacturaController implements Initializable {

    private static int noVentaSeleccionada;

    public static void setNoVentaSeleccionada(int noVenta) {
        noVentaSeleccionada = noVenta;
    }

    public static int getNoVentaSeleccionada() {
        return noVentaSeleccionada;
    }

    private final FacturaDAO facturaDAO = (FacturaDAO) new FacturaDAOImpl();
    private final ObservableList<DetalleVenta> lineasFactura = FXCollections.observableArrayList();
    private Factura facturaActual;

    @FXML private Label lblNoFactura;
    @FXML private Label lblFecha;
    @FXML private Label lblCliente;
    @FXML private Label lblCui;
    @FXML private Label lblCorreo;
    @FXML private Label lblUsuario;
    @FXML private Label lblTotal;

    @FXML private TableView<DetalleVenta> tablaLineas;
    @FXML private TableColumn<DetalleVenta, String> colTitulo;
    @FXML private TableColumn<DetalleVenta, String> colCodigoBarras;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleVenta, Double> colPrecioUnitario;
    @FXML private TableColumn<DetalleVenta, Double> colSubtotal;

    @FXML private Button btnImprimir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarFactura();
    }

    public void configurarTabla() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("tituloAlbum"));
        colCodigoBarras.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
    }

    private void cargarFactura() {
        try {
            List<Factura> facturas = facturaDAO.buscarFactura(noVentaSeleccionada);
            if (facturas.isEmpty()) {
                mostrarError("No se encontró la factura de la venta " + noVentaSeleccionada + ".");
                return;
            }

            facturaActual = facturas.get(0);
            lblNoFactura.setText("# " + facturaActual.getNumeroFactura());
            lblFecha.setText(facturaActual.getFechaEmision());
            lblCliente.setText(facturaActual.getNombreCliente());
            lblCui.setText(String.valueOf(facturaActual.getCuiCliente()));
            lblCorreo.setText(facturaActual.getCorreoCliente());
            lblUsuario.setText(facturaActual.getUsuarioAtendio());
            lblTotal.setText(String.format("Q %.2f", facturaActual.getGranTotal()));

            if (facturaActual.getDetalles() != null) {
                lineasFactura.setAll(facturaActual.getDetalles());
                tablaLineas.setItems(lineasFactura);
            }
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado al cargar la factura: " + e.getMessage());
        }
    }

    @FXML
    private void handleVolver() {
        try {
            Main.cambiarVista("/org/ibm/view/ListaVentasView.fxml");
        } catch (Exception e) {
            mostrarError("Error al volver a la lista de ventas: " + e.getMessage());
        }
    }

    @FXML
    private void handleImprimir() {
        if (facturaActual == null) {
            mostrarError("No hay datos de factura cargados para generar el documento.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Factura en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Factura_No_" + facturaActual.getNumeroFactura() + ".pdf");

        File file = fileChooser.showSaveDialog(tablaLineas.getScene().getWindow());
        if (file != null) {
            generarPdfFactura(file);
        }
    }

    private void generarPdfFactura(File destino) {
        Document documento = new Document();
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(destino));
            documento.open();

            Font fontEmpresa = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(41, 128, 185));
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
            Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.GRAY);
            Font fontTexto = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
            Font fontEncabezadoTabla = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);

            // Encabezado
            Paragraph pEmpresa = new Paragraph("TIENDA DE VINILOS", fontEmpresa);
            pEmpresa.setAlignment(Element.ALIGN_CENTER);
            documento.add(pEmpresa);

            Paragraph pDoc = new Paragraph("FACTURA DE VENTA", fontTitulo);
            pDoc.setAlignment(Element.ALIGN_CENTER);
            pDoc.setSpacingAfter(15);
            documento.add(pDoc);

            // Datos del cliente y factura
            PdfPTable infoTabla = new PdfPTable(2);
            infoTabla.setWidthPercentage(100);
            infoTabla.setSpacingAfter(15);

            infoTabla.addCell(crearCeldaSinBorde("No. Factura: #" + facturaActual.getNumeroFactura(), fontSubtitulo));
            infoTabla.addCell(crearCeldaSinBorde("Fecha: " + facturaActual.getFechaEmision(), fontTexto));
            infoTabla.addCell(crearCeldaSinBorde("Cliente: " + facturaActual.getNombreCliente(), fontTexto));
            infoTabla.addCell(crearCeldaSinBorde("CUI: " + facturaActual.getCuiCliente(), fontTexto));
            infoTabla.addCell(crearCeldaSinBorde("Correo: " + facturaActual.getCorreoCliente(), fontTexto));
            infoTabla.addCell(crearCeldaSinBorde("Atendido por: " + facturaActual.getUsuarioAtendio(), fontTexto));

            documento.add(infoTabla);

            // Tabla de detalle
            PdfPTable tablaPdf = new PdfPTable(5);
            tablaPdf.setWidthPercentage(100);
            tablaPdf.setWidths(new float[]{35f, 20f, 15f, 15f, 15f});

            String[] headers = {"Álbum / Vinilo", "Código Barras", "Cant.", "Precio U.", "Subtotal"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontEncabezadoTabla));
                cell.setBackgroundColor(new BaseColor(41, 128, 185));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                tablaPdf.addCell(cell);
            }

            for (DetalleVenta d : lineasFactura) {
                tablaPdf.addCell(new PdfPCell(new Phrase(d.getTituloAlbum(), fontTexto)));
                tablaPdf.addCell(new PdfPCell(new Phrase(d.getCodigoBarras(), fontTexto)));
                
                PdfPCell cCant = new PdfPCell(new Phrase(String.valueOf(d.getCantidad()), fontTexto));
                cCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPdf.addCell(cCant);

                PdfPCell cPrec = new PdfPCell(new Phrase(String.format("Q %.2f", d.getPrecioUnitario()), fontTexto));
                cPrec.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPdf.addCell(cPrec);

                PdfPCell cSub = new PdfPCell(new Phrase(String.format("Q %.2f", d.getSubTotal()), fontTexto));
                cSub.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPdf.addCell(cSub);
            }

            documento.add(tablaPdf);

            // Total
            Paragraph pTotal = new Paragraph("\nGRAN TOTAL: Q " + String.format("%.2f", facturaActual.getGranTotal()), fontTitulo);
            pTotal.setAlignment(Element.ALIGN_RIGHT);
            documento.add(pTotal);

            documento.close();

            mostrarInformacion("Factura guardada correctamente en PDF:\n" + destino.getAbsolutePath());

        } catch (DocumentException | IOException e) {
            mostrarError("Error al generar el archivo PDF: " + e.getMessage());
        }
    }

    private PdfPCell crearCeldaSinBorde(String texto, Font fuente) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(3);
        return cell;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
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