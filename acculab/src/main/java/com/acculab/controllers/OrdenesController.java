package com.acculab.controllers;

import com.acculab.dao.OrdenDAO;
import com.acculab.dao.PacienteDAO;
import com.acculab.dao.PerfilDAO;
import com.acculab.dao.PruebaDAO;
import com.acculab.dao.MedicoDAO;
import com.acculab.models.Orden;
import com.acculab.models.Paciente;
import com.acculab.models.Perfil;
import com.acculab.models.Prueba;
import com.acculab.models.Medico;
import com.acculab.services.OrdenService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.UUID;

public class OrdenesController {

    @FXML private TextField txtBuscarPaciente;
    @FXML private ComboBox<Paciente> cbPacientes;
    @FXML private ComboBox<Medico> cbMedicos;
    
    @FXML private ComboBox<Prueba> cbPruebas;
    @FXML private Button btnAgregarPrueba;
    
    @FXML private ComboBox<Perfil> cbPerfiles;
    @FXML private Button btnAgregarPerfil;

    @FXML private TableView<Prueba> tablePruebasSeleccionadas;
    @FXML private TableColumn<Prueba, String> colNombrePrueba;
    
    @FXML private Label lblTotalPruebas;

    private PacienteDAO pacienteDAO;
    private PruebaDAO pruebaDAO;
    private PerfilDAO perfilDAO;
    private OrdenDAO ordenDAO;
    private MedicoDAO medicoDAO;
    private OrdenService ordenService;

    private ObservableList<Prueba> pruebasEnOrden;
    private ObservableList<Paciente> listaPacientes;
    private javafx.collections.transformation.FilteredList<Paciente> pacientesFiltrados;

    @FXML
    public void initialize() {
        pacienteDAO = new PacienteDAO();
        pruebaDAO = new PruebaDAO();
        perfilDAO = new PerfilDAO();
        ordenDAO = new OrdenDAO();
        medicoDAO = new MedicoDAO();
        ordenService = new OrdenService(ordenDAO);

        pruebasEnOrden = FXCollections.observableArrayList();
        
        cargarListas();
        configurarTabla();
        configurarBuscadorPaciente();
    }

    private void cargarListas() {
        listaPacientes = FXCollections.observableArrayList(pacienteDAO.findAllActivos());
        pacientesFiltrados = new javafx.collections.transformation.FilteredList<>(listaPacientes, p -> true);
        cbPacientes.setItems(pacientesFiltrados);
        
        cbPruebas.setItems(FXCollections.observableArrayList(pruebaDAO.findAll()));
        cbPerfiles.setItems(FXCollections.observableArrayList(perfilDAO.findAll()));
        cbMedicos.setItems(FXCollections.observableArrayList(medicoDAO.findAllActivos()));
    }

