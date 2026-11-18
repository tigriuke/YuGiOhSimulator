package modelo;

/**
 * Monstruo de efecto con daño perforante: cuando destruye a un monstruo en
 * posición de defensa, la diferencia entre su ATQ y la DEF del defensor
 * también se resta de los LP del rival.
 *
 * @author Samuel Lopez
 */
public class MonstruoEfecto extends Monstruo {

    /** Texto que describe el efecto de este tipo de monstruo. */
    public static final String DESCRIPCION_EFECTO =
            "Daño perforante: inflige la diferencia ATQ - DEF al destruir un monstruo en defensa.";

    /**
     * Crea un monstruo de efecto.
     *
     * @param nombre   nombre del monstruo
     * @param ataque   puntos de ataque (ATQ)
     * @param defensa  puntos de defensa (DEF)
     * @param posicion posición inicial en el campo
     */
    public MonstruoEfecto(String nombre, int ataque, int defensa, Posicion posicion) {
        super(nombre, ataque, defensa, posicion);
    }

    @Override
    public String getTipo() {
        return "Efecto";
    }

    @Override
    public boolean esPerforante() {
        return true;
    }
}
