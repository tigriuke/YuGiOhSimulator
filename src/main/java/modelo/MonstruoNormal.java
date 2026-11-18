package modelo;

/**
 * Monstruo sin efecto especial: combate únicamente con su ATQ y su DEF.
 *
 * @author Samuel Lopez
 */
public class MonstruoNormal extends Monstruo {

    /**
     * Crea un monstruo normal.
     *
     * @param nombre   nombre del monstruo
     * @param ataque   puntos de ataque (ATQ)
     * @param defensa  puntos de defensa (DEF)
     * @param posicion posición inicial en el campo
     */
    public MonstruoNormal(String nombre, int ataque, int defensa, Posicion posicion) {
        super(nombre, ataque, defensa, posicion);
    }

    @Override
    public String getTipo() {
        return "Normal";
    }

    @Override
    public boolean esPerforante() {
        return false;
    }
}
