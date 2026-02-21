package mundo;

import java.awt.*;

public abstract class Mapa {

	public static final int LINHAS = 13;
	public static final int COLUNAS = 13;
	public static final int TAMANHO = 60;


    public abstract int[][] getMatriz();


    public void desenhar(Graphics g) {
        int[][] mapa = getMatriz();

        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                int x = coluna * TAMANHO;
                int y = linha * TAMANHO;

                switch (mapa[linha][coluna]) {
                    case 1: // parede
                        g.setColor(Color.GRAY);
                        g.fillRect(x, y, TAMANHO, TAMANHO);
                        break;
                    case 2: // base
                        g.setColor(Color.RED);
                        g.fillRect(x, y, TAMANHO, TAMANHO);
                        break;
                    case 3: // obstáculo
                        g.setColor(Color.ORANGE);
                        g.fillRect(x, y, TAMANHO, TAMANHO);
                        break;
                    default: // chão
                        g.setColor(Color.BLACK);
                        g.fillRect(x, y, TAMANHO, TAMANHO);
                        break;
                }
            }
        }
    }
}
