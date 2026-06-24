package com.acculab.controllers;

import com.acculab.dao.OrdenDAO;
import com.acculab.models.Orden;
import com.acculab.services.OrdenService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class LaboratorioListaController {

    @FXML private TextField txtBuscarOrden;
    @FXML private TableView<Orden> tableOrdenes;
    @FXML private TableColumn<Orden, String> colId;
    @FXML private TableColumn<Orden, String> colFecha;
    @FXML private TableColumn<Orden, String> colPaciente;
    @FXML private TableColumn<Orden, String> colMedico;
    @FXML private TableColumn<Orden, String> colEstado;

    private OrdenDAO ordenDAO;
    private OrdenService ordenService;
    private ObservableList<Orden> listaOrdenes;
    private FilteredList<Orden> ordenesFiltradas;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        ordenDAO = new OrdenDAO();
        ordenService = new OrdenService(ordenDAO);

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colFecha.setCellValueFactory(cellData -> {
            java.time.LocalDateTime fecha = cellData.getValue().getFechaCreacion();
            return new SimpleStringProperty(fecha != null ? fecha.format(formatter) : "N/A");
        });
        colPaciente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPaciente().toString()));
        colMedico.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicoSolicitante()));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado().toString()));

        cargarOrdenesPendientes();
        configurarBuscador();

        // Doble clic para abrir los resultados
        tableOrdenes.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableOrdenes.getSelectionModel().getSelectedItem() != null) {
                abrirResultados(tableOrdenes.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void cargarOrdenesPendientes() {
        // Asegurar la recarga desde el archivo en disco
        ordenDAO = new OrdenDAO();
        ordenService = new OrdenService(ordenDAO);

        listaOrdenes = FXCollections.observableArrayList();
        for (Orden o : ordenDAO.findAll()) {
            if (o.getEstado() != com.acculab.models.EstadoOrden.ELIMINADA) {
                listaOrdenes.add(o);
            }
        }
        ordenesFiltradas = new FilteredList<>(listaOrdenes, p -> true);
        tableOrdenes.setItems(ordenesFiltradas);
    }

    @FXML
    private void eliminarOrden(ActionEvent event) {
        Orden seleccionada = tableOrdenes.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Seleccione una orden para eliminar.");
            a.showAndWait();
            return;
        }

        javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("Al eliminar esta orden, también se eliminarán todos sus registros financieros asociados.");
        confirm.setContentText("¿Está de acuerdo en eliminar la orden " + seleccionada.getId() + "?");
        if (confirm.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            ordenService.marcarComoEliminada(seleccionada);
            cargarOrdenesPendientes();
        }
    }

    @FXML
    private void cambiarEstadoOrden(ActionEvent event) {
        Orden seleccionada = tableOrdenes.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Seleccione una orden para cambiar su estado.");
            a.showAndWait();
            return;
        }

        java.util.List<com.acculab.models.EstadoOrden> estados = java.util.Arrays.asList(
            com.acculab.models.EstadoOrden.PENDIENTE,
            com.acculab.models.EstadoOrden.EN_PROCESO,
            com.acculab.models.EstadoOrden.FINALIZADA
        );
        
        javafx.scene.control.ChoiceDialog<com.acculab.models.EstadoOrden> dialog = new javafx.scene.control.ChoiceDialog<>(seleccionada.getEstado(), estados);
        dialog.setTitle("Cambiar Estado");
        dialog.setHeaderText("Modificar el estado de la Orden " + seleccionada.getId());
        dialog.setContentText("Seleccione el nuevo estado:");

        java.util.Optional<com.acculab.models.EstadoOrden> result = dialog.showAndWait();
        if (result.isPresent()) {
            seleccionada.setEstado(result.get());
            ordenDAO.update(seleccionada);
            cargarOrdenesPendientes();
        }
    }

    private void configurarBuscador() {
        txtBuscarOrden.textProperty().addListener((observable, oldValue, newValue) -> {
            ordenesFiltradas.setPredicate(orden -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return orden.getId().toLowerCase().contains(lower) || 
                       orden.getPaciente().toString().toLowerCase().contains(lower);
            });
        });
    }

    @FXML
    private void generarReportePDF(ActionEvent event) {
        if (ordenesFiltradas.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("No hay órdenes activas para reportar.");
            a.showAndWait();
            return;
        }

        try {
            String dest = "Reporte_Ordenes_Activas.pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titulo = new Paragraph("Reporte de Órdenes Activas", fontTitulo);
            titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            
            String[] cabeceras = {"Orden ID", "Paciente", "Médico", "Estado"};
            for (String cabecera : cabeceras) {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                header.setPhrase(new Phrase(cabecera, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                table.addCell(header);
            }

            for (Orden o : ordenesFiltradas) {
                table.addCell(o.getId());
                table.addCell(o.getPaciente().toString());
                table.addCell(o.getMedicoSolicitante());
                table.addCell(o.getEstado().toString());
            }

            document.add(table);
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Reporte generado con éxito: " + dest);
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error al generar el PDF.");
            alert.showAndWait();
        }
    }

    private void abrirResultados(Orden orden) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/resultados.fxml"));
            Parent root = loader.load();

            ResultadosController controller = loader.getController();
            controller.initData(orden, ordenService);

            Stage stage = new Stage();
            stage.setTitle("AccuLab - Resultados Orden: " + orden.getId());
            stage.setScene(new Scene(root, 900, 600));
            stage.showAndWait();
            
            // Recargar al cerrar la ventana
            cargarOrdenesPendientes();
            
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error al cargar la pantalla de resultados.");
            alert.showAndWait();
        }
    }
}
