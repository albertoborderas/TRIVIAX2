/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

public class JugadorSession {
    private static Jugador jugadorActual;

    // Establecer el jugador actual
    public static void setJugadorActual(Jugador jugador) {
        jugadorActual = jugador;
    }

    // Obtener el jugador actual
    public static Jugador getJugadorActual() {
        return jugadorActual;
    }
}
