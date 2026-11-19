package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la gestión de LP y del campo del jugador.
 *
 * @author Samuel Lopez
 */
class JugadorTest {

    private Jugador jugador;

    @BeforeEach
    void setUp() {
        jugador = new Jugador("  Ana  ");
    }

    @Test
    void jugadorEmpiezaConLosLpIniciales() {
        assertEquals(Jugador.LP_INICIAL, jugador.getPuntosVida());
        assertEquals(8000, jugador.getPuntosVida());
        assertFalse(jugador.estaDerrotado());
    }

    @Test
    void nombreSeGuardaSinEspacios() {
        assertEquals("Ana", jugador.getNombre());
    }

    @Test
    void nombreVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Jugador(""));
        assertThrows(IllegalArgumentException.class, () -> new Jugador(null));
    }

    @Test
    void recibirDanioRestaLosLp() {
        jugador.recibirDanio(1500);
        assertEquals(6500, jugador.getPuntosVida());
    }

    @Test
    void lpNuncaQuedanNegativosYElJugadorPierde() {
        jugador.recibirDanio(9000);
        assertEquals(0, jugador.getPuntosVida());
        assertTrue(jugador.estaDerrotado());
    }

    @Test
    void danioCeroNoCambiaLosLp() {
        jugador.recibirDanio(0);
        assertEquals(Jugador.LP_INICIAL, jugador.getPuntosVida());
    }

    @Test
    void danioNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> jugador.recibirDanio(-10));
    }

    @Test
    void agregarYObtenerMonstruosDelCampo() throws DueloException {
        Monstruo golem = new MonstruoNormal("Golem de Piedra", 1000, 2000, Posicion.DEFENSA);
        jugador.agregarMonstruo(golem);
        assertTrue(jugador.tieneMonstruos());
        assertEquals(1, jugador.cantidadMonstruos());
        assertSame(golem, jugador.obtenerMonstruo(0));
    }

    @Test
    void campoAdmiteComoMaximoCincoMonstruos() throws DueloException {
        for (int i = 0; i < Jugador.MAX_MONSTRUOS; i++) {
            jugador.agregarMonstruo(new MonstruoNormal("Soldado " + i, 1000, 1000, Posicion.ATAQUE));
        }
        assertThrows(DueloException.class,
                () -> jugador.agregarMonstruo(new MonstruoNormal("Extra", 500, 500, Posicion.ATAQUE)));
    }

    @Test
    void agregarMonstruoNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> jugador.agregarMonstruo(null));
    }

    @Test
    void retirarMonstruoLoSacaDelCampo() throws DueloException {
        Monstruo golem = new MonstruoNormal("Golem de Piedra", 1000, 2000, Posicion.DEFENSA);
        jugador.agregarMonstruo(golem);
        assertTrue(jugador.retirarMonstruo(golem));
        assertFalse(jugador.tieneMonstruos());
    }

    @Test
    void indiceInvalidoLanzaDueloException() {
        assertThrows(DueloException.class, () -> jugador.obtenerMonstruo(0));
        assertThrows(DueloException.class, () -> jugador.obtenerMonstruo(-1));
    }

    @Test
    void campoEsDeSoloLectura() {
        assertThrows(UnsupportedOperationException.class,
                () -> jugador.getCampo().add(new MonstruoNormal("Intruso", 100, 100, Posicion.ATAQUE)));
    }

    @Test
    void reiniciarAtaquesHabilitaATodosLosMonstruos() throws DueloException {
        Monstruo dragon = new MonstruoEfecto("Dragón de Obsidiana", 2400, 1800, Posicion.ATAQUE);
        jugador.agregarMonstruo(dragon);
        dragon.registrarAtaque();
        assertFalse(dragon.puedeAtacar());
        jugador.reiniciarAtaques();
        assertTrue(dragon.puedeAtacar());
    }
}
