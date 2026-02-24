package mundo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public abstract class Mapa {

	// Imagens dos blocos
	private BufferedImage imgTijolo;
	private BufferedImage imgArvore;
	private BufferedImage imgAgua;
	private BufferedImage imgBase;
	private BufferedImage imgAco;
	public static final int LINHAS = 13;
	public static final int COLUNAS = 13;
	public static final int TAMANHO = 64;

	public static final int VAZIO = 0;
	public static final int PAREDE = 1;     // Parede fixa (borda)
	public static final int BASE = 2;       // Base (game over se destruída)
	public static final int TIJOLO = 3;     // Tijolo destrutível
	public static final int ACO = 4;        // Aço (indestrutível sem power-up)
	public static final int ARVORE = 5;     // Árvore (passa por cima visualmente)
	public static final int AGUA = 6;       // Água (impede movimento)

	public Mapa() {
		carregarImagens();
	}

	public abstract int[][] getMatriz();

	public void desenhar(Graphics g) {
		int[][] mapa = getMatriz();

		for (int linha = 0; linha < LINHAS; linha++) {
			for (int coluna = 0; coluna < COLUNAS; coluna++) {
				int x = coluna * TAMANHO;
				int y = linha * TAMANHO;

				switch (mapa[linha][coluna]) {
					case PAREDE:
						g.setColor(Color.GRAY);
						g.fillRect(x, y, TAMANHO, TAMANHO);
						g.setColor(Color.DARK_GRAY);
						g.drawRect(x, y, TAMANHO, TAMANHO);
						break;

					case BASE:
						if (imgBase != null) {
							g.drawImage(imgBase, x, y, TAMANHO, TAMANHO, null);
						} else {
							g.setColor(Color.RED);
							g.fillRect(x, y, TAMANHO, TAMANHO);
							g.setColor(Color.YELLOW);
							int cx = x + TAMANHO / 2;
							int cy = y + TAMANHO / 2;
							g.fillOval(cx - 4, cy - 4, 8, 8);
							g.drawLine(cx - 6, cy, cx - 2, cy + 2);
							g.drawLine(cx + 6, cy, cx + 2, cy + 2);
						}
						break;

					case TIJOLO:
						if (imgTijolo != null) {
							g.drawImage(imgTijolo, x, y, TAMANHO, TAMANHO, null);
						} else {
							g.setColor(new Color(139, 69, 19));
							g.fillRect(x, y, TAMANHO, TAMANHO);
							g.setColor(Color.BLACK);
							g.drawRect(x + 2, y + 2, TAMANHO - 4, TAMANHO - 4);
						}
						break;

					case ACO:
						if (imgAco != null) {
							g.drawImage(imgAco, x, y, TAMANHO, TAMANHO, null);
						} else {
							g.setColor(Color.LIGHT_GRAY);
							g.fillRect(x, y, TAMANHO, TAMANHO);
							g.setColor(Color.BLACK);
							for (int i = 0; i < TAMANHO; i += 6) {
								g.drawLine(x + i, y, x + i + 3, y + TAMANHO);
								g.drawLine(x, y + i, x + TAMANHO, y + i + 3);
							}
						}
						break;

					case ARVORE:
						if (imgArvore != null) {

							g.drawImage(imgArvore, x, y, TAMANHO, TAMANHO, null);
						} else {
							g.setColor(new Color(34, 139, 34));
							g.fillRect(x, y, TAMANHO, TAMANHO);
							g.setColor(new Color(0, 100, 0));
							g.fillOval(x + 4, y + 4, TAMANHO - 8, TAMANHO - 8);
						}
						break;

					case AGUA:
						if (imgAgua != null) {
							g.drawImage(imgAgua, x, y, TAMANHO, TAMANHO, null);
						} else {
							g.setColor(new Color(0, 100, 255));
							g.fillRect(x, y, TAMANHO, TAMANHO);
							g.setColor(new Color(0, 150, 255));
							for (int i = 0; i < TAMANHO; i += 8) {
								g.drawLine(x + i, y, x + i + 4, y + TAMANHO);
							}
						}
						break;

					default:
						g.setColor(Color.BLACK);
						g.fillRect(x, y, TAMANHO, TAMANHO);
						break;
				}
			}
		}
	}

	protected void carregarImagens() {
		try {
			InputStream is = getClass().getResourceAsStream("/res/tijolo.png");
			if (is != null) {
				imgTijolo = ImageIO.read(is);
			} else {
				imgTijolo = null;
			}
		} catch (Exception e) {
			imgTijolo = null;
		}

		try {
			InputStream is = getClass().getResourceAsStream("/res/arvore.png");
			if (is != null) {
				imgArvore = ImageIO.read(is);
			} else {
				imgArvore = null;
			}
		} catch (Exception e) {
			imgArvore = null;
		}

		try {
			InputStream is = getClass().getResourceAsStream("/res/agua.png");
			if (is != null) {
				imgAgua = ImageIO.read(is);
			} else {
				imgAgua = null;
			}
		} catch (Exception e) {
			imgAgua = null;
		}

		try {
			InputStream is = getClass().getResourceAsStream("/res/base.png");
			if (is != null) {
				imgBase = ImageIO.read(is);
			} else {
				imgBase = null;
			}
		} catch (Exception e) {
			imgBase = null;
		}

		try {
			InputStream is = getClass().getResourceAsStream("/res/aco.png");
			if (is != null) {
				imgAco = ImageIO.read(is);
			} else {
				imgAco = null;
			}
		} catch (Exception e) {
			imgAco = null;
		}
	}

	public void desenharArvores(Graphics g) {
		int[][] mat = getMatriz();

		for (int l = 0; l < LINHAS; l++) {
			for (int c = 0; c < COLUNAS; c++) {
				if (mat[l][c] == 5) {
					int x = c * TAMANHO;
					int y = l * TAMANHO;

					if (imgArvore != null) {
						g.drawImage(imgArvore, x, y, TAMANHO, TAMANHO, null);
					} else {
						g.setColor(new Color(34, 139, 34));
						g.fillRect(x, y, TAMANHO, TAMANHO);
						g.setColor(new Color(0, 100, 0));
						g.fillOval(x + 4, y + 4, TAMANHO - 8, TAMANHO - 8);
					}
				}
			}
		}
	}
}
