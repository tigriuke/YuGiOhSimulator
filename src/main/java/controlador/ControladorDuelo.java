package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import modelo.Duelo;
import modelo.Jugador;
import modelo.Monstruo;
import vista.VistaDuelo;

/**
 * Controlador del patrón MVC. Recibe los eventos de la vista, le pide al
 * modelo que ejecute la acción y luego actualiza la vista con el nuevo
 * estado. No contiene reglas del juego (están en el paquete modelo) ni crea
 * componentes gráficos (están en el JFrame Form).
 *
 * @author Samuel Lopez
 */
public class ControladorDuelo implements ActionListener {

    private final VistaDuelo vista;
    private final Duelo duelo;

    /**
     * @param vista ventana del simulador
     * @param duelo modelo con las reglas y el estado del duelo
     */
    public ControladorDuelo(VistaDuelo vista, Duelo duelo) {
        this.vista = vista;
        this.duelo = duelo;
        this.vista.registrarControlador(this);
    }

    /**
     * Muestra la ventana con el estado inicial.
     */
    public void iniciar() {
        actualizarVista();
        vista.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        try {
            switch (evento.getActionCommand()) {
                case VistaDuelo.CMD_INICIAR:
                    iniciarDuelo();
                    break;
                default:
                    break;
            }
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
        actualizarVista();
    }

    // ------------------------------------------------------------------
    // Acciones
    // ------------------------------------------------------------------

    private void iniciarDuelo() {
        if (duelo.estaIniciado() && !duelo.hayGanador()
                && !vista.confirmar("Hay un duelo en curso. ¿Desea iniciar uno nuevo?")) {
            return;
        }
        duelo.iniciar(vista.getNombreJugador1(), vista.getNombreJugador2());
        vista.limpiarHistorial();
        vista.agregarAlHistorial("Duelo iniciado: " + duelo.getJugador1().getNombre() + " vs "
                + duelo.getJugador2().getNombre() + " (" + Jugador.LP_INICIAL + " LP cada uno).");
    }

    // ------------------------------------------------------------------
    // Sincronización de la vista con el modelo
    // ------------------------------------------------------------------

    private void actualizarVista() {
        List<String> vacio = new ArrayList<>();
        if (!duelo.estaIniciado()) {
            vista.mostrarJugador1("Jugador 1", Jugador.LP_INICIAL, false, vacio);
            vista.mostrarJugador2("Jugador 2", Jugador.LP_INICIAL, false, vacio);
            vista.mostrarTurno("Escriba los nombres e inicie el duelo");
            vista.cargarOpcionesAtacante(vacio);
            vista.cargarOpcionesObjetivo(vacio);
            vista.habilitarAcciones(false, false);
            return;
        }
        Jugador jugador1 = duelo.getJugador1();
        Jugador jugador2 = duelo.getJugador2();
        Jugador enTurno = duelo.getJugadorEnTurno();
        boolean activo = !duelo.hayGanador();

        vista.mostrarJugador1(jugador1.getNombre(), jugador1.getPuntosVida(),
                activo && jugador1 == enTurno, describirCampo(jugador1));
        vista.mostrarJugador2(jugador2.getNombre(), jugador2.getPuntosVida(),
                activo && jugador2 == enTurno, describirCampo(jugador2));
        vista.cargarNombresDuenos(jugador1.getNombre(), jugador2.getNombre());
        vista.habilitarAcciones(activo, activo);
        vista.mostrarTurno("Turno " + duelo.getTurno() + " - Ataca: " + enTurno.getNombre());
    }

    private List<String> describirCampo(Jugador jugador) {
        List<String> lineas = new ArrayList<>();
        for (Monstruo monstruo : jugador.getCampo()) {
            lineas.add(monstruo.getNombre() + " - " + monstruo.getAtaque() + "/" + monstruo.getDefensa()
                    + " - " + (monstruo.estaEnAtaque() ? "Ataque" : "Defensa")
                    + (monstruo.esPerforante() ? " - Efecto" : ""));
        }
        return lineas;
    }
}
