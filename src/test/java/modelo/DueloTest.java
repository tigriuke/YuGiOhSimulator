package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas del flujo del duelo: turnos, ataques, LP y fin del duelo.
 *
 * @author Samuel Lopez
 */
class DueloTest {

    @TempDir
    Path carpetaTemporal;

    private Duelo duelo;

    @BeforeEach
    void setUp() {
        HistorialArchivo archivo = new HistorialArchivo(carpetaTemporal.resolve("historial.txt").toString());
        duelo = new Duelo(new CalculadoraDanio(), archivo);
        duelo.iniciar("Ana", "Luis");
    }

    @Test
    void iniciarCreaLosJugadoresYEmpiezaElJugador1() {
        assertEquals("Ana", duelo.getJugador1().getNombre());
        assertEquals("Luis", duelo.getJugador2().getNombre());
        assertEquals(Jugador.LP_INICIAL, duelo.getJugador2().getPuntosVida());
        assertSame(duelo.getJugador1(), duelo.getJugadorEnTurno());
        assertSame(duelo.getJugador2(), duelo.getRival());
        assertEquals(1, duelo.getTurno());
    }

    @Test
    void nombresRepetidosLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> duelo.iniciar("Ana", "ana"));
    }

    @Test
    void sinIniciarNoSePuedenInvocarMonstruos() {
        Duelo sinIniciar = new Duelo(new CalculadoraDanio(),
                new HistorialArchivo(carpetaTemporal.resolve("otro.txt").toString()));
        assertFalse(sinIniciar.estaIniciado());
        assertNull(sinIniciar.getRival());
        assertThrows(DueloException.class, () -> sinIniciar.agregarMonstruo(1,
                new MonstruoNormal("Golem", 1000, 1000, Posicion.ATAQUE)));
    }

    @Test
    void terminarTurnoAlternaElJugador() throws DueloException {
        duelo.terminarTurno();
        assertSame(duelo.getJugador2(), duelo.getJugadorEnTurno());
        assertEquals(2, duelo.getTurno());
    }

    @Test
    void ataqueGanadorRestaLpAlRivalYDestruyeSuMonstruo() throws Exception {
        duelo.agregarMonstruo(1, new MonstruoNormal("Dragón", 2500, 2000, Posicion.ATAQUE));
        duelo.agregarMonstruo(2, new MonstruoNormal("Guerrero", 2000, 1500, Posicion.ATAQUE));
        duelo.atacar(0, 0);
        assertEquals(7500, duelo.getJugador2().getPuntosVida());
        assertFalse(duelo.getJugador2().tieneMonstruos());
        assertTrue(duelo.getJugador1().tieneMonstruos());
    }

    @Test
    void ataqueContraDefensaMayorDaniaAlAtacante() throws Exception {
        duelo.agregarMonstruo(1, new MonstruoNormal("Duende", 1000, 500, Posicion.ATAQUE));
        duelo.agregarMonstruo(2, new MonstruoNormal("Muro", 500, 2000, Posicion.DEFENSA));
        duelo.atacar(0, 0);
        assertEquals(7000, duelo.getJugador1().getPuntosVida());
        assertEquals(Jugador.LP_INICIAL, duelo.getJugador2().getPuntosVida());
        assertEquals(1, duelo.getJugador2().cantidadMonstruos());
    }

    @Test
    void ataqueDirectoSoloSiElRivalNoTieneMonstruos() throws Exception {
        duelo.agregarMonstruo(1, new MonstruoNormal("Dragón", 2500, 2000, Posicion.ATAQUE));
        duelo.agregarMonstruo(2, new MonstruoNormal("Muro", 0, 2500, Posicion.DEFENSA));
        assertThrows(DueloException.class, () -> duelo.atacar(0));
    }

    @Test
    void ataqueDirectoRestaElAtaqueCompleto() throws Exception {
        duelo.agregarMonstruo(1, new MonstruoEfecto("Lancero", 1600, 800, Posicion.ATAQUE));
        duelo.atacar(0);
        assertEquals(6400, duelo.getJugador2().getPuntosVida());
    }

    @Test
    void unMonstruoNoAtacaDosVecesEnElMismoTurno() throws Exception {
        duelo.agregarMonstruo(1, new MonstruoNormal("Dragón", 2500, 2000, Posicion.ATAQUE));
        duelo.atacar(0);
        assertThrows(DueloException.class, () -> duelo.atacar(0));
    }

    @Test
    void elMonstruoVuelveAAtacarEnElSiguienteTurnoPropio() throws Exception {
        duelo.agregarMonstruo(1, new MonstruoNormal("Dragón", 2500, 2000, Posicion.ATAQUE));
        duelo.atacar(0);
        duelo.terminarTurno();
        duelo.terminarTurno();
        duelo.atacar(0);
        assertEquals(3000, duelo.getJugador2().getPuntosVida());
    }

    @Test
    void indiceDeMonstruoInvalidoLanzaDueloException() {
        assertThrows(DueloException.class, () -> duelo.atacar(0));
        assertThrows(DueloException.class, () -> duelo.cambiarPosicion(3));
    }

    @Test
    void cambiarPosicionAfectaAlMonstruoDelJugadorEnTurno() throws DueloException {
        Monstruo golem = new MonstruoNormal("Golem", 1000, 2000, Posicion.ATAQUE);
        duelo.agregarMonstruo(1, golem);
        duelo.cambiarPosicion(0);
        assertEquals(Posicion.DEFENSA, golem.getPosicion());
    }

    @Test
    void elDueloTerminaCuandoUnJugadorLlegaACeroLp() throws Exception {
        duelo.agregarMonstruo(1, new MonstruoNormal("Titán", 5000, 5000, Posicion.ATAQUE));
        duelo.atacar(0);
        duelo.terminarTurno();
        duelo.terminarTurno();
        duelo.atacar(0);
        assertTrue(duelo.hayGanador());
        assertSame(duelo.getJugador1(), duelo.getGanador());
        assertEquals(0, duelo.getJugador2().getPuntosVida());
        assertThrows(DueloException.class, () -> duelo.terminarTurno());
    }

    @Test
    void cadaAtaqueSeGuardaEnElHistorialYEnElArchivo() throws Exception {
        duelo.agregarMonstruo(1, new MonstruoNormal("Dragón", 2500, 2000, Posicion.ATAQUE));
        duelo.agregarMonstruo(2, new MonstruoNormal("Guerrero", 2000, 1500, Posicion.ATAQUE));
        RegistroAtaque registro = duelo.atacar(0, 0);

        assertEquals(1, duelo.getHistorial().size());
        List<RegistroAtaque> guardados = duelo.leerHistorialGuardado();
        assertEquals(1, guardados.size());
        assertEquals("Dragón", guardados.get(0).getAtacante());
        assertEquals("Guerrero", guardados.get(0).getDefensor());
        assertEquals(500, guardados.get(0).getDanio());
        assertEquals("Luis", registro.getJugadorDaniado());
    }

    @Test
    void registroSinDanioNoTieneJugadorAfectado() throws Exception {
        duelo.agregarMonstruo(1, new MonstruoNormal("Dragón", 2000, 2000, Posicion.ATAQUE));
        duelo.agregarMonstruo(2, new MonstruoNormal("Muro", 0, 1500, Posicion.DEFENSA));
        RegistroAtaque registro = duelo.atacar(0, 0);
        assertFalse(registro.huboDanio());
        assertEquals(RegistroAtaque.SIN_JUGADOR, registro.getJugadorDaniado());
    }

    @Test
    void iniciarUnNuevoDueloLimpiaElHistorialDeLaSesion() throws Exception {
        duelo.agregarMonstruo(1, new MonstruoNormal("Dragón", 2500, 2000, Posicion.ATAQUE));
        duelo.atacar(0);
        duelo.iniciar("Carlos", "Diana");
        assertTrue(duelo.getHistorial().isEmpty());
        assertEquals(1, duelo.leerHistorialGuardado().size());
    }
}
