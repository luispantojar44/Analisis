package com.acculab.controllers;

import com.acculab.dao.PruebaDAO;
import com.acculab.models.Prueba;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.UUID;

public class PruebasController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtUnidad;
    @FXML private TextField txtMinM;
    @FXML private TextField txtMaxM;
    @FXML private TextField txtMinF;
    @FXML private TextField txtMaxF;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Prueba> tablePruebas;
    @FXML private TableColumn<Prueba, String> colNombre;
    @FXML private TableColumn<Prueba, String> colUnidad;
    @FXML private TableColumn<Prueba, String> colRefM;
    @FXML private TableColumn<Prueba, String> colRefF;
    @FXML private TableColumn<Prueba, String> colPrecio;

    // ----- ELEMENTOS PESTAÑA PERFILES -----
    @FXML private TextField txtNombrePerfil;
    @FXML private ComboBox<Prueba> cbPruebasParaPerfil;
    @FXML private TableView<Prueba> tablePruebasEnPerfil;
    @FXML private TableColumn<Prueba, String> colNombrePruebaPerfil;
    @FXML private TableView<com.acculab.models.Perfil> tablePerfiles;
    @FXML private TableColumn<com.acculab.models.Perfil, String> colNombrePerfil;

    private PruebaDAO pruebaDAO;
    private com.acculab.dao.PerfilDAO perfilDAO;
    
    private ObservableList<Prueba> listaPruebas;
    private FilteredList<Prueba> listaFiltrada;
    private Prueba pruebaSeleccionada = null;

    private ObservableList<com.acculab.models.Perfil> listaPerfiles;
    private ObservableList<Prueba> pruebasDelPerfilActual;
    private com.acculab.models.Perfil perfilSeleccionado = null;

    @FXML
    public void initialize() {
        pruebaDAO = new PruebaDAO();
        perfilDAO = new com.acculab.dao.PerfilDAO();
        
        pruebasDelPerfilActual = FXCollections.observableArrayList();
        
        configurarTabla();
        configurarTablaPerfiles();
        cargarDatos();
        configurarBuscador();
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colUnidad.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUnidad()));
        colRefM.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRefMinMasculino() + " - " + cellData.getValue().getRefMaxMasculino()));
        colRefF.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRefMinFemenino() + " - " + cellData.getValue().getRefMaxFemenino()));
        colPrecio.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getPrecio())));

        tablePruebas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarFormulario(newSelection);
            }
        });
    }

    private void configurarTablaPerfiles() {
        colNombrePerfil.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colNombrePruebaPerfil.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        
        tablePruebasEnPerfil.setItems(pruebasDelPerfilActual);
        
        tablePerfiles.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarFormularioPerfil(newSelection);
            }
        });
    }

    private void cargarDatos() {
        listaPruebas = FXCollections.observableArrayList(pruebaDAO.findAll());
        listaFiltrada = new FilteredList<>(listaPruebas, p -> true);
        tablePruebas.setItems(listaFiltrada);
        
        cbPruebasParaPerfil.setItems(listaPruebas);
        
        listaPerfiles = FXCollections.observableArrayList(perfilDAO.findAll());
        tablePerfiles.setItems(listaPerfiles);
    }

    private void configurarBuscador() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(prueba -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return prueba.getNombre().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }

    private void cargarFormulario(Prueba p) {
        pruebaSeleccionada = p;
        if (txtCodigo != null) {
            txtCodigo.setText(p.getId());
            txtCodigo.setDisable(true); // El código no se edita después de creado
        }
        txtNombre.setText(p.getNombre());
        txtUnidad.setText(p.getUnidad());
        txtMinM.setText(String.valueOf(p.getRefMinMasculino()));
        txtMaxM.setText(String.valueOf(p.getRefMaxMasculino()));
        txtMinF.setText(String.valueOf(p.getRefMinFemenino()));
        txtMaxF.setText(String.valueOf(p.getRefMaxFemenino()));
        txtPrecio.setText(String.valueOf(p.getPrecio()));
    }

    private void cargarFormularioPerfil(com.acculab.models.Perfil p) {
        perfilSeleccionado = p;
        txtNombrePerfil.setText(p.getNombre());
        pruebasDelPerfilActual.setAll(p.getPruebas());
    }

    @FXML
    private void guardarPrueba(ActionEvent event) {
        try {
            if (txtCodigo != null && txtCodigo.getText().trim().isEmpty()) {
                mostrarAlerta("Campos vacíos", "El código es obligatorio.");
                return;
            }

            if (txtNombre.getText().trim().isEmpty() || txtUnidad.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty()) {
                mostrarAlerta("Campos vacíos", "El nombre, unidad y precio son obligatorios.");
                return;
            }

            double minM = Double.parseDouble(txtMinM.getText().trim());
            double maxM = Double.parseDouble(txtMaxM.getText().trim());
            double minF = Double.parseDouble(txtMinF.getText().trim());
            double maxF = Double.parseDouble(txtMaxF.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim());

            if (pruebaSeleccionada == null) {
                String codigo = (txtCodigo != null) ? txtCodigo.getText().trim() : ("PR-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
                
                // Buscar por código exacto como dice la secuencia
                if (pruebaDAO.findById(codigo) != null) {
                    mostrarAlerta("Código Duplicado", "Ya existe una prueba con el código: " + codigo);
                    return;
                }
                
                Prueba nuevaPrueba = new Prueba(codigo, txtNombre.getText().trim(), txtUnidad.getText().trim(), minM, maxM, minF, maxF, precio);
                pruebaDAO.save(nuevaPrueba);
            } else {
                pruebaSeleccionada.setNombre(txtNombre.getText().trim());
                pruebaSeleccionada.setUnidad(txtUnidad.getText().trim());
                pruebaSeleccionada.setRefMinMasculino(minM);
                pruebaSeleccionada.setRefMaxMasculino(maxM);
                pruebaSeleccionada.setRefMinFemenino(minF);
                pruebaSeleccionada.setRefMaxFemenino(maxF);
                pruebaSeleccionada.setPrecio(precio);
                pruebaDAO.update(pruebaSeleccionada);
            }

            limpiarFormulario(null);
            cargarDatos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Valores Inválidos", "Los rangos numéricos y precio deben ser válidos.");
        }
    }

    @FXML
    private void eliminarPrueba(ActionEvent event) {
        if (pruebaSeleccionada != null) {
            pruebaDAO.delete(pruebaSeleccionada.getId());
            limpiarFormulario(null);
            cargarDatos();
        }
    }

    @FXML
    private void limpiarFormulario(ActionEvent event) {
        pruebaSeleccionada = null;
        if (txtCodigo != null) {
            txtCodigo.clear();
            txtCodigo.setDisable(false);
        }
        txtNombre.clear();
        txtUnidad.clear();
        txtMinM.clear();
        txtMaxM.clear();
        txtMinF.clear();
        txtMaxF.clear();
        txtPrecio.clear();
        tablePruebas.getSelectionModel().clearSelection();
    }

    // ----- METODOS PERFILES -----
    @FXML
    private void agregarPruebaAPerfil(ActionEvent event) {
        Prueba p = cbPruebasParaPerfil.getValue();
        if (p != null && !pruebasDelPerfilActual.contains(p)) {
            pruebasDelPerfilActual.add(p);
        }
    }

    @FXML
    private void quitarPruebaDePerfil(ActionEvent event) {
        Prueba p = tablePruebasEnPerfil.getSelectionModel().getSelectedItem();
        if (p != null) {
            pruebasDelPerfilActual.remove(p);
        }
    }

    @FXML
    private void guardarPerfil(ActionEvent event) {
        if (txtNombrePerfil.getText().trim().isEmpty() || pruebasDelPerfilActual.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debe ingresar un nombre y añadir al menos una prueba.");
            return;
        }

        if (perfilSeleccionado == null) {
            String id = "PF-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            com.acculab.models.Perfil nuevoPerfil = new com.acculab.models.Perfil(id, txtNombrePerfil.getText().trim());
            nuevoPerfil.setPruebas(new java.util.ArrayList<>(pruebasDelPerfilActual));
            perfilDAO.save(nuevoPerfil);
        } else {
            perfilSeleccionado.setNombre(txtNombrePerfil.getText().trim());
            perfilSeleccionado.setPruebas(new java.util.ArrayList<>(pruebasDelPerfilActual));
            perfilDAO.update(perfilSeleccionado);
        }

        limpiarFormularioPerfil(null);
        cargarDatos();
    }

    @FXML
    private void eliminarPerfil(ActionEvent event) {
        if (perfilSeleccionado != null) {
            perfilDAO.delete(perfilSeleccionado.getId());
            limpiarFormularioPerfil(null);
            cargarDatos();
        }
    }

    @FXML
    private void limpiarFormularioPerfil(ActionEvent event) {
        perfilSeleccionado = null;
        txtNombrePerfil.clear();
        pruebasDelPerfilActual.clear();
        cbPruebasParaPerfil.getSelectionModel().clearSelection();
        tablePerfiles.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
