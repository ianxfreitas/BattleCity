package mundo;

import java.util.List;
import java.util.Optional;
import objeto.*;

public class GerenciadorColisao {

	public GerenciadorColisao() {
	}

	/**
	 * Verifica colisão entre dois objetos colidíveis.
	 */
	public static boolean verificaColisao(Colidivel obj1, Colidivel obj2) {
		return obj1.getBounds().intersects(obj2.getBounds());
	}


	/**
	 * Verifica colisão entre um objeto e uma lista de objetos.
	 * Retorna o primeiro objeto da lista que colide, ou Optional.empty() se nenhum colidir.
	 */
	public static <T extends Colidivel> Optional<T> verificaColisaoComLista(
		Colidivel objeto, List<T> lista) {
		for (T item : lista) {
			if (verificaColisao(objeto, item)) {
				return Optional.of(item);
			}
		}
		return Optional.empty();
	}

	/**
	 * Verifica colisão entre um tiro e o mapa.
	 * Tiros colidem com: PAREDE, TIJOLO, ACO, BASE
	 * Tiros NÃO colidem com: AGUA, ARVORE, VAZIO
	 */
	public static boolean verificaColisaoComMapa(Tiro tiro, Mapa mapa) {
		int col = tiro.getX() / Mapa.TAMANHO;
		int row = tiro.getY() / Mapa.TAMANHO;

		if (row < 0 || row >= Mapa.LINHAS || col < 0 || col >= Mapa.COLUNAS) {
			return false;
		}

		int tipo = mapa.getMatriz()[row][col];
		// Tiros colidem com: PAREDE, TIJOLO, ACO, BASE
		// Tiros NÃO colidem com: AGUA, ARVORE, VAZIO
		return tipo == Mapa.PAREDE || tipo == Mapa.TIJOLO || 
		       tipo == Mapa.ACO || tipo == Mapa.BASE;
	}

	/**
	 * Verifica colisão entre um tiro do jogador e inimigos.
	 */
	public static Optional<TanqueInimigo> tiroAcertaInimigo(
		Tiro tiro, List<TanqueInimigo> inimigos) {
		if (tiro.getShooter() != Tiro.Shooter.JOGADOR) {
			return Optional.empty();
		}

		for (TanqueInimigo inimigo : inimigos) {
			if (inimigo.estaVivo() && verificaColisao(tiro, inimigo)) {
				return Optional.of(inimigo);
			}
		}
		return Optional.empty();
	}

	/**
	 * Verifica colisão entre um tiro inimigo e o jogador.
	 */
	public static boolean tiroAcertaJogador(Tiro tiro, TanqueJogador jogador) {
		if (tiro.getShooter() != Tiro.Shooter.INIMIGO) {
			return false;
		}

		return jogador.estaVivo() && verificaColisao(tiro, jogador);
	}

	/**
	 * Verifica se um tanque pode se mover para a nova posição sem colidir com o mapa.
	 * Considera os blocos que impedem movimento: PAREDE, AGUA, TIJOLO, ACO, BASE
	 */
	public static boolean podeMoverse(int novaX, int novaY, int largura, int altura, Mapa mapa) {
		int leftCol = novaX / Mapa.TAMANHO;
		int rightCol = (novaX + largura - 1) / Mapa.TAMANHO;
		int topRow = novaY / Mapa.TAMANHO;
		int bottomRow = (novaY + altura - 1) / Mapa.TAMANHO;

		leftCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, leftCol));
		rightCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, rightCol));
		topRow = Math.max(0, Math.min(Mapa.LINHAS - 1, topRow));
		bottomRow = Math.max(0, Math.min(Mapa.LINHAS - 1, bottomRow));

		int[][] mat = mapa.getMatriz();
		for (int r = topRow; r <= bottomRow; r++) {
			for (int c = leftCol; c <= rightCol; c++) {
				int tipoBloco = mat[r][c];
				// Blocos que IMPEDEM movimento: PAREDE, AGUA, TIJOLO, ACO, BASE
				if (tipoBloco == Mapa.PAREDE || tipoBloco == Mapa.AGUA || 
				    tipoBloco == Mapa.TIJOLO || tipoBloco == Mapa.ACO || 
				    tipoBloco == Mapa.BASE) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Verifica colisão entre um tiro e a base.
	 */
	public static boolean tiroAcertaBase(Tiro tiro, Mapa mapa) {
		int col = tiro.getX() / Mapa.TAMANHO;
		int row = tiro.getY() / Mapa.TAMANHO;

		if (row < 0 || row >= Mapa.LINHAS || col < 0 || col >= Mapa.COLUNAS) {
			return false;
		}

		int tipo = mapa.getMatriz()[row][col];
		return tipo == Mapa.BASE;
	}

	/**
	 * Verifica colisão entre dois tanques (para evitar sobreposição).
	 */
	public static boolean tanquesColidem(TanqueJogador jogador, TanqueInimigo inimigo) {
		return jogador.estaVivo() && inimigo.estaVivo() &&
			jogador.getBounds().intersects(inimigo.getBounds());
	}
}
