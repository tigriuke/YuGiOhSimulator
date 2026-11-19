package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Pruebas del resultado inmutable que entregan las reglas de batalla.
 *
 * @author Samuel Lopez
 */
class ResultadoAtaqueTest {

    @Test
    void guardaTodosLosDatosDelResultado() {
        ResultadoAtaque r = new ResultadoAtaque("Dragón", "Guerrero", 500, false, false, true,
                "Dragón destruye a Guerrero.");
        assertEquals("Dragón", r.getAtacante());
        assertEquals("Guerrero", r.getDefensor());
        assertEquals(500, r.getDanio());
        assertFalse(r.isDanioAlAtacante());
        assertFalse(r.isAtacanteDestruido());
        assertTrue(r.isDefensorDestruido());
        assertEquals("Dragón destruye a Guerrero.", r.getDescripcion());
    }

    @Test
    void danioNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResultadoAtaque("A", "B", -1, false, false, false, "inválido"));
    }
}
