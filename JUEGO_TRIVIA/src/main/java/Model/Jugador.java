package Model;

public class Jugador {
    private String id;
    private String nombreUsuario;
    private int partidasJugadas;
    private int partidasGanadas;
    private double porcentajeAciertos;
     private int comodinesUsados;
    private int maxRacha;

       public Jugador(String id, String nombreUsuario, int partidasJugadas, int partidasGanadas, double porcentajeAciertos, int comodinesUsados, int maxRacha) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.partidasJugadas = partidasJugadas;
        this.partidasGanadas = partidasGanadas;
        this.porcentajeAciertos = porcentajeAciertos;
        this.comodinesUsados = comodinesUsados;
        this.maxRacha = maxRacha;
    }
    // Métodos getter para los datos
    public String getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public int getPartidasGanadas() {
        return partidasGanadas;
    }

    public double getPorcentajeAciertos() {
        return porcentajeAciertos;
    }

    // Método setter si es necesario
    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

    public void setPartidasGanadas(int partidasGanadas) {
        this.partidasGanadas = partidasGanadas;
    }

    public void setPorcentajeAciertos(double porcentajeAciertos) {
        this.porcentajeAciertos = porcentajeAciertos;
    }
     public int getComodinesUsados() {
        return comodinesUsados;
    }

    public void setComodinesUsados(int comodinesUsados) {
        this.comodinesUsados = comodinesUsados;
    }

    public int getMaxRacha() {
        return maxRacha;
    }

    public void setMaxRacha(int maxRacha) {
        this.maxRacha = maxRacha;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "id='" + id + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", partidasJugadas=" + partidasJugadas +
                ", partidasGanadas=" + partidasGanadas +
                ", porcentajeAciertos=" + porcentajeAciertos +
                '}';
    }
}
