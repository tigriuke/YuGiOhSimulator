package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Jugador del duelo. Administra sus Puntos de Vida (LP) y los monstruos que
 * tiene en el campo.
 *
 * @author Samuel Lopez
 */
public class Jugador {

    /** LP con los que empieza todo jugador (regla oficial: 8000). */
    public static final int LP_INICIAL = 8000;

    /** Cantidad máxima de monstruos en el campo (zonas de monstruo). */
    public static final int MAX_MONSTRUOS = 5;

    private final String nombre;
    private int puntosVida;
    private final List<Monstruo> campo;

    /**
     * Crea un jugador con los LP iniciales.
     *
     * @param nombre nombre del jugador
     */
    public Jugador(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío.");
        }
        this.nombre = nombre.trim();
        this.puntosVida = LP_INICIAL;
        this.campo = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntosVida() {
        return puntosVida;
    }

    // ------------------------------------------------------------------
    // Gestión del campo
    // ------------------------------------------------------------------

    /**
     * Coloca un monstruo en el campo del jugador.
     *
     * @param monstruo monstruo a invocar
     * @throws DueloException si el campo ya está lleno
     */
    public void agregarMonstruo(Monstruo monstruo) throws DueloException {
        if (monstruo == null) {
            throw new IllegalArgumentException("El monstruo no puede ser nulo.");
        }
        if (campo.size() >= MAX_MONSTRUOS) {
            throw new DueloException(nombre + " ya tiene " + MAX_MONSTRUOS + " monstruos en el campo.");
        }
        campo.add(monstruo);
    }

    /**
     * Envía un monstruo destruido al cementerio (lo retira del campo).
     *
     * @param monstruo monstruo a retirar
     * @return true si el monstruo estaba en el campo
     */
    public boolean retirarMonstruo(Monstruo monstruo) {
        return campo.remove(monstruo);
    }

    /**
     * @param indice posición del monstruo en el campo (desde 0)
     * @return el monstruo ubicado en ese índice
     * @throws DueloException si el índice no corresponde a ningún monstruo
     */
    public Monstruo obtenerMonstruo(int indice) throws DueloException {
        if (indice < 0 || indice >= campo.size()) {
            throw new DueloException("Seleccione un monstruo válido del campo de " + nombre + ".");
        }
        return campo.get(indice);
    }

    public boolean tieneMonstruos() {
        return !campo.isEmpty();
    }

    public int cantidadMonstruos() {
        return campo.size();
    }

    /**
     * @return vista de solo lectura de los monstruos en el campo
     */
    public List<Monstruo> getCampo() {
        return Collections.unmodifiableList(campo);
    }

    // ------------------------------------------------------------------
    // Gestión de los Puntos de Vida (LP)
    // ------------------------------------------------------------------

    /**
     * Resta daño a los LP. Los LP nunca quedan por debajo de 0.
     *
     * @param danio cantidad de daño recibido (no negativo)
     */
    public void recibirDanio(int danio) {
        if (danio < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo.");
        }
        puntosVida = Math.max(0, puntosVida - danio);
    }

    /**
     * @return true si el jugador se quedó sin LP y perdió el duelo
     */
    public boolean estaDerrotado() {
        return puntosVida == 0;
    }
}
