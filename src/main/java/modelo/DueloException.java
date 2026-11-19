package modelo;

/**
 * Excepción propia del simulador. Se lanza cuando una acción viola las
 * reglas del duelo, por ejemplo atacar con un monstruo en defensa, atacar
 * dos veces en el mismo turno o atacar directamente si el rival tiene
 * monstruos en el campo.
 *
 * @author Samuel Lopez
 */
public class DueloException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje explicación de la regla que se incumplió
     */
    public DueloException(String mensaje) {
        super(mensaje);
    }
}
