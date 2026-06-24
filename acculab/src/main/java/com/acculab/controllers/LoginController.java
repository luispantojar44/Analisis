package com.acculab.controllers;

import com.acculab.models.Usuario;
import com.acculab.models.Orden;
import com.acculab.models.Paciente;
import com.acculab.models.Prueba;
import com.acculab.services.AuthService;
import com.acculab.services.OrdenService;
import com.acculab.dao.OrdenDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.time.LocalDate;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor, ingrese sus credenciales.");
            return;
        }

        Usuario usuario = authService.login(username, password);

        if (usuario != null) {
            lblError.setText("");
            abrirPantallaPrincipal(usuario);
        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
        }
    }

    private void abrirPantallaPrincipal(Usuario usuario) {
        try {
            // Cargar la vista principal (Dashboard)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            // Inyectar usuario al DashboardController
            DashboardController controller = loader.getController();
            controller.initData(usuario);

            // Cambiar Escena
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setTitle("AccuLab - Dashboard");
            stage.setScene(new Scene(root, 1000, 700));
            stage.setResizable(true);
            stage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Error al cargar el Dashboard principal.");
        }
    }
}
