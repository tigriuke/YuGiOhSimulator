package modelo;

/**
 * Registro de un ataque para el historial de combate: turno, jugador que
 * atacó, monstruos involucrados, daño infligido y resultado.
 *
 * @author Samuel Lopez
 */
public class RegistroAtaque {

    /** Valor que se guarda cuando ningún jugador recibe daño. */
    public static final String SIN_JUGADOR = "-";

    private final int turno;
    private final String jugadorAtacante;
    private final String atacante;
    private final String defensor;
    private final int danio;
    private final String jugadorDaniado;
    private final String resultado;

    /**
     * @param turno           número de turno en que ocurrió el ataque
     * @param jugadorAtacante nombre del jugador que declaró el ataque
     * @param atacante        nombre del monstruo atacante
     * @param defensor        nombre del monstruo defensor o "Ataque directo"
     * @param danio           daño infligido
     * @param jugadorDaniado  jugador que recibió el daño o {@link #SIN_JUGADOR}
     * @param resultado       descripción del resultado
     */
    public RegistroAtaque(int turno, String jugadorAtacante, String atacante, String defensor,
            int danio, String jugadorDaniado, String resultado) {
        if (turno < 1 || danio < 0) {
            throw new IllegalArgumentException("El turno debe ser mayor que 0 y el daño no negativo.");
        }
        if (jugadorAtacante == null || atacante == null || defensor == null
                || jugadorDaniado == null || resultado == null) {
            throw new IllegalArgumentException("Los datos del registro no pueden ser nulos.");
        }
        this.turno = turno;
        this.jugadorAtacante = jugadorAtacante;
        this.atacante = atacante;
        this.defensor = defensor;
        this.danio = danio;
        this.jugadorDaniado = jugadorDaniado;
        this.resultado = resultado;
    }

    public int getTurno() {
        return turno;
    }

    public String getJugadorAtacante() {
        return jugadorAtacante;
    }

    public String getAtacante() {
        return atacante;
    }

    public String getDefensor() {
        return defensor;
    }

    public int getDanio() {
        return danio;
    }

    public String getJugadorDaniado() {
        return jugadorDaniado;
    }

    public String getResultado() {
        return resultado;
    }

    /**
     * @return true si el ataque causó daño a los LP de algún jugador
     */
    public boolean huboDanio() {
        return danio > 0;
    }

    @Override
    public String toString() {
        return "Turno " + turno + " | " + jugadorAtacante + ": " + atacante + " -> " + defensor
                + " | Daño: " + danio + (huboDanio() ? " a " + jugadorDaniado : "")
                + " | " + resultado;
    }
}
