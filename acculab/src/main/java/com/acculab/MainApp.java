package com.acculab;

import com.acculab.controllers.LoginController;
import com.acculab.dao.UsuarioDAO;
import com.acculab.models.Usuario;
import com.acculab.services.AuthService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Inicializar DAO y Servicio de Autenticación
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        AuthService authService = new AuthService(usuarioDAO);

        // Crear un usuario administrador por defecto si no existe ninguno
        if (usuarioDAO.findAll().isEmpty()) {
            Usuario admin = new Usuario("US-01", "Administrador Principal", "admin", "", Usuario.Rol.ADMINISTRADOR);
            authService.registrarUsuario(admin, "admin123");
            System.out.println("Usuario por defecto creado. Credenciales: admin / admin123");
        }

        // Generar Catálogo Base si no existe
        com.acculab.dao.PruebaDAO pruebaDAO = new com.acculab.dao.PruebaDAO();
        com.acculab.dao.PerfilDAO perfilDAO = new com.acculab.dao.PerfilDAO();
        
        if (pruebaDAO.findAll().isEmpty()) {
            com.acculab.models.Prueba glucosa = new com.acculab.models.Prueba("PR-01", "Glucosa", "mg/dL", 70.0, 100.0, 70.0, 100.0, 15.00);
            com.acculab.models.Prueba creatinina = new com.acculab.models.Prueba("PR-02", "Creatinina", "mg/dL", 0.7, 1.3, 0.6, 1.1, 12.50);
            com.acculab.models.Prueba colesterol = new com.acculab.models.Prueba("PR-03", "Colesterol Total", "mg/dL", 0.0, 200.0, 0.0, 200.0, 18.00);
            
            pruebaDAO.save(glucosa);
            pruebaDAO.save(creatinina);
            pruebaDAO.save(colesterol);
            
            com.acculab.models.Perfil perfilBasico = new com.acculab.models.Perfil("PF-01", "Química Sanguínea Básica");
            perfilBasico.addPrueba(glucosa);
            perfilBasico.addPrueba(creatinina);
            perfilBasico.addPrueba(colesterol);
            
            perfilDAO.save(perfilBasico);
            System.out.println("Catálogo de Pruebas autogenerado exitosamente.");
        }

        // Cargar la vista de Login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Inyectar dependencias al controlador
        LoginController controller = loader.getController();
        controller.setAuthService(authService);

        primaryStage.setTitle("AccuLab - Iniciar Sesión");
        primaryStage.setScene(new Scene(root, 400, 450));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
