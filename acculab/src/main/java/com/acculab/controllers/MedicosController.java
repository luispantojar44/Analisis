package com.acculab.controllers;

import com.acculab.dao.MedicoDAO;
import com.acculab.models.Medico;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.UUID;

public class MedicosController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEspecialidad;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Medico> tableMedicos;
    @FXML private TableColumn<Medico, String> colNombres;
    @FXML private TableColumn<Medico, String> colApellidos;
    @FXML private TableColumn<Medico, String> colEspecialidad;
    @FXML private TableColumn<Medico, String> colTelefono;
    @FXML private TableColumn<Medico, String> colCorreo;

    private MedicoDAO medicoDAO;
    private ObservableList<Medico> listaMedicos;
    private FilteredList<Medico> listaFiltrada;

    private Medico medicoSeleccionado = null;

    @FXML
    public void initialize() {
        medicoDAO = new MedicoDAO();
        configurarTabla();
        cargarDatos();
        configurarBuscador();
    }

    private void configurarTabla() {
        colNombres.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombres()));
        colApellidos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellidos()));
        colEspecialidad.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEspecialidad()));
        colTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono() != null ? cellData.getValue().getTelefono() : ""));
        colCorreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorreo() != null ? cellData.getValue().getCorreo() : ""));

        tableMedicos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarMedicoEnFormulario(newSelection);
            }
        });
    }

    private void cargarDatos() {
        listaMedicos = FXCollections.observableArrayList(medicoDAO.findAllActivos());
        listaFiltrada = new FilteredList<>(listaMedicos, p -> true);
        tableMedicos.setItems(listaFiltrada);
    }

    private void configurarBuscador() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(medico -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (medico.getNombres().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (medico.getApellidos().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }

    private void cargarMedicoEnFormulario(Medico medico) {
        medicoSeleccionado = medico;
        txtNombres.setText(medico.getNombres());
        txtApellidos.setText(medico.getApellidos());
        txtEspecialidad.setText(medico.getEspecialidad());
        txtTelefono.setText(medico.getTelefono() != null ? medico.getTelefono() : "");
        txtCorreo.setText(medico.getCorreo() != null ? medico.getCorreo() : "");
    }

    @FXML
    private void guardarMedico(ActionEvent event) {
        if (txtNombres.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, complete los campos obligatorios.");
            return;
        }

        if (medicoSeleccionado == null) {
            String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Medico nuevoMedico = new Medico(id, txtNombres.getText().trim(), txtApellidos.getText().trim(), txtEspecialidad.getText().trim(), txtTelefono.getText().trim(), txtCorreo.getText().trim());
            medicoDAO.save(nuevoMedico);
        } else {
            medicoSeleccionado.setNombres(txtNombres.getText().trim());
            medicoSeleccionado.setApellidos(txtApellidos.getText().trim());
            medicoSeleccionado.setEspecialidad(txtEspecialidad.getText().trim());
            medicoSeleccionado.setTelefono(txtTelefono.getText().trim());
            medicoSeleccionado.setCorreo(txtCorreo.getText().trim());
            medicoDAO.update(medicoSeleccionado);
        }

        limpiarFormulario(null);
        cargarDatos();
    }

    @FXML
    private void eliminarMedico(ActionEvent event) {
        if (medicoSeleccionado != null) {
            medicoSeleccionado.setEliminado(true);
            medicoDAO.update(medicoSeleccionado);
            limpiarFormulario(null);
            cargarDatos();
        } else {
            mostrarAlerta("Selección vacía", "Debe seleccionar un médico de la tabla para eliminarlo.");
        }
    }

    @FXML
    private void limpiarFormulario(ActionEvent event) {
        medicoSeleccionado = null;
        txtNombres.clear();
        txtApellidos.clear();
        txtEspecialidad.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        tableMedicos.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
