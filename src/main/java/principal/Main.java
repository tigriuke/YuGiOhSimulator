package principal;

import controlador.ControladorDuelo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import modelo.CalculadoraDanio;
import modelo.Duelo;
import modelo.HistorialArchivo;
import modelo.RegistroAtaque;
import vista.VistaDuelo;

/**
 * Clase principal: arma el patrón MVC (modelo, vista y controlador) y genera
 * dos reportes sobre los ataques guardados en el historial de combate.
 *
 * @author Samuel Lopez
 */
public class Main {

    public static void main(String[] args) {
        HistorialArchivo archivo = new HistorialArchivo();

        // Reportes al iniciar (ataques de sesiones anteriores) y al cerrar la ventana
        imprimirReportes("REPORTES AL INICIAR", archivo);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> imprimirReportes("REPORTES AL CERRAR", archivo)));

        Duelo duelo = new Duelo(new CalculadoraDanio(), archivo);
        aplicarLookAndFeel();
        java.awt.EventQueue.invokeLater(() -> {
            VistaDuelo vista = new VistaDuelo();
            ControladorDuelo controlador = new ControladorDuelo(vista, duelo);
            controlador.iniciar();
        });
    }

    /** Usa el estilo Nimbus de Swing si está disponible. */
    private static void aplicarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ex) {
            System.out.println("No se pudo aplicar Nimbus, se usa el estilo por defecto.");
        }
    }

    // ------------------------------------------------------------------
    // Reportes sobre los objetos guardados en estructuras dinámicas
    // ------------------------------------------------------------------

    private static void imprimirReportes(String titulo, HistorialArchivo archivo) {
        try {
            List<RegistroAtaque> registros = archivo.leer();
            System.out.println();
            System.out.println("==================== " + titulo + " ====================");
            System.out.println(reporteDanioPorJugador(registros));
            System.out.println(reporteAtaquesMasFuertes(registros, 5));
        } catch (IOException ex) {
            System.out.println("No se pudo leer el historial: " + ex.getMessage());
        }
    }

    /**
     * Reporte 1: por cada jugador, cuántos ataques declaró y cuánto daño
     * recibió en total.
     *
     * @param registros ataques guardados
     * @return texto del reporte
     */
    public static String reporteDanioPorJugador(List<RegistroAtaque> registros) {
        Map<String, Integer> ataquesDeclarados = new LinkedHashMap<>();
        Map<String, Integer> danioRecibido = new LinkedHashMap<>();
        for (RegistroAtaque registro : registros) {
            String atacante = registro.getJugadorAtacante();
            ataquesDeclarados.put(atacante, ataquesDeclarados.getOrDefault(atacante, 0) + 1);
            danioRecibido.putIfAbsent(atacante, 0);
            if (registro.huboDanio()) {
                String daniado = registro.getJugadorDaniado();
                danioRecibido.put(daniado, danioRecibido.getOrDefault(daniado, 0) + registro.getDanio());
            }
        }

        StringBuilder texto = new StringBuilder("Reporte 1 - Ataques declarados y daño recibido por jugador\n");
        if (registros.isEmpty()) {
            return texto.append("  (no hay ataques registrados)").toString();
        }
        for (String jugador : danioRecibido.keySet()) {
            texto.append(String.format("  %-15s ataques: %3d   daño recibido: %6d%n",
                    jugador, ataquesDeclarados.getOrDefault(jugador, 0), danioRecibido.get(jugador)));
        }
        return texto.toString();
    }

    /**
     * Reporte 2: los ataques que más daño causaron, de mayor a menor.
     *
     * @param registros ataques guardados
     * @param cantidad  cuántos ataques mostrar como máximo
     * @return texto del reporte
     */
    public static String reporteAtaquesMasFuertes(List<RegistroAtaque> registros, int cantidad) {
        List<RegistroAtaque> conDanio = new ArrayList<>();
        for (RegistroAtaque registro : registros) {
            if (registro.huboDanio()) {
                conDanio.add(registro);
            }
        }
        conDanio.sort((a, b) -> Integer.compare(b.getDanio(), a.getDanio()));

        StringBuilder texto = new StringBuilder("Reporte 2 - Top " + cantidad + " ataques con más daño\n");
        if (conDanio.isEmpty()) {
            return texto.append("  (ningún ataque ha causado daño)").toString();
        }
        for (int i = 0; i < Math.min(cantidad, conDanio.size()); i++) {
            RegistroAtaque r = conDanio.get(i);
            texto.append(String.format("  %d. %s (%s) -> %s : %d de daño a %s%n",
                    i + 1, r.getAtacante(), r.getJugadorAtacante(), r.getDefensor(),
                    r.getDanio(), r.getJugadorDaniado()));
        }
        return texto.toString();
    }
}
