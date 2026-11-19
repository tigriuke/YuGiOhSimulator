package modelo;

/**
 * Resultado matemático de una batalla, calculado por {@link ReglasBatalla}.
 * Es inmutable: solo informa el daño, quién lo recibe y qué monstruos se
 * destruyen. Aplicar esos efectos sobre los jugadores le corresponde a
 * {@link Duelo}.
 *
 * @author Samuel Lopez
 */
public class ResultadoAtaque {

    /** Texto que se usa como defensor cuando el ataque es directo. */
    public static final String ATAQUE_DIRECTO = "Ataque directo";

    private final String atacante;
    private final String defensor;
    private final int danio;
    private final boolean danioAlAtacante;
    private final boolean atacanteDestruido;
    private final boolean defensorDestruido;
    private final String descripcion;

    /**
     * @param atacante          nombre del monstruo atacante
     * @param defensor          nombre del monstruo defensor o {@link #ATAQUE_DIRECTO}
     * @param danio             daño de batalla (0 si no hay daño)
     * @param danioAlAtacante   true si el daño lo recibe el jugador atacante
     * @param atacanteDestruido true si el monstruo atacante es destruido
     * @param defensorDestruido true si el monstruo defensor es destruido
     * @param descripcion       explicación legible del resultado
     */
    public ResultadoAtaque(String atacante, String defensor, int danio, boolean danioAlAtacante,
            boolean atacanteDestruido, boolean defensorDestruido, String descripcion) {
        if (danio < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo.");
        }
        this.atacante = atacante;
        this.defensor = defensor;
        this.danio = danio;
        this.danioAlAtacante = danioAlAtacante;
        this.atacanteDestruido = atacanteDestruido;
        this.defensorDestruido = defensorDestruido;
        this.descripcion = descripcion;
    }

    public String getAtacante() {
        return atacante;
    }

    public String getDefensor() {
        return defensor;
    }

    public int getDanio() {
        return danio;
    }

    public boolean isDanioAlAtacante() {
        return danioAlAtacante;
    }

    public boolean isAtacanteDestruido() {
        return atacanteDestruido;
    }

    public boolean isDefensorDestruido() {
        return defensorDestruido;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
