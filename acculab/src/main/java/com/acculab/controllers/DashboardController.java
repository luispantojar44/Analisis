package com.acculab.controllers;

import com.acculab.models.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML private BorderPane mainPane;
    @FXML private Label lblUsuarioActual;

    private Usuario usuarioActual;

    public void initData(Usuario usuario) {
        this.usuarioActual = usuario;
        lblUsuarioActual.setText(usuario.getNombre() + " | " + usuario.getRol());
        // Cargar la vista por defecto (Pacientes) al iniciar
        abrirModuloPacientes(null);
    }

    @FXML
    private void abrirModuloPacientes(ActionEvent event) {
        cargarVistaCentral("/fxml/pacientes.fxml");
    }

    @FXML
    private void abrirModuloOrdenes(ActionEvent event) {
        cargarVistaCentral("/fxml/ordenes.fxml");
    }

    @FXML
    private void abrirModuloLaboratorio(ActionEvent event) {
        cargarVistaCentral("/fxml/laboratorio_lista.fxml");
    }

    @FXML
    private void abrirModuloPruebas(ActionEvent event) {
        cargarVistaCentral("/fxml/pruebas.fxml");
    }
    
    @FXML
    private void abrirModuloMedicos(ActionEvent event) {
        cargarVistaCentral("/fxml/medicos.fxml");
    }
    
    @FXML
    private void abrirModuloUsuarios(ActionEvent event) {
        cargarVistaCentral("/fxml/usuarios.fxml");
    }

    @FXML
    private void abrirModuloFinanciero(ActionEvent event) {
        cargarVistaCentral("/fxml/financiero.fxml");
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setTitle("AccuLab - Iniciar Sesión");
            stage.setScene(new Scene(root, 400, 450));
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarVistaCentral(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent vistaModulo = loader.load();
            mainPane.setCenter(vistaModulo);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el módulo: " + fxmlFile);
        }
    }
}
