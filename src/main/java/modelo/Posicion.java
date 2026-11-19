package modelo;

/**
 * Posiciones de batalla en las que puede estar un monstruo.
 *
 * @author Samuel Lopez
 */
public enum Posicion {
    ATAQUE,
    DEFENSA;

    /**
     * Devuelve la posición opuesta (ATAQUE pasa a DEFENSA y viceversa).
     *
     * @return la posición contraria a la actual
     */
    public Posicion contraria() {
        return this == ATAQUE ? DEFENSA : ATAQUE;
    }
}
