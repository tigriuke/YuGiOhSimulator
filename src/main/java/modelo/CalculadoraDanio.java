package modelo;

/**
 * Reglas oficiales de cálculo de daño de la Fase de Batalla.
 * <p>
 * Es una clase final (no admite subclases) y no guarda estado: solo
 * calcula, por eso se puede probar de forma independiente con JUnit.
 * <pre>
 * ATQ vs ATQ  atacante mayor : defensor destruido, el rival recibe la diferencia
 *             atacante menor : atacante destruido, el atacante recibe la diferencia
 *             empate         : ambos destruidos, sin daño (si ambos tienen 0 ATQ, ninguno)
 * ATQ vs DEF  ATQ mayor      : defensor destruido, sin daño (salvo daño perforante)
 *             ATQ menor      : nadie es destruido, el atacante recibe la diferencia
 *             empate         : no pasa nada
 * Directo     el rival recibe el ATQ completo del atacante
 * </pre>
 *
 * @author Samuel Lopez
 */
public final class CalculadoraDanio implements ReglasBatalla {

    @Override
    public ResultadoAtaque atacar(Monstruo atacante, Monstruo defensor) {
        validarMonstruo(atacante, "atacante");
        validarMonstruo(defensor, "defensor");
        if (defensor.estaEnAtaque()) {
            return resolverAtaqueContraAtaque(atacante, defensor);
        }
        return resolverAtaqueContraDefensa(atacante, defensor);
    }

    @Override
    public ResultadoAtaque atacar(Monstruo atacante) {
        validarMonstruo(atacante, "atacante");
        int danio = atacante.getAtaque();
        return new ResultadoAtaque(atacante.getNombre(), ResultadoAtaque.ATAQUE_DIRECTO, danio,
                false, false, false,
                atacante.getNombre() + " ataca directamente e inflige " + danio + " de daño.");
    }

    /**
     * Diferencia entre dos valores de batalla (método de clase reutilizable).
     *
     * @param valorA primer valor (ATQ o DEF)
     * @param valorB segundo valor (ATQ o DEF)
     * @return diferencia absoluta entre ambos valores
     */
    public static int calcularDiferencia(int valorA, int valorB) {
        return Math.abs(valorA - valorB);
    }

    private ResultadoAtaque resolverAtaqueContraAtaque(Monstruo atacante, Monstruo defensor) {
        String nombreA = atacante.getNombre();
        String nombreD = defensor.getNombre();
        int atqAtacante = atacante.getAtaque();
        int atqDefensor = defensor.getAtaque();
        int diferencia = calcularDiferencia(atqAtacante, atqDefensor);

        if (atqAtacante > atqDefensor) {
            return new ResultadoAtaque(nombreA, nombreD, diferencia, false, false, true,
                    nombreA + " destruye a " + nombreD + ". El rival recibe " + diferencia + " de daño.");
        }
        if (atqAtacante < atqDefensor) {
            return new ResultadoAtaque(nombreA, nombreD, diferencia, true, true, false,
                    nombreD + " destruye a " + nombreA + ". El atacante recibe " + diferencia + " de daño.");
        }
        if (atqAtacante == 0) {
            return new ResultadoAtaque(nombreA, nombreD, 0, false, false, false,
                    "Ambos monstruos tienen 0 ATQ: ninguno es destruido.");
        }
        return new ResultadoAtaque(nombreA, nombreD, 0, false, true, true,
                "Empate de ATQ: " + nombreA + " y " + nombreD + " se destruyen mutuamente.");
    }

    private ResultadoAtaque resolverAtaqueContraDefensa(Monstruo atacante, Monstruo defensor) {
        String nombreA = atacante.getNombre();
        String nombreD = defensor.getNombre();
        int atq = atacante.getAtaque();
        int def = defensor.getDefensa();
        int diferencia = calcularDiferencia(atq, def);

        if (atq > def) {
            if (atacante.esPerforante()) {
                return new ResultadoAtaque(nombreA, nombreD, diferencia, false, false, true,
                        nombreA + " destruye a " + nombreD + " y su daño perforante inflige "
                        + diferencia + " al rival.");
            }
            return new ResultadoAtaque(nombreA, nombreD, 0, false, false, true,
                    nombreA + " destruye a " + nombreD + " (en defensa). No hay daño a los LP.");
        }
        if (atq < def) {
            return new ResultadoAtaque(nombreA, nombreD, diferencia, true, false, false,
                    "La DEF de " + nombreD + " es mayor. El atacante recibe " + diferencia + " de daño.");
        }
        return new ResultadoAtaque(nombreA, nombreD, 0, false, false, false,
                "El ATQ es igual a la DEF de " + nombreD + ": no hay destrucción ni daño.");
    }

    private static void validarMonstruo(Monstruo monstruo, String rol) {
        if (monstruo == null) {
            throw new IllegalArgumentException("El monstruo " + rol + " no puede ser nulo.");
        }
    }
}
