package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la jerarquía Monstruo / MonstruoNormal / MonstruoEfecto.
 *
 * @author Samuel Lopez
 */
class MonstruoTest {

    private Monstruo normal;
    private Monstruo efecto;

    @BeforeEach
    void setUp() {
        normal = new MonstruoNormal("Guerrero de Bronce", 1800, 1200, Posicion.ATAQUE);
        efecto = new MonstruoEfecto("Lancero Veloz", 1500, 1000, Posicion.DEFENSA);
    }

    @Test
    void constructorAsignaLosAtributos() {
        assertEquals("Guerrero de Bronce", normal.getNombre());
        assertEquals(1800, normal.getAtaque());
        assertEquals(1200, normal.getDefensa());
        assertEquals(Posicion.ATAQUE, normal.getPosicion());
    }

    @Test
    void nombreVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new MonstruoNormal("   ", 1000, 1000, Posicion.ATAQUE));
    }

    @Test
    void puntosFueraDeRangoLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new MonstruoNormal("Golem", -100, 1000, Posicion.ATAQUE));
        assertThrows(IllegalArgumentException.class,
                () -> new MonstruoEfecto("Golem", 1000, Monstruo.PUNTOS_MAXIMOS + 1, Posicion.ATAQUE));
    }

    @Test
    void posicionNulaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new MonstruoNormal("Golem", 1000, 1000, null));
    }

    @Test
    void settersValidanLosPuntos() {
        normal.setAtaque(2500);
        assertEquals(2500, normal.getAtaque());
        assertThrows(IllegalArgumentException.class, () -> normal.setDefensa(-1));
    }

    @Test
    void cadaSubclaseSobreescribeSuTipo() {
        assertEquals("Normal", normal.getTipo());
        assertEquals("Efecto", efecto.getTipo());
    }

    @Test
    void soloElMonstruoDeEfectoEsPerforante() {
        assertFalse(normal.esPerforante());
        assertTrue(efecto.esPerforante());
    }

    @Test
    void puntosDeBatallaDependenDeLaPosicion() {
        assertTrue(normal.estaEnAtaque());
        assertEquals(1800, normal.getPuntosDeBatalla());
        assertFalse(efecto.estaEnAtaque());
        assertEquals(1000, efecto.getPuntosDeBatalla());
    }

    @Test
    void contadorEstaticoAumentaConCadaMonstruo() {
        int antes = Monstruo.getTotalCreados();
        new MonstruoNormal("Golem de Piedra", 1000, 2000, Posicion.DEFENSA);
        assertEquals(antes + 1, Monstruo.getTotalCreados());
    }

    @Test
    void toStringMuestraNombreTipoYPosicion() {
        String texto = efecto.toString();
        assertTrue(texto.contains("Lancero Veloz"));
        assertTrue(texto.contains("Efecto"));
        assertTrue(texto.contains("Defensa"));
    }
}
