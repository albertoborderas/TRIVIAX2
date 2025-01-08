package interfaz.controladores;

import Model.JugadorSession;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfigurarPartidaController {

  
    @FXML
    private Label mensajeBienvenidaLabel;  // Asegúrate de que este es el Label donde mostrarás el mensaje
    
public void mostrarNombreUsuario() {
    // Obtener el nombre de usuario directamente desde la sesión
    String nombreUsuario = JugadorSession.getJugadorActual().getNombreUsuario();
    
    // Verificar si el nombre de usuario está disponible
    if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
        mensajeBienvenidaLabel.setText("¡Bienvenido/a, " + nombreUsuario + "!");
    } else {
        // En caso de que no se haya obtenido el nombre de usuario, mostrar un mensaje por defecto
        mensajeBienvenidaLabel.setText("¡Bienvenido/a!");
    }
}

public void verRanking(){
  try {
             
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaz/vistas/ranking.fxml"));
            Parent root = loader.load();
            
           
            // Crear la escena para la nueva ventana (partida)
            Stage stage = new Stage();
            stage.setTitle("Ranking");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Cerrar la ventana actual (ConfigurarPartidaController)
           
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el FXML de la partida.");
        }
    }


    public void iniciarPartida() {
        try {
             mostrarNombreUsuario();
            // Cargar el archivo FXML y el controlador asociado
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaz/vistas/vistaPartida.fxml"));
            Parent root = loader.load();
            
           
            // Crear la escena para la nueva ventana (partida)
            Stage stage = new Stage();
            stage.setTitle("Partida");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Cerrar la ventana actual (ConfigurarPartidaController)
            Stage currentStage = (Stage) mensajeBienvenidaLabel.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el FXML de la partida.");
        }
    }
    
     public void irAEstadisticas() throws Exception {
        try {
            
            
            // Cargar el archivo FXML y el controlador asociado
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaz/vistas/EstadisticasPersonales.fxml"));
            Parent root = loader.load();
            EstadisticasPersonalesController estadisticasController = loader.getController();
           
          
            // Crear la escena para la nueva ventana (partida)
            Stage stage = new Stage();
             
            stage.setTitle("Estadísticas");
             estadisticasController.setEstadisticas();
            stage.setScene(new Scene(root));
            stage.show();
            
            

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el FXML de la partida.");
        }
    }
    
 

}

  

