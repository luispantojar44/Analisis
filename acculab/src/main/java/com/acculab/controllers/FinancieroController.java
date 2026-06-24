package com.acculab.controllers;

import com.acculab.dao.OrdenDAO;
import com.acculab.models.Orden;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FinancieroController {

    @FXML private TextField txtBuscarOrden;
    @FXML private TableView<Orden> tableOrdenes;
    @FXML private TableColumn<Orden, String> colId;
    @FXML private TableColumn<Orden, String> colPaciente;
    @FXML private TableColumn<Orden, String> colCosto;
    @FXML private TableColumn<Orden, String> colAbonado;
    @FXML private TableColumn<Orden, String> colSaldo;

    @FXML private Label lblOrdenSeleccionada;
    @FXML private Label lblCostoTotal;
    @FXML private Label lblAbonado;
    @FXML private Label lblSaldoDeudor;
    
    @FXML private TextField txtNuevoAbono;

    private OrdenDAO ordenDAO;
    private ObservableList<Orden> listaOrdenes;
    private FilteredList<Orden> ordenesFiltradas;
    private Orden ordenSeleccionada = null;

    @FXML
    public void initialize() {
        ordenDAO = new OrdenDAO();
        configurarTabla();
        cargarDatos();
        configurarBuscador();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colPaciente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPaciente().toString()));
        colCosto.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getCostoTotal())));
        colAbonado.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getAbono())));
        colSaldo.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getSaldoDeudor())));

        // Resaltado de filas en base al saldo deudor
        tableOrdenes.setRowFactory(tv -> new TableRow<Orden>() {
            @Override
            protected void updateItem(Orden orden, boolean empty) {
                super.updateItem(orden, empty);
                if (orden == null || empty) {
                    setStyle("");
                } else {
                    if (orden.getSaldoDeudor() <= 0) {
                        setStyle("-fx-background-color: #d4edda;"); // Verde claro
                    } else {
                        setStyle("-fx-background-color: #f8d7da;"); // Rojo claro
                    }
                }
            }
        });

        tableOrdenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarOrdenEnPanel(newSelection);
            }
        });
    }

    private void cargarDatos() {
        listaOrdenes = FXCollections.observableArrayList();
        for (Orden o : ordenDAO.findAll()) {
            if (o.getEstado() != com.acculab.models.EstadoOrden.ELIMINADA) {
                listaOrdenes.add(o);
            }
        }
        // Ordenar inicialmente: Saldo deudor > 0 primero, luego pagados.
        listaOrdenes.sort((o1, o2) -> {
            boolean deuda1 = o1.getSaldoDeudor() > 0;
            boolean deuda2 = o2.getSaldoDeudor() > 0;
            if (deuda1 && !deuda2) return -1;
            if (!deuda1 && deuda2) return 1;
            return o2.getFechaCreacion().compareTo(o1.getFechaCreacion()); // Más recientes primero
        });

        ordenesFiltradas = new FilteredList<>(listaOrdenes, p -> true);
        
        // Wrap en SortedList para permitir ordenar haciendo clic en las columnas
        javafx.collections.transformation.SortedList<Orden> ordenesOrdenadas = new javafx.collections.transformation.SortedList<>(ordenesFiltradas);
        ordenesOrdenadas.comparatorProperty().bind(tableOrdenes.comparatorProperty());
        
        tableOrdenes.setItems(ordenesOrdenadas);
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

    private void cargarOrdenEnPanel(Orden orden) {
        ordenSeleccionada = orden;
        lblOrdenSeleccionada.setText("Orden: " + orden.getId() + " - " + orden.getPaciente().toString());
        lblCostoTotal.setText(String.format("$%.2f", orden.getCostoTotal()));
        lblAbonado.setText(String.format("$%.2f", orden.getAbono()));
        lblSaldoDeudor.setText(String.format("$%.2f", orden.getSaldoDeudor()));
        txtNuevoAbono.clear();
    }

    @FXML
    private void registrarAbono(ActionEvent event) {
        if (ordenSeleccionada == null) {
            mostrarAlerta("Selección vacía", "Debe seleccionar una orden de la tabla primero.");
            return;
        }

        try {
            double nuevoAbono = Double.parseDouble(txtNuevoAbono.getText().trim());
            
            if (nuevoAbono <= 0) {
                mostrarAlerta("Valor Inválido", "El abono debe ser mayor a cero.");
                return;
            }

            if (nuevoAbono > ordenSeleccionada.getSaldoDeudor()) {
                mostrarAlerta("Monto Excedido", "El abono no puede ser mayor al saldo deudor actual.");
                return;
            }

            // Actualizar abono
            double abonoActualizado = ordenSeleccionada.getAbono() + nuevoAbono;
            ordenSeleccionada.setAbono(abonoActualizado);
            ordenDAO.update(ordenSeleccionada);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText(String.format("Abono de $%.2f registrado exitosamente.", nuevoAbono));
            alert.showAndWait();

            cargarOrdenEnPanel(ordenSeleccionada);
            tableOrdenes.refresh();

        } catch (NumberFormatException e) {
            mostrarAlerta("Valor Inválido", "Debe ingresar un monto numérico válido para el abono.");
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
