package objeto;

/**
 * Interface que define o contrato para objetos que podem atirar.
 */
public interface Atirador {
	/**
	 * Realiza um tiro baseado na direção atual do atirador.
	 * @return o projétil criado, ou null se não pode atirar
	 */
	Tiro atirar();

	/**
	 * Verifica se o atirador deseja disparar.
	 * @return true se quer disparar, false caso contrário
	 */
	boolean wantsToShoot();

	/**
	 * Reseta o cooldown de tiro.
	 */
	void resetShootCooldown();
}
