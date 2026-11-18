package modelo;

/**
 * Clase abstracta que representa un monstruo en el campo de batalla.
 * Agrupa los atributos comunes (nombre, ATQ, DEF y posición) y deja a las
 * subclases el comportamiento propio de cada tipo de carta.
 *
 * @author Samuel Lopez
 */
public abstract class Monstruo {

    /** Valor máximo permitido para el ATQ y la DEF. */
    public static final int PUNTOS_MAXIMOS = 5000;

    /** Cantidad de monstruos creados durante la ejecución (atributo de clase). */
    private static int totalCreados = 0;

    private String nombre;
    private int ataque;
    private int defensa;
    private Posicion posicion;

    /**
     * Constructor protegido: solo las subclases pueden crear monstruos.
     *
     * @param nombre   nombre del monstruo
     * @param ataque   puntos de ataque (ATQ)
     * @param defensa  puntos de defensa (DEF)
     * @param posicion posición inicial en el campo
     */
    protected Monstruo(String nombre, int ataque, int defensa, Posicion posicion) {
        setNombre(nombre);
        setAtaque(ataque);
        setDefensa(defensa);
        setPosicion(posicion);
        totalCreados++;
    }

    // ------------------------------------------------------------------
    // Métodos abstractos: cada subclase los sobreescribe (polimorfismo)
    // ------------------------------------------------------------------

    /**
     * @return el tipo de carta de monstruo ("Normal" o "Efecto")
     */
    public abstract String getTipo();

    /**
     * Indica si el monstruo inflige daño perforante, es decir, si causa
     * daño a los LP del rival aunque destruya a un monstruo en defensa.
     *
     * @return true si el monstruo tiene daño perforante
     */
    public abstract boolean esPerforante();

    // ------------------------------------------------------------------
    // Lógica propia del monstruo
    // ------------------------------------------------------------------

    /**
     * @return true si el monstruo está en posición de ataque
     */
    public boolean estaEnAtaque() {
        return posicion == Posicion.ATAQUE;
    }

    /**
     * Puntos con los que el monstruo participa en una batalla cuando es
     * atacado: su ATQ si está en ataque o su DEF si está en defensa.
     *
     * @return ATQ o DEF según la posición actual
     */
    public int getPuntosDeBatalla() {
        return estaEnAtaque() ? ataque : defensa;
    }

    // ------------------------------------------------------------------
    // Getters y setters (con validaciones)
    // ------------------------------------------------------------------

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del monstruo no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public int getAtaque() {
        return ataque;
    }

    public void setAtaque(int ataque) {
        validarPuntos(ataque, "ATQ");
        this.ataque = ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public void setDefensa(int defensa) {
        validarPuntos(defensa, "DEF");
        this.defensa = defensa;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException("La posición del monstruo es obligatoria.");
        }
        this.posicion = posicion;
    }

    /**
     * @return cantidad de monstruos creados desde que inició el programa
     */
    public static int getTotalCreados() {
        return totalCreados;
    }

    private static void validarPuntos(int puntos, String campo) {
        if (puntos < 0 || puntos > PUNTOS_MAXIMOS) {
            throw new IllegalArgumentException(campo + " debe estar entre 0 y " + PUNTOS_MAXIMOS + ".");
        }
    }

    @Override
    public String toString() {
        return nombre + " [" + getTipo() + "] ATQ " + ataque + " / DEF " + defensa
                + " - " + (estaEnAtaque() ? "Ataque" : "Defensa");
    }
}
