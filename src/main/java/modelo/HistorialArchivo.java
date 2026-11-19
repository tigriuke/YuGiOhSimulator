package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia del historial de combate en un archivo de texto plano.
 * Cada ataque ocupa una línea con sus campos separados por ';'.
 *
 * @author Samuel Lopez
 */
public class HistorialArchivo {

    /** Nombre del archivo que exige el enunciado. */
    public static final String NOMBRE_POR_DEFECTO = "historial_combate.txt";

    /** Primera línea del archivo: describe las columnas. */
    private static final String ENCABEZADO =
            "# turno;jugador_atacante;monstruo_atacante;defensor;daño;jugador_que_recibe_daño;resultado";

    private final File archivo;

    /**
     * Usa el archivo historial_combate.txt en la carpeta del proyecto.
     */
    public HistorialArchivo() {
        this(NOMBRE_POR_DEFECTO);
    }

    /**
     * @param ruta ruta del archivo de texto donde se guarda el historial
     */
    public HistorialArchivo(String ruta) {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía.");
        }
        this.archivo = new File(ruta);
    }

    /**
     * Agrega un registro al final del archivo (lo crea si no existe).
     *
     * @param registro ataque a guardar
     * @throws IOException si no se puede escribir el archivo
     */
    public void guardar(RegistroAtaque registro) throws IOException {
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo.");
        }
        boolean archivoNuevo = !archivo.exists() || archivo.length() == 0;
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo, StandardCharsets.UTF_8, true))) {
            if (archivoNuevo) {
                escritor.write(ENCABEZADO);
                escritor.newLine();
            }
            escritor.write(registro.aLineaArchivo());
            escritor.newLine();
        }
    }

    /**
     * Lee todos los registros guardados. Ignora el encabezado, las líneas
     * vacías y las líneas que no tengan el formato correcto.
     *
     * @return lista de registros (vacía si el archivo no existe)
     * @throws IOException si no se puede leer el archivo
     */
    public List<RegistroAtaque> leer() throws IOException {
        List<RegistroAtaque> registros = new ArrayList<>();
        if (!archivo.exists()) {
            return registros;
        }
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                try {
                    registros.add(RegistroAtaque.desdeLinea(linea));
                } catch (IllegalArgumentException ex) {
                    // Línea dañada: se omite para no perder el resto del historial.
                }
            }
        }
        return registros;
    }

    /**
     * Borra el contenido del archivo.
     *
     * @throws IOException si no se puede escribir el archivo
     */
    public void limpiar() throws IOException {
        try (FileWriter escritor = new FileWriter(archivo, StandardCharsets.UTF_8, false)) {
            escritor.flush();
        }
    }

    public boolean existe() {
        return archivo.exists();
    }

    public String getRuta() {
        return archivo.getAbsolutePath();
    }
}
