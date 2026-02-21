package objeto;

/**
 * Interface que define o contrato para objetos que podem se mover.
 */
public interface Colidivel {
	/**
	 * Verifica se este objeto colide com outro.
	 * @param outro o outro objeto a verificar colisão
	 * @return true se há colisão, false caso contrário
	 */
	boolean colideCom(Colidivel outro);

	/**
	 * Obtém os limites (bounding box) do objeto.
	 * @return um Rectangle representando os limites
	 */
	java.awt.Rectangle getBounds();
}
