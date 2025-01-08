package interfaz.controladores;

import Database.FirebaseConexion;
import Model.JugadorSession;
import Model.Pregunta;
import java.io.IOException;
import java.net.MalformedURLException;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class VistaPartidaController {

    private List<Pregunta> todasLasPreguntas;
    private int preguntaActualIndex = 0;
    private Pregunta preguntaActual;
    private int tiempoRestante = 15; // Tiempo inicial en segundos
    private Timeline temporizador;
    private int contadorCorrectas = 0;
    private int indiceRespuestaCorrectaActual; // Índice de la respuesta correcta de la pregunta actual

    @FXML private Label labelPregunta;
    @FXML private Label labelspane;
    @FXML private StackPane spane;
    @FXML private Label labelContadorTiempo;
    @FXML private Label labelContadorCorrectas;
    @FXML private Button btnRespuesta1;
    @FXML private Button btnRespuesta2;
    @FXML private Button btnRespuesta3;
    @FXML private Button btnRespuesta4;
    

    
    private int indiceMedias=5;
    private int indiceDificiles=10;
    
    
   
    private List<Pregunta> preguntasExtrasFacil;
       private List<Pregunta> preguntasExtrasIntermedio;
          private List<Pregunta> preguntasExtrasDificil;
    

    private boolean comodin50Disponible = true; // Comodín 50%
private boolean comodinCambiarPreguntaDisponible = true; // Comodín Cambiar Pregunta


    @FXML
    public void initialize() throws Exception {
  Database.FirebaseConexion.actualizarPartidasJugadas(Model.JugadorSession.getJugadorActual().getId());
        cargarPreguntasParaPartida();   
            
    }

    private void cargarPreguntasParaPartida() throws IOException {
        List<Pregunta> preguntasFacil = FirebaseConexion.obtenerPreguntasAleatoriasPorNivel("facil", 5);
        List<Pregunta> preguntasIntermedio = FirebaseConexion.obtenerPreguntasAleatoriasPorNivel("intermedio", 5);
        List<Pregunta> preguntasDificil = FirebaseConexion.obtenerPreguntasAleatoriasPorNivel("dificil", 5);
        
             preguntasExtrasFacil =FirebaseConexion.obtenerPreguntasAleatoriasPorNivel("facil", 1);
        preguntasExtrasIntermedio =FirebaseConexion.obtenerPreguntasAleatoriasPorNivel("intermedio", 1);
            preguntasExtrasDificil =FirebaseConexion.obtenerPreguntasAleatoriasPorNivel("dificil", 1);

        todasLasPreguntas = new ArrayList<>();
        todasLasPreguntas.addAll(preguntasFacil);
        todasLasPreguntas.addAll(preguntasIntermedio);
        todasLasPreguntas.addAll(preguntasDificil);

        if (!todasLasPreguntas.isEmpty()) {
            cargarPregunta();
        } else {
            mostrarMensaje("No se encontraron preguntas.");
        }
    }

  private void cargarPregunta() {
    // Reactivar los botones antes de cargar una nueva pregunta
    reactivarBotones();

    if (preguntaActualIndex < todasLasPreguntas.size()) {
        preguntaActual = todasLasPreguntas.get(preguntaActualIndex);
        // Configuración de la pregunta según el índice
        if (preguntaActualIndex == 0) {
            labelspane.setText("NIVEL FÁCIL");
            hacerPausa(1);
        } else if (preguntaActualIndex == indiceMedias) {
            labelspane.setText("NIVEL MEDIO");
            hacerPausa(1);
        } else if (preguntaActualIndex == indiceDificiles) {
            labelspane.setText("NIVEL DIFÍCIL");
            hacerPausa(1);
        } else {
            mostrarPreguntaYOpciones();
        }
    } else {
        mostrarMensaje("¡Has completado todas las preguntas de la partida!");
        cargarPantallaResultados();
    }
}


  private void hacerPausa(int segundos) {
    spane.setVisible(true);
    spane.setOpacity(1); // Asegurar opacidad inicial visible

    // Pausa inicial
    PauseTransition pausa = new PauseTransition(Duration.seconds(segundos));
    pausa.setOnFinished(event -> {
        // Mostrar la nueva pregunta antes de la transición de fade-out
        mostrarPreguntaYOpciones();

        // Crear transición de fade-out después de mostrar la nueva pregunta
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), spane);
        fadeOut.setToValue(0); // Reducir opacidad a 0
        fadeOut.setOnFinished(fadeEvent -> {
            spane.setVisible(false); // Ocultar el StackPane después del fade-out
        });
        fadeOut.play(); // Iniciar fade-out
    });

    pausa.play(); // Iniciar la pausa
}


    private void mostrarPreguntaYOpciones() {
        labelPregunta.setText(preguntaActual.getPregunta());

        // Obtener y mezclar opciones
        List<String> opciones = new ArrayList<>(preguntaActual.getOpciones());
        String respuestaCorrectaTexto = preguntaActual.getOpciones().get(preguntaActual.getRespuestaCorrecta());
        Collections.shuffle(opciones);

        // Asignar opciones a los botones
        btnRespuesta1.setText(opciones.get(0));
        btnRespuesta2.setText(opciones.get(1));
        btnRespuesta3.setText(opciones.get(2));
        btnRespuesta4.setText(opciones.get(3));

        resetearEstiloBotones();

        // Índice de la respuesta correcta
        indiceRespuestaCorrectaActual = opciones.indexOf(respuestaCorrectaTexto);

        // Configurar eventos de los botones
        btnRespuesta1.setOnAction(event -> {
            try {
                verificarRespuesta(indiceRespuestaCorrectaActual == 0, btnRespuesta1);
            } catch (MalformedURLException ex) {
                Logger.getLogger(VistaPartidaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnRespuesta2.setOnAction(event -> {
            try {
                verificarRespuesta(indiceRespuestaCorrectaActual == 1, btnRespuesta2);
            } catch (MalformedURLException ex) {
                Logger.getLogger(VistaPartidaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnRespuesta3.setOnAction(event -> {
            try {
                verificarRespuesta(indiceRespuestaCorrectaActual == 2, btnRespuesta3);
            } catch (MalformedURLException ex) {
                Logger.getLogger(VistaPartidaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        btnRespuesta4.setOnAction(event -> {
            try {
                verificarRespuesta(indiceRespuestaCorrectaActual == 3, btnRespuesta4);
            } catch (MalformedURLException ex) {
                Logger.getLogger(VistaPartidaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // Iniciar temporizador solo cuando la pausa haya terminado
        iniciarTemporizador();
    }

    private void iniciarTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
        }

        tiempoRestante = 15;
        labelContadorTiempo.setText("Tiempo: " + tiempoRestante);

        temporizador = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            tiempoRestante--;
            labelContadorTiempo.setText("Tiempo: " + tiempoRestante);

            if (tiempoRestante <= 0) {
                temporizador.stop();
                manejarTiempoAgotado(); // Procesar cuando el tiempo se acaba
            }
        }));
        temporizador.setCycleCount(Timeline.INDEFINITE);
        temporizador.play();
    }

    private void manejarTiempoAgotado() {
        Platform.runLater(() -> {
            marcarRespuestaCorrecta();
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Tiempo agotado");
            alerta.setHeaderText(null);
            alerta.setContentText("Se te acabó el tiempo. Respuesta incorrecta.");
            alerta.showAndWait();

            preguntaActualIndex++;
            cargarPregunta();
        });
    }
private void cargarPantallaResultados() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaz/vistas/resultado.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista de resultados
        ResultadoController resultadoController = loader.getController();

        // Pasar los resultados al controlador
    

    
            // Crear la escena y la ventana para mostrarla
            Stage stage = new Stage();
            stage.setTitle("Resultados");
                resultadoController.setResultados(contadorCorrectas,comodin50Disponible,comodinCambiarPreguntaDisponible);
            stage.setScene(new Scene(root));
            stage.show();
            
            Stage currentStage = (Stage) labelPregunta.getScene().getWindow();
            currentStage.close();
    
            
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private void verificarRespuesta(boolean esCorrecto, Button botonSeleccionado) throws MalformedURLException {
        if (temporizador != null) {
            temporizador.stop();
        }

        if (esCorrecto) {
            FirebaseConexion.actualizarRachaAsync(JugadorSession.getJugadorActual().getId(),true);
            contadorCorrectas++;
            labelContadorCorrectas.setText("Correctas: " + contadorCorrectas+"/15");
            botonSeleccionado.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-background-radius: 50;");
            mostrarMensaje("Correcto");
            FirebaseConexion.actualizarProgreso(Model.JugadorSession.getJugadorActual().getId(),1,1);
        } else {
               FirebaseConexion.actualizarRachaAsync(JugadorSession.getJugadorActual().getId(),false);
            botonSeleccionado.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-background-radius: 50;");
            marcarRespuestaCorrecta();
            mostrarMensaje("Incorrecto");
              FirebaseConexion.actualizarProgreso(Model.JugadorSession.getJugadorActual().getId(),1,0);
        }

        preguntaActualIndex++;
        cargarPregunta();
    }

    private void resetearEstiloBotones() {
        String estiloOriginal = "-fx-background-radius: 50; -fx-background-color: #ffe0f8; -fx-text-fill: black;";
        btnRespuesta1.setStyle(estiloOriginal);
        btnRespuesta2.setStyle(estiloOriginal);
        btnRespuesta3.setStyle(estiloOriginal);
        btnRespuesta4.setStyle(estiloOriginal);
    }

    private void marcarRespuestaCorrecta() {
        // Identificar el botón correspondiente y cambiar su estilo a verde
        switch (indiceRespuestaCorrectaActual) {
            case 0:
                btnRespuesta1.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-background-radius: 50;");
                break;
            case 1:
                btnRespuesta2.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-background-radius: 50;");
                break;
            case 2:
                btnRespuesta3.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-background-radius: 50;");
                break;
            case 3:
                btnRespuesta4.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-background-radius: 50;");
                break;
        }
    }
    // Número de segmentos de la ruleta



    
 @FXML
private void activarComodin50() {
    if (comodin50Disponible) {
        comodin50Disponible = false; // Desactivamos el comodín para que no pueda usarse de nuevo

       FirebaseConexion.actualizarComodinesAsync(JugadorSession.getJugadorActual().getId())
    .thenAccept(success -> {
        if (success) {
            System.out.println("Comodín actualizado correctamente.");
        } else {
            System.out.println("Error al actualizar comodín.");
        }
    })
    .exceptionally(e -> {
        System.out.println("Error en la llamada al servidor: " + e.getMessage());
        return null;
    });
        // Lista de botones disponibles
        List<Button> botones = new ArrayList<>(Arrays.asList(btnRespuesta1, btnRespuesta2, btnRespuesta3, btnRespuesta4));

        // Creamos una lista de índices para las respuestas incorrectas
        List<Integer> indicesIncorrectos = new ArrayList<>();
        for (int i = 0; i < botones.size(); i++) {
            if (i != indiceRespuestaCorrectaActual) {  // No incluimos el índice de la respuesta correcta
                indicesIncorrectos.add(i);
            }
        }

        // Seleccionamos aleatoriamente dos respuestas incorrectas para desactivar
        Collections.shuffle(indicesIncorrectos); // Mezclamos los índices incorrectos
        int boton1Index = indicesIncorrectos.get(0);
        int boton2Index = indicesIncorrectos.get(1);

        // Desactivamos esos botones
        botones.get(boton1Index).setDisable(true);
        botones.get(boton2Index).setDisable(true);

        // Mostrar un mensaje de que el comodín ha sido usado
        mostrarMensaje("Comodín 50/50 activado: Dos respuestas incorrectas han sido desactivadas.");
    } else {
        mostrarMensaje("El comodín 50/50 ya ha sido utilizado.");
    }
}
private void reactivarBotones() {
    // Reactivamos los botones
    btnRespuesta1.setDisable(false);
    btnRespuesta2.setDisable(false);
    btnRespuesta3.setDisable(false);
    btnRespuesta4.setDisable(false);

    // Resetear estilos de los botones
    resetearEstiloBotones();
}
@FXML
private void cambiarPregunta() {
   if (comodinCambiarPreguntaDisponible) {
            comodinCambiarPreguntaDisponible = false;
            
              FirebaseConexion.actualizarComodinesAsync(JugadorSession.getJugadorActual().getId())
    .thenAccept(success -> {
        if (success) {
            System.out.println("Comodín actualizado correctamente.");
        } else {
            System.out.println("Error al actualizar comodín.");
        }
    })
    .exceptionally(e -> {
        System.out.println("Error en la llamada al servidor: " + e.getMessage());
        return null;
    });

            // Dependiendo del nivel actual, agregamos una pregunta extra
            Pregunta preguntaExtra = null;
            if (preguntaActualIndex < 5) {
                preguntaExtra = preguntasExtrasFacil.get(0);  // Pregunta extra fácil
               indiceMedias=6;
               indiceDificiles=11;
            } else if (preguntaActualIndex < 10) {
                preguntaExtra = preguntasExtrasIntermedio.get(0);  // Pregunta extra intermedia
                   indiceDificiles=11;
            } else {
                preguntaExtra = preguntasExtrasDificil.get(0);  // Pregunta extra difícil
              
            }

            if (preguntaExtra != null) {
                todasLasPreguntas.add(preguntaActualIndex + 1, preguntaExtra);
                 preguntaActualIndex++;
                cargarPregunta();  // Cargar la nueva pregunta
            }
            
             mostrarMensaje("Has usado el comodín para cambiar de pregunta");
        }
   else {
        mostrarMensaje("El comodín cambiar pregunta ya ha sido utilizado.");
    }
}

   
   
        


    private void mostrarMensaje(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Resultado");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
