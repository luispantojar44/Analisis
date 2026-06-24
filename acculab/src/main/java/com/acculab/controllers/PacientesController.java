package com.acculab.controllers;

import com.acculab.dao.PacienteDAO;
import com.acculab.models.Paciente;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.UUID;

public class PacientesController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private ComboBox<Paciente.Sexo> cbSexo;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Paciente> tablePacientes;
    @FXML private TableColumn<Paciente, String> colNombres;
    @FXML private TableColumn<Paciente, String> colApellidos;
    @FXML private TableColumn<Paciente, String> colTelefono;
    @FXML private TableColumn<Paciente, String> colCorreo;
    @FXML private TableColumn<Paciente, String> colFechaNac;
    @FXML private TableColumn<Paciente, String> colSexo;

    private PacienteDAO pacienteDAO;
    private ObservableList<Paciente> listaPacientes;
    private FilteredList<Paciente> listaFiltrada;

    private Paciente pacienteSeleccionado = null;

    @FXML
    public void initialize() {
        pacienteDAO = new PacienteDAO();
        cbSexo.getItems().setAll(Paciente.Sexo.values());

        configurarTabla();
        cargarDatos();
        configurarBuscador();
    }

    private void configurarTabla() {
        colNombres.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombres()));
        colApellidos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellidos()));
        colTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono() != null ? cellData.getValue().getTelefono() : ""));
        colCorreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorreo() != null ? cellData.getValue().getCorreo() : ""));
        colFechaNac.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaNacimiento().toString()));
        colSexo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSexo().toString()));

        tablePacientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarPacienteEnFormulario(newSelection);
            }
        });
    }

    private void cargarDatos() {
        listaPacientes = FXCollections.observableArrayList(pacienteDAO.findAllActivos());
        listaFiltrada = new FilteredList<>(listaPacientes, p -> true);
        tablePacientes.setItems(listaFiltrada);
    }

    private void configurarBuscador() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(paciente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (paciente.getNombres().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (paciente.getApellidos().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }

    private void cargarPacienteEnFormulario(Paciente paciente) {
        pacienteSeleccionado = paciente;
        txtNombres.setText(paciente.getNombres());
        txtApellidos.setText(paciente.getApellidos());
        txtTelefono.setText(paciente.getTelefono() != null ? paciente.getTelefono() : "");
        txtCorreo.setText(paciente.getCorreo() != null ? paciente.getCorreo() : "");
        dpFechaNacimiento.setValue(paciente.getFechaNacimiento());
        cbSexo.setValue(paciente.getSexo());
    }

    @FXML
    private void guardarPaciente(ActionEvent event) {
        if (txtNombres.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty() || dpFechaNacimiento.getValue() == null || cbSexo.getValue() == null) {
            mostrarAlerta("Campos vacíos", "Por favor, complete los campos obligatorios.");
            return;
        }
        
        if (dpFechaNacimiento.getValue().isAfter(LocalDate.now())) {
            mostrarAlerta("Fecha Inválida", "La fecha de nacimiento no puede ser en el futuro.");
            return;
        }

        if (pacienteSeleccionado == null) {
            // Nuevo paciente
            String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Paciente nuevoPaciente = new Paciente(id, txtNombres.getText().trim(), txtApellidos.getText().trim(), dpFechaNacimiento.getValue(), cbSexo.getValue(), txtTelefono.getText().trim(), txtCorreo.getText().trim());
            pacienteDAO.save(nuevoPaciente);
        } else {
            // Actualizar paciente existente
            pacienteSeleccionado.setNombres(txtNombres.getText().trim());
            pacienteSeleccionado.setApellidos(txtApellidos.getText().trim());
            pacienteSeleccionado.setTelefono(txtTelefono.getText().trim());
            pacienteSeleccionado.setCorreo(txtCorreo.getText().trim());
            pacienteSeleccionado.setFechaNacimiento(dpFechaNacimiento.getValue());
            pacienteSeleccionado.setSexo(cbSexo.getValue());
            pacienteDAO.update(pacienteSeleccionado);
        }

        limpiarFormulario(null);
        cargarDatos();
    }

    @FXML
    private void eliminarPaciente(ActionEvent event) {
        if (pacienteSeleccionado != null) {
            // Soft delete
            pacienteSeleccionado.setEliminado(true);
            pacienteDAO.update(pacienteSeleccionado);
            limpiarFormulario(null);
            cargarDatos();
        } else {
            mostrarAlerta("Selección vacía", "Debe seleccionar un paciente de la tabla para eliminarlo.");
        }
    }

    @FXML
    private void limpiarFormulario(ActionEvent event) {
        pacienteSeleccionado = null;
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        dpFechaNacimiento.setValue(null);
        cbSexo.setValue(null);
        tablePacientes.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
