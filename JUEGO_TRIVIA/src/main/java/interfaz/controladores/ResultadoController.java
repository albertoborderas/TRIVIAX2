package interfaz.controladores;

import Database.FirebaseConexion;
import Model.JugadorSession;
import java.io.IOException;
import java.net.MalformedURLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ResultadoController {

    @FXML private Label labelResultado;
    @FXML private Label labelCorrectas;
     @FXML private Label labelCom50;
       @FXML private Label labelComCambio;
 



    // Método para recibir los resultados desde la vista anterior
    public void setResultados(int respuestasCorrectas, boolean com50, boolean comCambiar) throws MalformedURLException, IOException {
        
          
        // Mostrar los resultados
        labelResultado.setText("Resultado Final");
        labelCorrectas.setText("Respuestas Correctas: " + respuestasCorrectas);
        
        if(respuestasCorrectas==15){
            FirebaseConexion.incrementarPartidasGanadas(JugadorSession.getJugadorActual().getId());
        }
        
        if(com50==true){
            labelCom50.setText("50/50: No Usado");
        }
        else{
              labelCom50.setText("50/50: Usado");
        }
        
           if(comCambiar==true){
            labelComCambio.setText("Cambiar pregunta:  No usado");
        }
        else{
              labelComCambio.setText("Cambiar pregunta: Usado");
        }
      
    }

    // Acción para jugar de nuevo
    @FXML
    private void jugarDeNuevo(ActionEvent event) throws IOException, Exception {
   FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaz/vistas/vistaPartida.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
       
        stage.setScene(scene);
           stage.centerOnScreen();
        stage.setTitle("Partida");
    }

@FXML
private void volverAlMenu(ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaz/vistas/ConfigurarPartida.fxml"));
    Parent root = loader.load();
      // Obtener el controlador de la nueva pantalla
            ConfigurarPartidaController controller = loader.getController();
            // Pasar el email al controlador de la nueva pantalla
            controller.mostrarNombreUsuario();

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(new Scene(root));
             stage.centerOnScreen();
        stage.setTitle("Menú principal");
    stage.show();
}


}
