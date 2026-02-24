package objeto;

public interface Temporizado {
	/**
	 * Atualiza o estado do objeto no tempo.
	 * Deve ser chamado a cada tick do game loop.
	 */
	void atualizar();

	/**
	 * Verifica se o objeto ainda está ativo.
	 * @return true se ativo, false se deve ser removido
	 */
	boolean isAtivo();
}
