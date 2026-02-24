package mundo;

/**
 * Mapa 2 - Com os blocos obrigatórios: Tijolo, Aço, Árvore, Água e Base
 * 0 = Vazio
 * 1 = Parede (borda)
 * 2 = Base (game over se destruída)
 * 3 = Tijolo (destrutível)
 * 4 = Aço (indestrutível sem power-up)
 * 5 = Árvore (passa por cima visualmente)
 * 6 = Água (impede movimento)
 */
public class Mapa2 extends Mapa {

	private static final int[][] MATRIZ = {
		{1,1,1,1,1,1,1,1,1,1,1,1,1},
		{1,0,0,0,4,0,0,4,0,0,0,0,1},
		{1,0,3,0,4,0,0,3,0,0,0,0,1},
		{1,0,0,0,0,0,0,0,0,3,4,0,1},
		{1,5,0,3,6,6,0,0,0,4,0,0,1},
		{1,5,5,0,0,0,0,0,5,5,0,0,1},
		{1,0,3,3,3,3,0,3,0,0,3,0,1},
		{1,0,0,0,4,0,0,0,0,0,3,0,1},
		{1,4,0,0,4,3,0,3,4,0,3,0,1},
		{1,0,0,0,0,0,0,0,0,0,6,0,1},
		{1,0,3,0,0,0,3,0,0,0,0,0,1},
		{1,0,3,0,0,3,2,3,0,3,3,0,1},
		{1,1,1,1,1,1,1,1,1,1,1,1,1},
	};

	@Override
	public int[][] getMatriz() {
		return MATRIZ;
	}
}
