package objeto;

import java.awt.*;
import java.util.Random;
import mundo.Mapa;

/**
 * Representa um tanque inimigo com IA.
 * Diferentes tipos de inimigos com comportamentos distintos.
 */
public class TanqueInimigo extends ObjetoJogo implements Atirador {

	public enum Direcao { CIMA, BAIXO, ESQUERDA, DIREITA }

	public enum TipoInimigo {
		NORMAL(1, 2, "Normal"),
		RAPIDO(1, 4, "Rápido"),
		ARMADO(2, 1, "Armado"),
		BLINDADO(3, 1, "Blindado");

		public final int vida;
		public final int velocidade;
		public final String nome;

		TipoInimigo(int vida, int velocidade, String nome) {
			this.vida = vida;
			this.velocidade = velocidade;
			this.nome = nome;
		}
	}

	private TipoInimigo tipo;
	private Direcao direcao = Direcao.BAIXO;
	private int velocidade; // pixels por atualização
	private Random rnd = new Random();

	// timers em ticks (cada tick é ~16ms)
	private int changeDirCooldown = 0;
	private int shootCooldown = 0;
	private boolean wantsToShoot = false;

	public TanqueInimigo(int x, int y, int tamanho) {
		this(x, y, tamanho, TipoInimigo.NORMAL);
	}

	public TanqueInimigo(int x, int y, int tamanho, TipoInimigo tipo) {
		super(x, y, tamanho, tamanho);
		this.tipo = tipo;
		this.velocidade = tipo.velocidade;
		this.vida = tipo.vida;
		this.direcao = Direcao.BAIXO;
		this.changeDirCooldown = rnd.nextInt(60) + 30;
		this.shootCooldown = rnd.nextInt(120) + 60;
	}

	@Override
	public void atualizar() {
		// decrementa contadores
		if (changeDirCooldown > 0) changeDirCooldown--;
		if (shootCooldown > 0) shootCooldown--;

		if (changeDirCooldown <= 0) {
			// escolhe nova direção aleatória
			int v = rnd.nextInt(4);
			switch (v) {
				case 0: direcao = Direcao.CIMA; break;
				case 1: direcao = Direcao.BAIXO; break;
				case 2: direcao = Direcao.ESQUERDA; break;
				default: direcao = Direcao.DIREITA; break;
			}
			changeDirCooldown = rnd.nextInt(80) + 20;
		}

		if (shootCooldown <= 0) {
			wantsToShoot = true;
		}
	}

	@Override
	public void desenhar(Graphics g) {
		// Cores diferentes por tipo
		Color cor = switch (tipo) {
			case NORMAL -> Color.RED;
			case RAPIDO -> Color.ORANGE;
			case ARMADO -> Color.MAGENTA;
			case BLINDADO -> Color.DARK_GRAY;
		};

		g.setColor(cor);
		g.fillRect(x, y, largura, altura);

		// Desenhar canhão na direção atual
		g.setColor(Color.YELLOW);
		int cx = x + largura / 2;
		int cy = y + altura / 2;
		int canoSize = largura / 3;

		switch (direcao) {
			case CIMA:
				g.fillRect(cx - 1, y - canoSize, 2, canoSize);
				break;
			case BAIXO:
				g.fillRect(cx - 1, y + altura, 2, canoSize);
				break;
			case ESQUERDA:
				g.fillRect(x - canoSize, cy - 1, canoSize, 2);
				break;
			case DIREITA:
				g.fillRect(x + largura, cy - 1, canoSize, 2);
				break;
		}

		// Indicador de tipo (número de barrinhas)
		g.setColor(Color.WHITE);
		for (int i = 0; i < vida; i++) {
			g.drawRect(x + 2 + i * 3, y + 2, 2, 2);
		}
	}

	// Move na direção atual, com checagem de colisão
	public void moveIfPossible(Mapa mapa) {
		int dx = 0, dy = 0;
		switch (direcao) {
			case CIMA: dy = -velocidade; break;
			case BAIXO: dy = velocidade; break;
			case ESQUERDA: dx = -velocidade; break;
			case DIREITA: dx = velocidade; break;
		}

		if (dx == 0 && dy == 0) return;

		int mapaW = Mapa.COLUNAS * Mapa.TAMANHO;
		int mapaH = Mapa.LINHAS * Mapa.TAMANHO;

		int[][] mat = mapa.getMatriz();

		// Move X separadamente
		if (dx != 0) {
			int novoX = x + dx;
			if (novoX >= 0 && novoX + largura <= mapaW) {
				if (!verificaColisao(novoX, y, mat)) {
					x = novoX;
				} else {
					changeDirCooldown = 0;
				}
			} else {
				changeDirCooldown = 0;
			}
		}

		// Move Y separadamente
		if (dy != 0) {
			int novoY = y + dy;
			if (novoY >= 0 && novoY + altura <= mapaH) {
				if (!verificaColisao(x, novoY, mat)) {
					y = novoY;
				} else {
					changeDirCooldown = 0;
				}
			} else {
				changeDirCooldown = 0;
			}
		}
	}

	private boolean verificaColisao(int px, int py, int[][] mat) {
		int leftCol = px / Mapa.TAMANHO;
		int rightCol = (px + largura - 1) / Mapa.TAMANHO;
		int topRow = py / Mapa.TAMANHO;
		int bottomRow = (py + altura - 1) / Mapa.TAMANHO;

		leftCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, leftCol));
		rightCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, rightCol));
		topRow = Math.max(0, Math.min(Mapa.LINHAS - 1, topRow));
		bottomRow = Math.max(0, Math.min(Mapa.LINHAS - 1, bottomRow));

		for (int r = topRow; r <= bottomRow; r++) {
			for (int c = leftCol; c <= rightCol; c++) {
				if (mat[r][c] == 1) { // parede
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Tiro atirar() {
		int tiroSize = Math.max(4, largura / 4);
		int tx = x + largura / 2 - tiroSize / 2;
		int ty = y + altura / 2 - tiroSize / 2;

		int dx = 0, dy = 0;
		int speed = tipo == TipoInimigo.RAPIDO ? 5 : 4;

		switch (direcao) {
			case CIMA: dy = -speed; break;
			case BAIXO: dy = speed; break;
			case ESQUERDA: dx = -speed; break;
			case DIREITA: dx = speed; break;
		}

		resetShootCooldown();
		return new Tiro(tx, ty, tiroSize, tiroSize, dx, dy, Tiro.Shooter.INIMIGO);
	}

	@Override
	public boolean wantsToShoot() {
		return wantsToShoot;
	}

	@Override
	public void resetShootCooldown() {
		wantsToShoot = false;
		shootCooldown = rnd.nextInt(120) + 60;
	}

	public TipoInimigo getTipo() { return tipo; }
	public Direcao getDirecao() { return direcao; }
}
