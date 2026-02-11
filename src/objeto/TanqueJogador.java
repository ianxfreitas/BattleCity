package objeto;

import java.awt.*;
import mundo.Mapa;

public class TanqueJogador extends ObjetoJogo {

	public enum Direcao { CIMA, BAIXO, ESQUERDA, DIREITA }

	private Direcao direcao = Direcao.CIMA;
	private int velocidade = 2; // pixels por atualização

	// flags de movimento controladas pelo teclado
	private boolean movCima = false;
	private boolean movBaixo = false;
	private boolean movEsquerda = false;
	private boolean movDireita = false;

	public TanqueJogador(int x, int y, int tamanho) {
		super(x, y, tamanho, tamanho);
		this.vida = 3; // vida inicial
	}

	@Override
	public void atualizar() {
		// Antes: movimento era instantâneo nos eventos de tecla.
		// Agora, PainelJogo chamará moveIfPossible para aplicar as flags com checagens.
	}

	@Override
	public void desenhar(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(x, y, largura, altura);
	}

	// Setters para as flags de movimento
	public void setMovCima(boolean v) { movCima = v; if (v) direcao = Direcao.CIMA; }
	public void setMovBaixo(boolean v) { movBaixo = v; if (v) direcao = Direcao.BAIXO; }
	public void setMovEsquerda(boolean v) { movEsquerda = v; if (v) direcao = Direcao.ESQUERDA; }
	public void setMovDireita(boolean v) { movDireita = v; if (v) direcao = Direcao.DIREITA; }

	// Tenta mover o tanque de acordo com as flags, fazendo checagens de colisão
	// Agora move por eixo separadamente (X então Y) para permitir "deslizar" quando encostar em parede
	public void moveIfPossible(Mapa mapa) {
		int dx = 0, dy = 0;
		if (movCima) dy -= velocidade;
		if (movBaixo) dy += velocidade;
		if (movEsquerda) dx -= velocidade;
		if (movDireita) dx += velocidade;

		if (dx == 0 && dy == 0) return;

		int mapaW = Mapa.COLUNAS * Mapa.TAMANHO;
		int mapaH = Mapa.LINHAS * Mapa.TAMANHO;

		int[][] mat = mapa.getMatriz();

		// Move X separadamente
		if (dx != 0) {
			int novoX = x + dx;
			// verifica limites horizontais
			if (novoX >= 0 && novoX + largura <= mapaW) {
				int leftCol = novoX / Mapa.TAMANHO;
				int rightCol = (novoX + largura - 1) / Mapa.TAMANHO;
				int topRow = y / Mapa.TAMANHO;
				int bottomRow = (y + altura - 1) / Mapa.TAMANHO;

				leftCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, leftCol));
				rightCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, rightCol));
				topRow = Math.max(0, Math.min(Mapa.LINHAS - 1, topRow));
				bottomRow = Math.max(0, Math.min(Mapa.LINHAS - 1, bottomRow));

				boolean colisao = false;
				for (int r = topRow; r <= bottomRow && !colisao; r++) {
					for (int c = leftCol; c <= rightCol; c++) {
						if (mat[r][c] == 1) { colisao = true; break; }
					}
				}

				if (!colisao) {
					x = novoX;
				}
			}
		}

		// Move Y separadamente
		if (dy != 0) {
			int novoY = y + dy;
			// verifica limites verticais
			if (novoY >= 0 && novoY + altura <= mapaH) {
				int leftCol = x / Mapa.TAMANHO;
				int rightCol = (x + largura - 1) / Mapa.TAMANHO;
				int topRow = novoY / Mapa.TAMANHO;
				int bottomRow = (novoY + altura - 1) / Mapa.TAMANHO;

				leftCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, leftCol));
				rightCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, rightCol));
				topRow = Math.max(0, Math.min(Mapa.LINHAS - 1, topRow));
				bottomRow = Math.max(0, Math.min(Mapa.LINHAS - 1, bottomRow));

				boolean colisao = false;
				for (int r = topRow; r <= bottomRow && !colisao; r++) {
					for (int c = leftCol; c <= rightCol; c++) {
						if (mat[r][c] == 1) { colisao = true; break; }
					}
				}

				if (!colisao) {
					y = novoY;
				}
			}
		}
	}

	private int vida;

	public void tomarDano(int d) {
		vida -= d;
		if (vida < 0) vida = 0;
	}

	public boolean estaVivo() { return vida > 0; }

	public Tiro atirar() {
		int tiroSize = Math.max(4, largura/4);
		int dx = 0, dy = 0;
		int tx = x + largura/2 - tiroSize/2;
		int ty = y + altura/2 - tiroSize/2;
		int speed = 6;
		switch (direcao) {
			case CIMA: dy = -speed; break;
			case BAIXO: dy = speed; break;
			case ESQUERDA: dx = -speed; break;
			case DIREITA: dx = speed; break;
		}
		return new Tiro(tx, ty, tiroSize, tiroSize, dx, dy, Tiro.Shooter.JOGADOR);
	}
}
