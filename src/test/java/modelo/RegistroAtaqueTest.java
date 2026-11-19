package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la conversión de un registro a texto plano y viceversa.
 *
 * @author Samuel Lopez
 */
class RegistroAtaqueTest {

    @Test
    void lineaDeArchivoYDesdeLineaSonOperacionesInversas() {
        RegistroAtaque original = new RegistroAtaque(3, "Ana", "Dragón", "Guerrero", 500, "Luis",
                "Dragón destruye a Guerrero.");
        RegistroAtaque copia = RegistroAtaque.desdeLinea(original.aLineaArchivo());

        assertEquals(original.getTurno(), copia.getTurno());
        assertEquals(original.getJugadorAtacante(), copia.getJugadorAtacante());
        assertEquals(original.getAtacante(), copia.getAtacante());
        assertEquals(original.getDefensor(), copia.getDefensor());
        assertEquals(original.getDanio(), copia.getDanio());
        assertEquals(original.getJugadorDaniado(), copia.getJugadorDaniado());
        assertEquals(original.getResultado(), copia.getResultado());
    }

    @Test
    void elSeparadorDentroDeUnTextoSeReemplaza() {
        RegistroAtaque registro = new RegistroAtaque(1, "Ana", "Rey;Sombra", "Ataque directo", 100,
                "Luis", "ok");
        RegistroAtaque copia = RegistroAtaque.desdeLinea(registro.aLineaArchivo());
        assertEquals("Rey,Sombra", copia.getAtacante());
    }

    @Test
    void lineaConCamposFaltantesLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> RegistroAtaque.desdeLinea("1;Ana;Dragón"));
    }

    @Test
    void danioNoNumericoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> RegistroAtaque.desdeLinea("1;Ana;Dragón;Guerrero;mucho;Luis;texto"));
    }

    @Test
    void datosInvalidosEnElConstructorLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroAtaque(0, "Ana", "A", "B", 0, "-", "x"));
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroAtaque(1, "Ana", "A", "B", -5, "-", "x"));
        assertThrows(IllegalArgumentException.class,
                () -> new RegistroAtaque(1, null, "A", "B", 0, "-", "x"));
    }

    @Test
    void huboDanioYToString() {
        RegistroAtaque conDanio = new RegistroAtaque(2, "Ana", "Dragón", "Ataque directo", 2500, "Luis", "ok");
        RegistroAtaque sinDanio = new RegistroAtaque(2, "Ana", "Dragón", "Muro", 0, "-", "ok");
        assertTrue(conDanio.huboDanio());
        assertFalse(sinDanio.huboDanio());
        assertTrue(conDanio.toString().contains("Turno 2"));
        assertTrue(conDanio.toString().contains("2500 a Luis"));
    }
}
