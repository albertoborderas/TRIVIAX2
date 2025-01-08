package Principal;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ClasePrincipal extends Application {

     @Override
    public void start(Stage stage) throws Exception {
        
       FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/interfaz/vistas/InicioSesion.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Iniciar Sesión");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    public static void main(String[] args) {
         
        launch();
    }
}
