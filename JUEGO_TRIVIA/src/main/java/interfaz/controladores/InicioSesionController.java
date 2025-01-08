package interfaz.controladores;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

public class InicioSesionController {

    @FXML
    private TextField nombreUsuarioField; // Campo para el email
    @FXML
    private PasswordField contrasenaField; // Campo para la contraseña

   @FXML
    private void iniciarSesion(ActionEvent event) {
        String email = nombreUsuarioField.getText().trim();
        String password = contrasenaField.getText().trim();

        // Verificar si los campos están vacíos
        if (email.isEmpty() || password.isEmpty()) {
            Database.FirebaseConexion.mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor, completa todos los campos antes de continuar.");
            return;
        }

        // Llamar al método de FirebaseConexion
        boolean autenticado = Database.FirebaseConexion.autenticarUsuario(email, password);

        // Cambiar a la siguiente vista si la autenticación fue exitosa
        if (autenticado) {
            

            try {
                // Cargar la nueva vista
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaz/vistas/ConfigurarPartida.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Pantalla principal");

                // Obtener el controlador de la nueva vista
                ConfigurarPartidaController configurarPartidaController = loader.getController();

                // Llamar al método setEmailUsuario en el controlador de la nueva vista
                configurarPartidaController.mostrarNombreUsuario();

            } catch (Exception e) {
                Database.FirebaseConexion.mostrarAlerta(Alert.AlertType.ERROR, "Error al cambiar de pantalla", "No se pudo cargar la siguiente pantalla.");
                e.printStackTrace();
            }
        }
    }
    // Método para abrir la vista de registro
    @FXML
    private void abrirRegistro(ActionEvent event) throws Exception {
        cambiarEscena(event, "/interfaz/vistas/Registro.fxml", "Registro");
    }

    // Método para cambiar de escena sin pasar datos adicionales
    private void cambiarEscena(ActionEvent event, String fxmlPath, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle(titulo);
    }
}