    private void configurarBuscadorPaciente() {
        txtBuscarPaciente.textProperty().addListener((obs, oldValue, newValue) -> {
            pacientesFiltrados.setPredicate(p -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return p.getNombres().toLowerCase().contains(lower) || p.getApellidos().toLowerCase().contains(lower);
            });
            if (!pacientesFiltrados.isEmpty()) {
                cbPacientes.show();
            }
        });
    }

    private void configurarTabla() {
        colNombrePrueba.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        tablePruebasSeleccionadas.setItems(pruebasEnOrden);
    }

    @FXML
    private void irACatalogo(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/pruebas.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = cbPruebas.getScene();
            if (scene != null && scene.getRoot() instanceof javafx.scene.layout.BorderPane) {
                ((javafx.scene.layout.BorderPane) scene.getRoot()).setCenter(root);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarPruebaAOrden(ActionEvent event) {
        Prueba pruebaSel = cbPruebas.getValue();
        if (pruebaSel != null) {
            if (!pruebasEnOrden.contains(pruebaSel)) {
                pruebasEnOrden.add(pruebaSel);
                actualizarContador();
            } else {
                mostrarAlertaError("La prueba ya está en la orden.");
            }
        } else {
            mostrarAlertaError("Seleccione una prueba del catálogo primero.");
        }
    }

    @FXML
    private void agregarPerfilAOrden(ActionEvent event) {
        Perfil perfilSel = cbPerfiles.getValue();
        if (perfilSel != null) {
            for (Prueba p : perfilSel.getPruebas()) {
                // Obtener la versión más reciente de la prueba con sus precios actuales
                Prueba pruebaFresca = pruebaDAO.findById(p.getId());
                Prueba pAInsertar = (pruebaFresca != null) ? pruebaFresca : p;
                
                if (!pruebasEnOrden.contains(pAInsertar)) {
                    pruebasEnOrden.add(pAInsertar);
                }
            }
            actualizarContador();
        } else {
            mostrarAlertaError("Seleccione un perfil del catálogo primero.");
        }
    }
    
    @FXML
    private void eliminarPruebaSeleccionada(ActionEvent event) {
        Prueba p = tablePruebasSeleccionadas.getSelectionModel().getSelectedItem();
        if (p != null) {
            pruebasEnOrden.remove(p);
            actualizarContador();
        }
    }

    private void actualizarContador() {
        lblTotalPruebas.setText("Total Pruebas: " + pruebasEnOrden.size());
    }

    private Orden ordenEditando = null;

    public void cargarOrdenParaEdicion(Orden orden) {
        this.ordenEditando = orden;
        cbPacientes.setValue(orden.getPaciente());
        cbPacientes.setDisable(true); // No se puede cambiar el paciente
        
        // Tratar de seleccionar el medico si coincide el nombre
        for(Medico m : cbMedicos.getItems()) {
            if(m.toString().equals(orden.getMedicoSolicitante())) {
                cbMedicos.setValue(m);
                break;
            }
        }
        
        pruebasEnOrden.setAll(orden.getPruebas());
        actualizarContador();
    }

    @FXML
    private void guardarOrden(ActionEvent event) {
        Paciente paciente = cbPacientes.getValue();
        Medico medicoObj = cbMedicos.getValue();
        String medico = (medicoObj != null) ? medicoObj.toString() : "";

        if (paciente == null || medico.isEmpty() || pruebasEnOrden.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Debe seleccionar un paciente, ingresar el médico y agregar al menos una prueba.");
            alert.showAndWait();
            return;
        }

        double costoTotal = 0.0;
        
        // Cumplimiento estricto del diagrama de secuencia_orden.png
        java.util.List<String> pruebasIds = pruebasEnOrden.stream().map(Prueba::getId).collect(java.util.stream.Collectors.toList());

        if (ordenEditando == null) {
            String id = "ORD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            Orden nuevaOrden = new Orden(id, paciente, medico);
            
            for (String pruebaId : pruebasIds) {
                Prueba p = pruebaDAO.findById(pruebaId);
                if (p != null) {
                    nuevaOrden.addPrueba(p);
                    costoTotal += p.getPrecio();
                }
            }
            nuevaOrden.setCostoTotal(costoTotal);
            ordenDAO.save(nuevaOrden);

            mostrarExito(id, costoTotal);
            limpiarFormulario();
        } else {
            ordenEditando.setMedicoSolicitante(medico);
            ordenEditando.getPruebas().clear();
            
            for (String pruebaId : pruebasIds) {
                Prueba p = pruebaDAO.findById(pruebaId);
                if (p != null) {
                    ordenEditando.addPrueba(p);
                    costoTotal += p.getPrecio();
                }
            }
            ordenEditando.setCostoTotal(costoTotal);
            ordenDAO.update(ordenEditando);

            mostrarExito(ordenEditando.getId(), costoTotal);
            
            // Si es un popup, cerrarlo
            javafx.scene.Scene scene = cbPruebas.getScene();
            if (scene != null && scene.getWindow() instanceof javafx.stage.Stage) {
                if (((javafx.stage.Stage)scene.getWindow()).getTitle() != null && ((javafx.stage.Stage)scene.getWindow()).getTitle().contains("Editar")) {
                    ((javafx.stage.Stage)scene.getWindow()).close();
                }
            }
        }
    }

    private void mostrarExito(String id, double costo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Orden N° %s guardada correctamente.\nCosto Total: $%.2f", id, costo));
        alert.showAndWait();
    }

    private void mostrarAlertaError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void limpiarFormulario() {
        cbPacientes.getSelectionModel().clearSelection();
        cbMedicos.getSelectionModel().clearSelection();
        cbPruebas.getSelectionModel().clearSelection();
        cbPerfiles.getSelectionModel().clearSelection();
        pruebasEnOrden.clear();
        actualizarContador();
    }
}
