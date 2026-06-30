package com.acculab.controllers;

import com.acculab.dao.UsuarioDAO;
import com.acculab.models.Usuario;
import com.acculab.models.Usuario.Rol;
import org.mindrot.jbcrypt.BCrypt;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.UUID;

public class UsuariosController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<Rol> cbRol;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colEstado;

    private UsuarioDAO usuarioDAO;
    private ObservableList<Usuario> listaUsuarios;
    private FilteredList<Usuario> listaFiltrada;

    private Usuario usuarioSeleccionado = null;

    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        cbRol.getItems().setAll(Rol.values());
        
        configurarTabla();
        cargarDatos();
        configurarBuscador();
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colRol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRol().toString()));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isActivo() ? "SI" : "NO"));

        tableUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarUsuarioEnFormulario(newSelection);
            }
        });
    }

    private void cargarDatos() {
        listaUsuarios = FXCollections.observableArrayList(usuarioDAO.findAll());
        listaFiltrada = new FilteredList<>(listaUsuarios, p -> true);
        tableUsuarios.setItems(listaFiltrada);
    }

    private void configurarBuscador() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (user.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }

    private void cargarUsuarioEnFormulario(Usuario user) {
        usuarioSeleccionado = user;
        txtNombre.setText(user.getNombre());
        txtUsername.setText(user.getUsername());
        txtPassword.setText(""); // No mostrar password
        cbRol.setValue(user.getRol());
    }

    @FXML
    private void guardarUsuario(ActionEvent event) {
        if (txtNombre.getText().trim().isEmpty() || txtUsername.getText().trim().isEmpty() || cbRol.getValue() == null) {
            mostrarAlerta("Campos vacíos", "Por favor, complete los campos obligatorios.");
            return;
        }

        if (usuarioSeleccionado == null) {
            if (txtPassword.getText().trim().isEmpty()) {
                mostrarAlerta("Campos vacíos", "Debe ingresar una contraseña para el nuevo usuario.");
                return;
            }
            String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String hashed = BCrypt.hashpw(txtPassword.getText().trim(), BCrypt.gensalt());
            Usuario nuevoUsuario = new Usuario(id, txtNombre.getText().trim(), txtUsername.getText().trim(), hashed, cbRol.getValue());
            usuarioDAO.save(nuevoUsuario);
        } else {
            usuarioSeleccionado.setNombre(txtNombre.getText().trim());
            usuarioSeleccionado.setUsername(txtUsername.getText().trim());
            usuarioSeleccionado.setRol(cbRol.getValue());
            if (!txtPassword.getText().trim().isEmpty()) {
                String hashed = BCrypt.hashpw(txtPassword.getText().trim(), BCrypt.gensalt());
                usuarioSeleccionado.setHashPassword(hashed);
            }
            usuarioDAO.update(usuarioSeleccionado);
        }

        limpiarFormulario(null);
        cargarDatos();
    }

    @FXML
    private void eliminarUsuario(ActionEvent event) {
        if (usuarioSeleccionado != null) {
            if(usuarioSeleccionado.getUsername().equals("admin")) {
                mostrarAlerta("Operación denegada", "No puede desactivar al usuario admin principal.");
                return;
            }
            usuarioSeleccionado.setActivo(!usuarioSeleccionado.isActivo());
            usuarioDAO.update(usuarioSeleccionado);
            limpiarFormulario(null);
            cargarDatos();
        } else {
            mostrarAlerta("Selección vacía", "Debe seleccionar un usuario.");
        }
    }

    @FXML
    private void limpiarFormulario(ActionEvent event) {
        usuarioSeleccionado = null;
        txtNombre.clear();
        txtUsername.clear();
        txtPassword.clear();
        cbRol.setValue(null);
        tableUsuarios.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
