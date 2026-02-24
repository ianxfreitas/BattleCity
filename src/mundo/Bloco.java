package mundo;

import java.awt.*;

/**
 * Representa um bloco/tile no mapa do jogo.
 * Cada bloco tem um tipo que determina suas propriedades.
 */
public class Bloco {

	public enum TipoBloco {
		VAZIO(0, "Vazio", true),
		PAREDE(1, "Parede", true),
		BASE(2, "Base", true),
		TIJOLO(3, "Tijolo", false),
		ACO(4, "Aço", false),
		ARVORE(5, "Árvore", false),
		AGUA(6, "Água", false);

		public final int codigo;
		public final String nome;
		public final boolean impassavel;

		TipoBloco(int codigo, String nome, boolean impassavel) {
			this.codigo = codigo;
			this.nome = nome;
			this.impassavel = impassavel;
		}

		public static TipoBloco fromCodigo(int codigo) {
			for (TipoBloco tipo : TipoBloco.values()) {
				if (tipo.codigo == codigo) return tipo;
			}
			return VAZIO;
		}
	}

	private int x; // posição em pixels (coluna * tamanho)
	private int y; // posição em pixels (linha * tamanho)
	private int tamanho;
	private TipoBloco tipo;
	private int vida;
	private boolean ativo;

	public Bloco(int x, int y, int tamanho, TipoBloco tipo) {
		this.x = x;
		this.y = y;
		this.tamanho = tamanho;
		this.tipo = tipo;
		this.ativo = true;

		switch (tipo) {
			case TIJOLO -> this.vida = 1;
			case ACO -> this.vida = 2;
			default -> this.vida = 0;
		}
	}

	public void desenhar(Graphics g) {
		if (!ativo) return;

		Graphics2D g2d = (Graphics2D) g;
		switch (tipo) {
			case PAREDE:
				g.setColor(Color.GRAY);
				g.fillRect(x, y, tamanho, tamanho);
				g.setColor(Color.DARK_GRAY);
				g2d.setStroke(new java.awt.BasicStroke(2));
				g.drawRect(x, y, tamanho, tamanho);
				break;

			case TIJOLO:
				g.setColor(new Color(139, 69, 19)); // marrom tijolo
				g.fillRect(x, y, tamanho, tamanho);
				g.setColor(Color.BLACK);
				g.drawRect(x + 2, y + 2, tamanho - 4, tamanho - 4);
				break;

			case ACO:
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(x, y, tamanho, tamanho);
				g.setColor(Color.BLACK);
				for (int i = 0; i < tamanho; i += 6) {
					g.drawLine(x + i, y, x + i + 3, y + tamanho);
					g.drawLine(x, y + i, x + tamanho, y + i + 3);
				}
				break;

			case ARVORE:
				g.setColor(new Color(34, 139, 34)); // floresta verde
				g.fillRect(x, y, tamanho, tamanho);
				g.setColor(new Color(0, 100, 0));
				g.fillOval(x + 4, y + 4, tamanho - 8, tamanho - 8);
				break;

			case AGUA:
				g.setColor(new Color(0, 100, 255)); // azul água
				g.fillRect(x, y, tamanho, tamanho);
				g.setColor(new Color(0, 150, 255));
				for (int i = 0; i < tamanho; i += 8) {
					g.drawLine(x + i, y, x + i + 4, y + tamanho);
				}
				break;

			case BASE:
				g.setColor(Color.RED);
				g.fillRect(x, y, tamanho, tamanho);
				g.setColor(Color.YELLOW);
				// Desenha uma águia/fênix simples
				int cx = x + tamanho / 2;
				int cy = y + tamanho / 2;
				g.fillOval(cx - 4, cy - 4, 8, 8); // cabeça
				g.drawLine(cx - 6, cy, cx - 2, cy + 2);
				g.drawLine(cx + 6, cy, cx + 2, cy + 2);
				break;

			default:
				break;
		}
	}

	public void tomarDano(int dano) {
		if (tipo == TipoBloco.TIJOLO || tipo == TipoBloco.ACO) {
			vida -= dano;
			if (vida <= 0) {
				ativo = false;
			}
		}
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, tamanho, tamanho);
	}

	public int getX() { return x; }
	public int getY() { return y; }
	public int getTamanho() { return tamanho; }
	public TipoBloco getTipo() { return tipo; }
	public boolean isAtivo() { return ativo; }
	public int getVida() { return vida; }
	public boolean isPassavel() { return !tipo.impassavel; }
}
