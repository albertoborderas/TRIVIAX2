/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz.controladores;



import Database.FirebaseConexion;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class RegistroController {
    @FXML
    private TextField emailField;

    @FXML
    private TextField nickField;
    @FXML
    private PasswordField contrasenaField;

    @FXML
    private PasswordField confirmarContrasenaField;

@FXML
private void registrarUsuario() {
    // Get user input
    String email = emailField.getText(); // Use email as username
    String field = nickField.getText();
    String contrasena = contrasenaField.getText();
    String confirmarContrasena = confirmarContrasenaField.getText();

    // Validate if fields are empty
    if (email.isEmpty() || field.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
        showAlert("Error", "Todos los campos deben ser completados.", AlertType.ERROR);
        return;
    }

    // Validate passwords match
    if (!contrasena.equals(confirmarContrasena)) {
        showAlert("Error", "Las contraseñas no coinciden.", AlertType.ERROR);
        return;
    }

    // Register user with Firebase Authentication (using FirebaseInit)
    FirebaseConexion.registrarUsuarioEnFirebaseAuth(email, contrasena, field);
}


    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método para volver a la pantalla de inicio de sesión
    @FXML
    private void volverInicioSesion(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaz/vistas/InicioSesion.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Inicio de Sesión");
    }
    
    
    
    
    
    
    
    
    
}