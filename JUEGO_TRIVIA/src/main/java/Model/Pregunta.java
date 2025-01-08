package Model;

import java.util.List;

public class Pregunta {
    private String pregunta;
    private List<String> opciones;
    private int respuestaCorrecta;
    private String categoria;
    private String dificultad;

    // Constructor sin parámetros (requerido por Firestore)
    public Pregunta() {
        // Constructor vacío necesario para la deserialización en Firestore
    }

    // Constructor con parámetros
    public Pregunta(String pregunta, List<String> opciones, int respuestaCorrecta, String categoria, String dificultad) {
        this.pregunta = pregunta;
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
        this.categoria = categoria;
        this.dificultad = dificultad;
    }

    
    // Getters y setters (necesarios para que Firestore pueda mapear la clase)
    public String getPregunta() {
        return pregunta;
    }

  
    public List<String> getOpciones() {
        return opciones;
    }

  

    public int getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    
 

}
