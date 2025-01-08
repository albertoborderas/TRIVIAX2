package interfaz.controladores;

import Database.FirebaseConexion;
import Model.JugadorSession;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class EstadisticasPersonalesController {

   
   @FXML private Label partidasJugadasLabel;
   @FXML  private Label partidasGanadasLabel;
     @FXML  private Label nombreLabel;
      @FXML  private Label porcentajeAciertosLabel;
       @FXML  private Label comodinesUsadosLabel;
      @FXML  private Label rachaLabel;

    @FXML
    public  void setEstadisticas() throws Exception {
        // Obtener el email del usuario loggeado desde UserSession
       
FirebaseConexion.obtenerEstadisticasUsuario( JugadorSession.getJugadorActual());
     
        String nombre=JugadorSession.getJugadorActual().getNombreUsuario();
        // Actualizar la etiqueta con la información
        partidasJugadasLabel.setText(""+JugadorSession.getJugadorActual().getPartidasJugadas());
        partidasGanadasLabel.setText(""+JugadorSession.getJugadorActual().getPartidasGanadas());
      comodinesUsadosLabel.setText(""+JugadorSession.getJugadorActual().getComodinesUsados());
       rachaLabel.setText(""+JugadorSession.getJugadorActual().getMaxRacha());
        nombreLabel.setText(nombre);
       
        
        porcentajeAciertosLabel.setText(""+JugadorSession.getJugadorActual().getPorcentajeAciertos()+"%");
        
    }


    @FXML
    private void volverConfiguracion(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaz/vistas/ConfigurarPartida.fxml"));
        Scene scene = new Scene(loader.load());
        ConfigurarPartidaController confController= loader.getController();
        confController.mostrarNombreUsuario();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
             stage.centerOnScreen();
        stage.setTitle("ConfigurarPartida");
    }
    
}
