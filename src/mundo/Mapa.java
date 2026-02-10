package mundo;

import java.awt.*;

public class Mapa {

    // tamanho do mapa em tiles
    public static final int LINHAS = 13;
    public static final int COLUNAS = 13;

    // tamanho de cada bloco em pixels
    public static final int TAMANHO = 40;

    // matriz 13x13 (exemplo)
    private int[][] mapa = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,2,0,0,0,3,0,0,0,2,0,1},
            {1,0,0,0,1,1,1,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,3,0,0,0,1},
            {1,0,3,0,0,1,0,0,0,0,3,0,1},
            {1,0,0,0,0,0,0,1,0,0,0,0,1},
            {1,0,0,3,0,0,0,0,0,3,0,0,1},
            {1,0,0,0,0,1,0,0,0,0,0,0,1},
            {1,0,3,0,0,0,0,3,0,0,3,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,3,0,0,0,3,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public void desenhar(Graphics g) {
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
