package com.acculab.controllers;

import com.acculab.models.Orden;
import com.acculab.models.Paciente;
import com.acculab.models.Prueba;
import com.acculab.services.OrdenService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

public class ResultadosController {

    @FXML private Label lblPaciente;
    @FXML private Label lblOrden;
    @FXML private TableView<PruebaWrapper> tableResultados;
    @FXML private TableColumn<PruebaWrapper, String> colPrueba;
    @FXML private TableColumn<PruebaWrapper, String> colReferencia;
    @FXML private TableColumn<PruebaWrapper, String> colResultado;
    @FXML private TableColumn<PruebaWrapper, String> colAlerta;

    private Orden ordenActual;
    private OrdenService ordenService;
    private ObservableList<PruebaWrapper> datosTabla;

    public void initData(Orden orden, OrdenService ordenService) {
        this.ordenActual = orden;
        this.ordenService = ordenService;
        
        lblPaciente.setText("Paciente: " + orden.getPaciente().toString());
        lblOrden.setText("Orden N°: " + orden.getId());
        
        cargarPruebas();
    }

    private void cargarPruebas() {
        datosTabla = FXCollections.observableArrayList();
        for (Prueba prueba : ordenActual.getPruebas()) {
            datosTabla.add(new PruebaWrapper(prueba, ordenActual.getPaciente()));
        }
        
        colPrueba.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().prueba.getNombre()));
        colReferencia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().rangoReferencia));
        
        colResultado.setCellValueFactory(cellData -> cellData.getValue().valorProperty);
        colResultado.setCellFactory(TextFieldTableCell.forTableColumn());
        colResultado.setOnEditCommit(event -> {
            PruebaWrapper wrapper = event.getRowValue();
            wrapper.setValor(event.getNewValue());
            evaluarRango(wrapper);
        });

        colAlerta.setCellValueFactory(cellData -> cellData.getValue().alertaProperty);
        
        // Colorear fila si está fuera de rango
        colAlerta.setCellFactory(column -> {
            return new TableCell<PruebaWrapper, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if (item.contains("FUERA")) {
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: green;");
                        }
                    }
                }
            };
        });

        tableResultados.setItems(datosTabla);
        tableResultados.setEditable(true);
    }

    private void evaluarRango(PruebaWrapper wrapper) {
        try {
            double valor = Double.parseDouble(wrapper.valorProperty.get());
            boolean fueraDeRango = false;
            Prueba p = wrapper.prueba;
            
            if (ordenActual.getPaciente().getSexo() == Paciente.Sexo.MASCULINO) {
                if (valor < p.getRefMinMasculino() || valor > p.getRefMaxMasculino()) fueraDeRango = true;
            } else {
                if (valor < p.getRefMinFemenino() || valor > p.getRefMaxFemenino()) fueraDeRango = true;
            }
            
            if (fueraDeRango) {
                wrapper.alertaProperty.set("FUERA DE RANGO");
            } else {
                wrapper.alertaProperty.set("NORMAL");
            }
            
            // Guardar a través del servicio
            ordenService.capturarResultado(ordenActual, p, valor, ordenActual.getPaciente());
            
        } catch (NumberFormatException e) {
            wrapper.alertaProperty.set("VALOR INVÁLIDO");
        }
    }

    @FXML
    private void guardarResultados(ActionEvent event) {
        try {
            mostrarAlertaExito("Resultados guardados correctamente.");
            javafx.stage.Stage stage = (javafx.stage.Stage) tableResultados.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error al guardar los resultados: " + e.getMessage());
        }
    }

    @FXML
    private void generarPDFMembretado(ActionEvent event) {
        generarPDF(new com.acculab.services.ReporteMembretadoStrategy());
    }

    @FXML
    private void generarPDFSinMembrete(ActionEvent event) {
        generarPDF(new com.acculab.services.ReporteSinMembreteStrategy());
    }

    @FXML
    private void generarPDFConvenio(ActionEvent event) {
        generarPDF(new com.acculab.services.ReporteConvenioStrategy());
    }

    private void generarPDF(com.acculab.services.ReportStrategy strategy) {
        try {
            // Guardar en la carpeta actual del proyecto
            java.io.File file = strategy.generarReporte(ordenActual, ".");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText("Reporte PDF generado exitosamente en: " + file.getAbsolutePath());
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Ocurrió un error al generar el PDF: " + e.getMessage());
        }
    }

    @FXML
    private void modificarOrden(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/ordenes.fxml"));
            javafx.scene.Parent root = loader.load();
            
            OrdenesController controller = loader.getController();
            controller.cargarOrdenParaEdicion(ordenActual);
            
            javafx.stage.Stage editStage = new javafx.stage.Stage();
            editStage.setTitle("Editar Orden - " + ordenActual.getId());
            editStage.setScene(new javafx.scene.Scene(root, 750, 700));
            
            // Mostrar ventana de edición y esperar
            editStage.showAndWait();
            
            // Cerrar ventana actual de Resultados DESPUÉS de la edición
            // para permitir que LaboratorioListaController recargue la tabla actualizada
            javafx.stage.Stage currentStage = (javafx.stage.Stage) tableResultados.getScene().getWindow();
            currentStage.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error al abrir la edición de la orden: " + e.getMessage());
        }
    }

    private void mostrarAlertaExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Clase auxiliar para manejar el estado de la tabla
    public static class PruebaWrapper {
        Prueba prueba;
        String rangoReferencia;
        SimpleStringProperty valorProperty;
        SimpleStringProperty alertaProperty;

        public PruebaWrapper(Prueba prueba, Paciente paciente) {
            this.prueba = prueba;
            if (paciente.getSexo() == Paciente.Sexo.MASCULINO) {
                this.rangoReferencia = prueba.getRefMinMasculino() + " - " + prueba.getRefMaxMasculino();
            } else {
                this.rangoReferencia = prueba.getRefMinFemenino() + " - " + prueba.getRefMaxFemenino();
            }
            this.valorProperty = new SimpleStringProperty("");
            this.alertaProperty = new SimpleStringProperty("");
        }

        public void setValor(String valor) { this.valorProperty.set(valor); }
    }
}
