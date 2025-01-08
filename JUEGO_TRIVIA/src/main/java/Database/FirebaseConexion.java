package Database;

import Model.Jugador;
import Model.JugadorSession;
import Model.Pregunta;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import javafx.scene.control.Alert;

public class FirebaseConexion {
  
   
   
  
    
    private static final String BACKEND_URL = "https://shade-bloom-flag.glitch.me"; // URL del backend

public static void registrarUsuarioEnFirebaseAuth(String email, String contrasena, String nombreUsuario) {
    HttpURLConnection conn = null;
    try {
        // Crear la URL para la solicitud
        URL url = new URL(BACKEND_URL + "/registrarUsuario");

        // Configuración de la conexión
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Crear el cuerpo de la solicitud
        String jsonBody = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"nombreUsuario\":\"%s\"}", 
                                         email, contrasena, nombreUsuario);

        // Escribir el cuerpo de la solicitud en la conexión
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes());
            os.flush();
        }

        // Leer la respuesta del servidor
        int responseCode = conn.getResponseCode();
        InputStream responseStream = (responseCode >= 200 && responseCode < 300) 
                                    ? conn.getInputStream() 
                                    : conn.getErrorStream();

        // Leer el mensaje de la respuesta
        StringBuilder responseMessage = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseMessage.append(line);
            }
        }

 if (responseCode >= 400) {
    String errorMessage = responseMessage.toString();

    if (errorMessage.contains("El nombre de usuario ya está en uso")) {
        mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", "El nombre de usuario ya está en uso. Por favor, elige otro.");
    } else if (errorMessage.contains("El correo electrónico ya está en uso")) {
        mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", "El correo electrónico ya está en uso. Por favor, utiliza otro.");
    } else if (errorMessage.contains("La contraseña debe tener al menos 6 caracteres")) {
        mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", "La contraseña debe tener al menos 6 caracteres.");
    } else {
        mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", "Ocurrió un error al registrar el usuario.");
    }
    return;
}



       
        mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Exitoso", "Usuario registrado correctamente.");
    } catch (Exception e) {
        e.printStackTrace();
        mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", "No se pudo conectar con el servidor.");
    } finally {
        if (conn != null) {
            conn.disconnect();
        }
    }
}





    
    
 public static List<Pregunta> obtenerPreguntasAleatoriasPorNivel(String nivel, int cantidad) throws MalformedURLException {
       URL url = new URL(BACKEND_URL + "/obtenerPreguntas");
        List<Pregunta> preguntas = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        try {
            // Crear cuerpo de la solicitud
            String requestBody = gson.toJson(new SolicitudPreguntas(nivel, cantidad));

            // Crear solicitud HTTP POST
            HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url.toString())) // Usar la URL del backend
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            // Enviar solicitud y obtener respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Convertir la respuesta JSON a una lista de objetos Pregunta
                preguntas = gson.fromJson(response.body(), new TypeToken<List<Pregunta>>(){}.getType());
            } else {
                System.err.println("Error al obtener preguntas: " + response.body());
            }
        } catch (JsonSyntaxException | IOException | InterruptedException e) {
            System.err.println("Error en la comunicación con el backend: " + e.getMessage());
        }

        return preguntas;
    }
 
 // Clase interna para representar la solicitud
    private static class SolicitudPreguntas {
        private String nivel;
        private int cantidad;

        public SolicitudPreguntas(String nivel, int cantidad) {
            this.nivel = nivel;
            this.cantidad = cantidad;
        }
    }
    
    
    
    
    
    
public static void actualizarPartidasJugadas(String documentId) throws MalformedURLException {
    URL url = new URL(BACKEND_URL + "/actualizarPartidasJugadas");

    if (documentId == null || documentId.isEmpty()) {
        return; // No hacemos nada si el documentId está vacío
    }

    try {
        // Crear cliente HTTP y solicitud
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        // Crear el cuerpo de la solicitud como JSON
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("documentId", documentId);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url.toString())) // Usar la URL del backend
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
            .build();

        // Enviar solicitud y manejar respuesta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Solo registrar errores para depuración, sin mostrar alertas
        if (response.statusCode() != 200) {
            System.err.println("Error al actualizar partidas jugadas: " + response.body());
        }
    } catch (Exception e) {
        // Registrar errores para depuración
        e.printStackTrace();
    }
}



