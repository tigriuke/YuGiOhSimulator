package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la tabla oficial de cálculo de daño.
 *
 * @author Samuel Lopez
 */
class CalculadoraDanioTest {

    /** Se usa la interfaz para probar también el polimorfismo. */
    private final ReglasBatalla reglas = new CalculadoraDanio();

    private static Monstruo enAtaque(String nombre, int atq) {
        return new MonstruoNormal(nombre, atq, 1000, Posicion.ATAQUE);
    }

    private static Monstruo enDefensa(String nombre, int def) {
        return new MonstruoNormal(nombre, 1000, def, Posicion.DEFENSA);
    }

    // ---------------- Ataque vs Ataque ----------------

    @Test
    void ataqueMayorDestruyeAlDefensorYDaniaAlRival() {
        ResultadoAtaque r = reglas.atacar(enAtaque("Dragón", 2500), enAtaque("Guerrero", 2000));
        assertEquals(500, r.getDanio());
        assertFalse(r.isDanioAlAtacante());
        assertTrue(r.isDefensorDestruido());
        assertFalse(r.isAtacanteDestruido());
    }

    @Test
    void ataqueMenorDestruyeAlAtacanteYLoDania() {
        ResultadoAtaque r = reglas.atacar(enAtaque("Guerrero", 1500), enAtaque("Dragón", 2400));
        assertEquals(900, r.getDanio());
        assertTrue(r.isDanioAlAtacante());
        assertTrue(r.isAtacanteDestruido());
        assertFalse(r.isDefensorDestruido());
    }

    @Test
    void empateDeAtaqueDestruyeAAmbosSinDanio() {
        ResultadoAtaque r = reglas.atacar(enAtaque("A", 1800), enAtaque("B", 1800));
        assertEquals(0, r.getDanio());
        assertTrue(r.isAtacanteDestruido());
        assertTrue(r.isDefensorDestruido());
    }

    @Test
    void empateConCeroAtaqueNoDestruyeANinguno() {
        ResultadoAtaque r = reglas.atacar(enAtaque("A", 0), enAtaque("B", 0));
        assertEquals(0, r.getDanio());
        assertFalse(r.isAtacanteDestruido());
        assertFalse(r.isDefensorDestruido());
    }

    // ---------------- Ataque vs Defensa ----------------

    @Test
    void ataqueMayorQueDefensaDestruyeSinDanio() {
        ResultadoAtaque r = reglas.atacar(enAtaque("Dragón", 2000), enDefensa("Muro", 1500));
        assertEquals(0, r.getDanio());
        assertTrue(r.isDefensorDestruido());
        assertFalse(r.isAtacanteDestruido());
    }

    @Test
    void monstruoPerforanteDaniaAunqueElDefensorEsteEnDefensa() {
        Monstruo lancero = new MonstruoEfecto("Lancero", 2000, 800, Posicion.ATAQUE);
        ResultadoAtaque r = reglas.atacar(lancero, enDefensa("Muro", 1500));
        assertEquals(500, r.getDanio());
        assertFalse(r.isDanioAlAtacante());
        assertTrue(r.isDefensorDestruido());
    }

    @Test
    void ataqueMenorQueDefensaDaniaAlAtacanteSinDestruir() {
        ResultadoAtaque r = reglas.atacar(enAtaque("Duende", 1200), enDefensa("Muro", 2000));
        assertEquals(800, r.getDanio());
        assertTrue(r.isDanioAlAtacante());
        assertFalse(r.isAtacanteDestruido());
        assertFalse(r.isDefensorDestruido());
    }

    @Test
    void ataqueIgualALaDefensaNoTieneEfecto() {
        ResultadoAtaque r = reglas.atacar(enAtaque("A", 1500), enDefensa("B", 1500));
        assertEquals(0, r.getDanio());
        assertFalse(r.isAtacanteDestruido());
        assertFalse(r.isDefensorDestruido());
    }

    // ---------------- Ataque directo ----------------

    @Test
    void ataqueDirectoInfligeTodoElAtaque() {
        ResultadoAtaque r = reglas.atacar(enAtaque("Dragón", 3000));
        assertEquals(3000, r.getDanio());
        assertEquals(ResultadoAtaque.ATAQUE_DIRECTO, r.getDefensor());
        assertFalse(r.isDanioAlAtacante());
    }

    // ---------------- Otros ----------------

    @Test
    void calcularDiferenciaEsSiemprePositiva() {
        assertEquals(500, CalculadoraDanio.calcularDiferencia(2000, 2500));
        assertEquals(500, CalculadoraDanio.calcularDiferencia(2500, 2000));
    }

    @Test
    void monstruoNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> reglas.atacar(null));
        assertThrows(IllegalArgumentException.class, () -> reglas.atacar(enAtaque("A", 100), null));
    }
}
