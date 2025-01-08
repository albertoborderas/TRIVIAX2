package interfaz.controladores;

import Database.FirebaseConexion;
import Model.Jugador;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RankingController {

    @FXML
    private TableView<Jugador> tableJugadores;  // Cambiar de UserSession a Jugador

    @FXML
    private TableColumn<Jugador, String> colEmail;  // Cambiar 'colEmail' a 'colNombreUsuario'
    @FXML
    private TableColumn<Jugador, Integer> colPartidasJugadas;
    @FXML
    private TableColumn<Jugador, Integer> colPartidasGanadas;
    @FXML
    private TableColumn<Jugador, Double> colPorcentajeAciertos;  // Añadir columna para el porcentaje de aciertos

    // Método para cargar los jugadores en la tabla
    public void initialize() {
        // Configurar las columnas de la tabla
        colEmail.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));  // Cambiar 'email' a 'nombreUsuario'
        colPartidasJugadas.setCellValueFactory(new PropertyValueFactory<>("partidasJugadas"));
        colPartidasGanadas.setCellValueFactory(new PropertyValueFactory<>("partidasGanadas"));
        colPorcentajeAciertos.setCellValueFactory(new PropertyValueFactory<>("porcentajeAciertos"));

        // Supongo que tienes una clase FirebaseConexion o algo similar para obtener los datos de los jugadores
        List<Jugador> jugadores = FirebaseConexion.obtenerRanking();  // Aquí cambiaríamos para obtener Jugadores
        ObservableList<Jugador> listaJugadores = FXCollections.observableArrayList(jugadores);
        tableJugadores.setItems(listaJugadores);  // Cambiar la tabla para mostrar jugadores
    }
}
