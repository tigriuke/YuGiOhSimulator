package principal;

import controlador.ControladorDuelo;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import modelo.CalculadoraDanio;
import modelo.Duelo;
import modelo.HistorialArchivo;
import vista.VistaDuelo;

/**
 * Clase principal: arma el patrón MVC (modelo, vista y controlador).
 *
 * @author Samuel Lopez
 */
public class Main {

    public static void main(String[] args) {
        HistorialArchivo archivo = new HistorialArchivo();

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
}
