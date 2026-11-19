package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import modelo.Duelo;
import modelo.DueloException;
import modelo.Jugador;
import modelo.Monstruo;
import modelo.MonstruoEfecto;
import modelo.MonstruoNormal;
import modelo.Posicion;
import modelo.RegistroAtaque;
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

    /** Cantidad máxima de dígitos que aceptan los campos de ATQ y DEF. */
    private static final int MAX_DIGITOS = 4;

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
        this.vista.registrarFiltroNumerico(new FiltroNumerico());
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
                case VistaDuelo.CMD_AGREGAR:
                    agregarMonstruo();
                    break;
                case VistaDuelo.CMD_ATACAR:
                    atacar();
                    break;
                case VistaDuelo.CMD_CAMBIAR_POSICION:
                    cambiarPosicion();
                    break;
                case VistaDuelo.CMD_TERMINAR_TURNO:
                    terminarTurno();
                    break;
                default:
                    break;
            }
        } catch (DueloException ex) {
            vista.mostrarAdvertencia(ex.getMessage());
        } catch (IOException ex) {
            vista.mostrarError("No se pudo acceder al archivo del historial: " + ex.getMessage());
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

    private void agregarMonstruo() throws DueloException {
        String nombre = vista.getNombreMonstruo();
        int ataque = leerPuntos(vista.getTextoAtaque(), "ATQ");
        int defensa = leerPuntos(vista.getTextoDefensa(), "DEF");
        Posicion posicion = vista.getIndicePosicion() == 0 ? Posicion.ATAQUE : Posicion.DEFENSA;

        Monstruo monstruo;
        if (vista.getIndiceTipo() == 0) {
            monstruo = new MonstruoNormal(nombre, ataque, defensa, posicion);
        } else {
            monstruo = new MonstruoEfecto(nombre, ataque, defensa, posicion);
        }
        int numeroJugador = vista.getIndiceDueno() + 1;
        duelo.agregarMonstruo(numeroJugador, monstruo);
        vista.agregarAlHistorial(duelo.getJugador(numeroJugador).getNombre() + " invoca a " + monstruo + ".");
        vista.limpiarFormularioMonstruo();
    }

    /**
     * Convierte el texto de un campo numérico en entero.
     *
     * @throws IllegalArgumentException si el campo está vacío o no es un número
     */
    private int leerPuntos(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese el valor de " + campo + ".");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(campo + " debe ser un número entero.");
        }
    }

    private void atacar() throws DueloException, IOException {
        int indiceAtacante = vista.getIndiceAtacante();
        Jugador rival = duelo.getRival();
        RegistroAtaque registro;
        if (rival != null && rival.tieneMonstruos()) {
            registro = duelo.atacar(indiceAtacante, vista.getIndiceObjetivo());
        } else {
            registro = duelo.atacar(indiceAtacante);
        }
        vista.agregarAlHistorial(registro.toString());
        if (duelo.hayGanador()) {
            vista.mostrarMensaje("¡" + duelo.getGanador().getNombre() + " gana el duelo!");
        }
    }

    private void cambiarPosicion() throws DueloException {
        int indice = vista.getIndiceAtacante();
        duelo.cambiarPosicion(indice);
        Monstruo monstruo = duelo.getJugadorEnTurno().obtenerMonstruo(indice);
        vista.agregarAlHistorial(monstruo.getNombre() + " pasa a posición de "
                + (monstruo.estaEnAtaque() ? "ataque." : "defensa."));
    }

    private void terminarTurno() throws DueloException {
        duelo.terminarTurno();
        vista.agregarAlHistorial("--- Turno " + duelo.getTurno() + ": ahora ataca "
                + duelo.getJugadorEnTurno().getNombre() + " ---");
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
        if (activo) {
            vista.mostrarTurno("Turno " + duelo.getTurno() + " - Ataca: " + enTurno.getNombre());
        } else {
            vista.mostrarTurno("Fin del duelo - Ganador: " + duelo.getGanador().getNombre());
        }
        vista.cargarOpcionesAtacante(describirAtacantes(enTurno));
        vista.cargarOpcionesObjetivo(describirObjetivos(duelo.getRival()));
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

    private List<String> describirAtacantes(Jugador jugador) {
        List<String> opciones = new ArrayList<>();
        int numero = 1;
        for (Monstruo monstruo : jugador.getCampo()) {
            String estado = "";
            if (monstruo.yaAtaco()) {
                estado = " (ya atacó)";
            } else if (!monstruo.estaEnAtaque()) {
                estado = " (en defensa)";
            }
            opciones.add(numero + ". " + monstruo.getNombre() + " - ATQ " + monstruo.getAtaque() + estado);
            numero++;
        }
        return opciones;
    }

    private List<String> describirObjetivos(Jugador rival) {
        List<String> opciones = new ArrayList<>();
        if (!rival.tieneMonstruos()) {
            opciones.add("Ataque directo a " + rival.getNombre());
            return opciones;
        }
        int numero = 1;
        for (Monstruo monstruo : rival.getCampo()) {
            String puntos = monstruo.estaEnAtaque()
                    ? "ATQ " + monstruo.getAtaque() + " (Ataque)"
                    : "DEF " + monstruo.getDefensa() + " (Defensa)";
            opciones.add(numero + ". " + monstruo.getNombre() + " - " + puntos);
            numero++;
        }
        return opciones;
    }

    /**
     * Filtro de teclado para los campos de ATQ y DEF: descarta cualquier
     * carácter que no sea un dígito y limita la cantidad de cifras.
     */
    private static class FiltroNumerico extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent evento) {
            char tecla = evento.getKeyChar();
            if (Character.isISOControl(tecla)) {
                return; // borrar, tabulador, enter...
            }
            JTextField campo = (JTextField) evento.getSource();
            boolean hayEspacio = campo.getText().length() < MAX_DIGITOS || campo.getSelectedText() != null;
            if (!Character.isDigit(tecla) || !hayEspacio) {
                evento.consume();
            }
        }
    }
}
