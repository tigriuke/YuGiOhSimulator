package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas de la persistencia del historial en el archivo de texto.
 *
 * @author Samuel Lopez
 */
class HistorialArchivoTest {

    @TempDir
    Path carpetaTemporal;

    private Path ruta;
    private HistorialArchivo archivo;

    @BeforeEach
    void setUp() {
        ruta = carpetaTemporal.resolve("historial_combate.txt");
        archivo = new HistorialArchivo(ruta.toString());
    }

    private static RegistroAtaque registro(int turno, int danio) {
        return new RegistroAtaque(turno, "Ana", "Dragón", "Guerrero", danio, "Luis", "Resultado " + turno);
    }

    @Test
    void leerUnArchivoQueNoExisteDevuelveListaVacia() throws IOException {
        assertFalse(archivo.existe());
        assertTrue(archivo.leer().isEmpty());
    }

    @Test
    void guardarYLeerConservaLosDatos() throws IOException {
        archivo.guardar(registro(1, 500));
        archivo.guardar(registro(2, 1200));

        List<RegistroAtaque> leidos = archivo.leer();
        assertEquals(2, leidos.size());
        assertEquals(1, leidos.get(0).getTurno());
        assertEquals(1200, leidos.get(1).getDanio());
        assertEquals("Luis", leidos.get(1).getJugadorDaniado());
    }

    @Test
    void elArchivoEmpiezaConUnEncabezado() throws IOException {
        archivo.guardar(registro(1, 500));
        List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        assertTrue(lineas.get(0).startsWith("#"));
        assertEquals(2, lineas.size());
    }

    @Test
    void lineasDaniadasSeIgnoran() throws IOException {
        Files.write(ruta, Arrays.asList(
                "# encabezado",
                "esto no es un registro",
                "1;Ana;Dragón;Guerrero;500;Luis;Gana Dragón",
                "x;Ana;Dragón;Guerrero;500;Luis;turno no numérico",
                ""), StandardCharsets.UTF_8);
        List<RegistroAtaque> leidos = archivo.leer();
        assertEquals(1, leidos.size());
        assertEquals("Gana Dragón", leidos.get(0).getResultado());
    }

    @Test
    void limpiarDejaElArchivoVacio() throws IOException {
        archivo.guardar(registro(1, 500));
        archivo.limpiar();
        assertTrue(archivo.leer().isEmpty());
    }

    @Test
    void rutaVaciaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new HistorialArchivo(" "));
    }

    @Test
    void guardarRegistroNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> archivo.guardar(null));
    }

    @Test
    void constructorPorDefectoUsaHistorialCombateTxt() {
        HistorialArchivo porDefecto = new HistorialArchivo();
        assertTrue(porDefecto.getRuta().endsWith(HistorialArchivo.NOMBRE_POR_DEFECTO));
    }

    @Test
    void getRutaDevuelveLaRutaAbsoluta() {
        assertEquals(ruta.toAbsolutePath().toString(), archivo.getRuta());
    }
}
