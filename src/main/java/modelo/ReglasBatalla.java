package modelo;

/**
 * Contrato de las reglas de cálculo de daño de la Fase de Batalla.
 * Gracias a esta interfaz, {@link Duelo} no depende de una implementación
 * concreta y las reglas se pueden probar o reemplazar por separado.
 *
 * @author Samuel Lopez
 */
public interface ReglasBatalla {

    /**
     * Resuelve el ataque de un monstruo contra otro monstruo.
     *
     * @param atacante monstruo que declara el ataque
     * @param defensor monstruo atacado
     * @return daño, jugador afectado y monstruos destruidos
     */
    ResultadoAtaque atacar(Monstruo atacante, Monstruo defensor);

    /**
     * Resuelve un ataque directo a los LP del rival (sobrecarga).
     *
     * @param atacante monstruo que ataca directamente
     * @return resultado con el daño infligido al rival
     */
    ResultadoAtaque atacar(Monstruo atacante);
}