public static CompletableFuture<Void> actualizarProgreso(String documentId, Integer preguntasRespondidas, Integer preguntasAcertadas) throws MalformedURLException {
    URL url = new URL(BACKEND_URL + "/actualizarProgreso");

    if (documentId == null || documentId.isEmpty()) {
        throw new IllegalArgumentException("El ID de usuario no puede estar vacío.");
    }

    // Crear cliente HTTP
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = new Gson();

    // Crear el cuerpo de la solicitud como JSON
    JsonObject requestBody = new JsonObject();
    requestBody.addProperty("documentId", documentId);
    if (preguntasRespondidas != null) {
        requestBody.addProperty("preguntasRespondidas", preguntasRespondidas);
    }
    if (preguntasAcertadas != null) {
        requestBody.addProperty("preguntasAcertadas", preguntasAcertadas);
    }

    // Crear solicitud HTTP
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url.toString())) // Usar la URL del backend
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
        .build();

    // Enviar solicitud asíncronamente
    return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenAccept(response -> {
            if (response.statusCode() == 204) {
                System.out.println("Progreso actualizado correctamente.");
            } else {
                System.err.println("Error al actualizar el progreso. Código de estado: " + response.statusCode());
            }
        })
        .exceptionally(e -> {
            System.err.println("Error inesperado al actualizar el progreso: " + e.getMessage());
            e.printStackTrace();
            return null;
        });
}


    public static void incrementarPartidasGanadas(String documentId) throws MalformedURLException {
    URL url = new URL(BACKEND_URL + "/incrementarPartidasGanadas");

    if (documentId == null || documentId.isEmpty()) {
        throw new IllegalArgumentException("El ID de usuario no puede estar vacío.");
    }

    try {
        // Crear cliente HTTP y solicitud
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        // Crear el cuerpo de la solicitud como JSON
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("documentId", documentId);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url.toString())) // Usar la URL del backend
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
            .build();

        // Enviar solicitud
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            System.err.println("Error al incrementar partidas ganadas. Código de estado: " + response.statusCode());
        }
    } catch (Exception e) {
        System.err.println("Error inesperado al incrementar partidas ganadas: " + e.getMessage());
        e.printStackTrace();
    }
}
  public static void obtenerEstadisticasUsuario(Jugador jugador) throws Exception {
      String documentId=jugador.getId();
    if (documentId == null || documentId.isEmpty()) {
        throw new IllegalArgumentException("El ID de usuario no puede estar vacío.");
    }

    URL url = new URL(BACKEND_URL + "/obtenerEstadisticasUsuario");
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = new Gson();

    // Crear el objeto JSON con el documentId
    JsonObject requestBody = new JsonObject();
    requestBody.addProperty("documentId", documentId);

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url.toString()))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
            .build();

    // Enviar la solicitud y esperar la respuesta
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
        // Procesar la respuesta JSON
        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

        // Acceder a cada valor y guardarlos en las variables de la instancia de Jugador
        jugador.setPartidasJugadas(jsonResponse.get("partidasJugadas").getAsInt());
        jugador.setPartidasGanadas(jsonResponse.get("partidasGanadas").getAsInt());
        jugador.setPorcentajeAciertos(jsonResponse.get("porcentajeAciertos").getAsDouble());
        jugador.setComodinesUsados(jsonResponse.get("comodinesUsados").getAsInt());
        jugador.setMaxRacha(jsonResponse.get("maxRacha").getAsInt());
    } else {
        System.err.println("Error al obtener estadísticas del usuario: " + response.statusCode());
        throw new Exception("Error al obtener estadísticas del usuario");
    }
}


