package objeto;

/**
 * Interface que define o contrato para objetos que podem se mover.
 */
public interface Colidivel {
	/**
	 * Verifica se este objeto colide com outro objeto colidível.
	 * @param outro o outro objeto para verificar a colisão
	 * @return true se colidir, false caso contrário
	 */
	boolean colideCom(Colidivel outro);

	java.awt.Rectangle getBounds();
}
