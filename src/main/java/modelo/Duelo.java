package modelo;

/**
 * Coordina el duelo entre dos jugadores: turnos, ataques y fin del duelo.
 * Aplica sobre los jugadores el resultado que calculan las
 * {@link ReglasBatalla}.
 *
 * @author Samuel Lopez
 */
public class Duelo {

    private final ReglasBatalla reglas;
    private Jugador jugador1;
    private Jugador jugador2;
    private Jugador jugadorEnTurno;
    private int turno;

    /**
     * @param reglas reglas de cálculo de daño que usará el duelo
     */
    public Duelo(ReglasBatalla reglas) {
        if (reglas == null) {
            throw new IllegalArgumentException("Las reglas de batalla son obligatorias.");
        }
        this.reglas = reglas;
    }

    /**
     * Crea a los dos jugadores con sus LP iniciales. Empieza el jugador 1.
     *
     * @param nombreJugador1 nombre del primer jugador
     * @param nombreJugador2 nombre del segundo jugador
     */
    public void iniciar(String nombreJugador1, String nombreJugador2) {
        Jugador nuevo1 = new Jugador(nombreJugador1);
        Jugador nuevo2 = new Jugador(nombreJugador2);
        if (nuevo1.getNombre().equalsIgnoreCase(nuevo2.getNombre())) {
            throw new IllegalArgumentException("Los jugadores deben tener nombres diferentes.");
        }
        jugador1 = nuevo1;
        jugador2 = nuevo2;
        jugadorEnTurno = jugador1;
        turno = 1;
    }

    public boolean estaIniciado() {
        return jugador1 != null;
    }

    /**
     * Invoca un monstruo en el campo del jugador indicado.
     *
     * @param numeroJugador 1 o 2
     * @param monstruo      monstruo a invocar
     * @throws DueloException si el duelo no está activo o el campo está lleno
     */
    public void agregarMonstruo(int numeroJugador, Monstruo monstruo) throws DueloException {
        verificarDueloActivo();
        getJugador(numeroJugador).agregarMonstruo(monstruo);
    }

    /**
     * El jugador en turno ataca a un monstruo del rival.
     *
     * @param indiceAtacante índice del monstruo atacante en el campo propio
     * @param indiceDefensor índice del monstruo atacado en el campo rival
     * @return resultado de la batalla
     * @throws DueloException si el ataque no cumple las reglas
     */
    public ResultadoAtaque atacar(int indiceAtacante, int indiceDefensor) throws DueloException {
        verificarDueloActivo();
        Monstruo atacante = jugadorEnTurno.obtenerMonstruo(indiceAtacante);
        Monstruo defensor = getRival().obtenerMonstruo(indiceDefensor);
        atacante.registrarAtaque();
        return aplicarResultado(reglas.atacar(atacante, defensor), atacante, defensor);
    }

    /**
     * Ataque directo a los LP del rival (sobrecarga). Solo se permite si el
     * rival no tiene monstruos en el campo.
     *
     * @param indiceAtacante índice del monstruo atacante en el campo propio
     * @return resultado de la batalla
     * @throws DueloException si el ataque no cumple las reglas
     */
    public ResultadoAtaque atacar(int indiceAtacante) throws DueloException {
        verificarDueloActivo();
        Jugador rival = getRival();
        if (rival.tieneMonstruos()) {
            throw new DueloException("No puede atacar directamente: " + rival.getNombre()
                    + " tiene monstruos en el campo.");
        }
        Monstruo atacante = jugadorEnTurno.obtenerMonstruo(indiceAtacante);
        atacante.registrarAtaque();
        return aplicarResultado(reglas.atacar(atacante), atacante, null);
    }

    /**
     * Cambia la posición de un monstruo del jugador en turno.
     *
     * @param indiceMonstruo índice del monstruo en el campo propio
     * @throws DueloException si el duelo no está activo o el monstruo ya atacó
     */
    public void cambiarPosicion(int indiceMonstruo) throws DueloException {
        verificarDueloActivo();
        jugadorEnTurno.obtenerMonstruo(indiceMonstruo).cambiarPosicion();
    }

    /**
     * Termina el turno: el rival pasa a ser el jugador en turno.
     *
     * @throws DueloException si el duelo no está activo
     */
    public void terminarTurno() throws DueloException {
        verificarDueloActivo();
        jugadorEnTurno.reiniciarAtaques();
        jugadorEnTurno = getRival();
        turno++;
    }

    public boolean hayGanador() {
        return estaIniciado() && (jugador1.estaDerrotado() || jugador2.estaDerrotado());
    }

    /**
     * @return el jugador que ganó o null si el duelo sigue en curso
     */
    public Jugador getGanador() {
        if (!hayGanador()) {
            return null;
        }
        return jugador1.estaDerrotado() ? jugador2 : jugador1;
    }

    /**
     * @param numero 1 o 2
     * @return el jugador con ese número
     */
    public Jugador getJugador(int numero) {
        if (numero == 1) {
            return jugador1;
        }
        if (numero == 2) {
            return jugador2;
        }
        throw new IllegalArgumentException("El número de jugador debe ser 1 o 2.");
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public Jugador getJugadorEnTurno() {
        return jugadorEnTurno;
    }

    /**
     * @return el jugador que no está en turno (null si no hay duelo)
     */
    public Jugador getRival() {
        if (!estaIniciado()) {
            return null;
        }
        return jugadorEnTurno == jugador1 ? jugador2 : jugador1;
    }

    public int getTurno() {
        return turno;
    }

    private ResultadoAtaque aplicarResultado(ResultadoAtaque resultado, Monstruo atacante, Monstruo defensor) {
        Jugador rival = getRival();
        Jugador jugadorDaniado = null;
        if (resultado.getDanio() > 0) {
            jugadorDaniado = resultado.isDanioAlAtacante() ? jugadorEnTurno : rival;
            jugadorDaniado.recibirDanio(resultado.getDanio());
        }
        if (resultado.isAtacanteDestruido()) {
            jugadorEnTurno.retirarMonstruo(atacante);
        }
        if (resultado.isDefensorDestruido() && defensor != null) {
            rival.retirarMonstruo(defensor);
        }
        return resultado;
    }

    private void verificarDueloActivo() throws DueloException {
        if (!estaIniciado()) {
            throw new DueloException("Primero debe iniciar el duelo.");
        }
        if (hayGanador()) {
            throw new DueloException("El duelo terminó: ganó " + getGanador().getNombre()
                    + ". Inicie un nuevo duelo.");
        }
    }
}