public static boolean autenticarUsuario(String email, String password) {
    if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
        mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor, completa todos los campos antes de continuar.");
        return false;
    }

    try {
        URL url = new URL(BACKEND_URL + "/iniciarSesion");
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        // Crear el cuerpo de la solicitud como JSON
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("email", email);
        requestBody.addProperty("password", password);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url.toString()))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
            .build();

        // Enviar la solicitud
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Procesar la respuesta
        if (response.statusCode() == 200) {
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            String mensaje = jsonResponse.get("message").getAsString();
           
            String documentId = jsonResponse.get("documentId").getAsString();
            String nombreUsuario = jsonResponse.get("nombreUsuario").getAsString(); // Obtener el nombre de usuario
           

            // Crear el objeto Jugador con los datos obtenidos
            Jugador jugadorActual = new Jugador(
                documentId, 
                nombreUsuario,0,0,0,0,0);
            // Guardar el jugador actual
            JugadorSession.setJugadorActual(jugadorActual);

            // Mostrar alerta de éxito
            mostrarAlerta(Alert.AlertType.INFORMATION, "Inicio exitoso", mensaje);
            return true;
        } else {
            JsonObject errorResponse = gson.fromJson(response.body(), JsonObject.class);
            String error = errorResponse.has("error") ? errorResponse.get("error").getAsString() : "Error desconocido";
            mostrarAlerta(Alert.AlertType.ERROR, "Error de inicio de sesión", error);
            return false;
        }
    } catch (Exception e) {
        mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado", "Ocurrió un error al intentar iniciar sesión. Por favor, inténtalo más tarde.");
        e.printStackTrace();
        return false;
    }
}

 public static CompletableFuture<Boolean> actualizarComodinesAsync(String documentId) {
        return CompletableFuture.supplyAsync(() -> {
            if (documentId == null || documentId.isEmpty()) {
                System.out.println("El documentId no puede estar vacío.");
                return false;
            }

            try {
                // Crear la URL para hacer la solicitud
                URI url = URI.create(BACKEND_URL + "/actualizarComodines");

                // Crear el cliente HTTP
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();

                // Crear el cuerpo de la solicitud como JSON
                String requestBody = gson.toJson(Map.of("documentId", documentId));

                // Crear la solicitud HTTP
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(url)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                // Enviar la solicitud y obtener la respuesta
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Verificar el código de respuesta
                if (response.statusCode() == 204) {
                    System.out.println("Comodín actualizado correctamente.");
                    return true;
                } else {
                    System.out.println("Error al actualizar comodín. Código de error: " + response.statusCode());
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Error al realizar la solicitud: " + e.getMessage());
                return false;
            }
        });
    }

public static CompletableFuture<Boolean> actualizarRachaAsync(String documentId, boolean acertada) {
    return CompletableFuture.supplyAsync(() -> {
        if (documentId == null || documentId.isEmpty()) {
            System.out.println("El documentId no puede estar vacío.");
            return false;
        }

        try {
            // Crear la URL para hacer la solicitud
            URI url = URI.create(BACKEND_URL + "/actualizarRacha");

            // Crear el cliente HTTP
            HttpClient client = HttpClient.newHttpClient();
            Gson gson = new Gson();

            // Crear el cuerpo de la solicitud como JSON
            String requestBody = gson.toJson(Map.of("documentId", documentId, "acertada", acertada));

            // Crear la solicitud HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Enviar la solicitud y obtener la respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verificar el código de respuesta
            if (response.statusCode() == 204) {
                System.out.println("Racha actualizada correctamente.");
                return true;
            } else {
                System.out.println("Error al actualizar racha. Código de error: " + response.statusCode());
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error al realizar la solicitud: " + e.getMessage());
            return false;
        }
    });
}

   public static List<Jugador> obtenerRanking() {
    List<Jugador> ranking = new ArrayList<>();
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = new Gson();

    try {
        // Crear cuerpo de la solicitud con la cantidad de jugadores que quieres obtener
        String jsonBody = "{\"cantidad\": 10}"; // Puedes cambiar 10 por la cantidad que necesites

        // Crear solicitud HTTP POST
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL + "/obtenerRanking"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)) // Usamos POST
                .build();

        // Enviar la solicitud y recibir la respuesta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Convertir la respuesta JSON a una lista de objetos Jugador
            ranking = gson.fromJson(response.body(), new TypeToken<List<Jugador>>(){}.getType());
        } else {
            System.err.println("Error al obtener el ranking: " + response.body());
        }
    } catch (JsonSyntaxException | IOException | InterruptedException e) {
        System.err.println("Error en la comunicación con el backend: " + e.getMessage());
    }

    return ranking;
}



    // Método para mostrar alertas
    public static void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
}