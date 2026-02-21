package objeto;

import java.awt.*;
import mundo.Mapa;

/**
 * Representa o tanque controlável pelo jogador.
 * Implementa a interface Atirador para poder disparar tiros.
 */
public class TanqueJogador extends ObjetoJogo implements Atirador {

	public enum Direcao { CIMA, BAIXO, ESQUERDA, DIREITA }

	private Direcao direcao = Direcao.CIMA;
	private int velocidade = 2; // pixels por atualização

	// flags de movimento controladas pelo teclado
	private boolean movCima = false;
	private boolean movBaixo = false;
	private boolean movEsquerda = false;
	private boolean movDireita = false;

	// Sistema de tiro
	private int shootCooldown = 0;
	private int shootCooldownMax = 20; // ~330ms entre tiros
	private int nivelTiro = 1; // 1=normal, 2=rápido, 3=múltiplo
	private int municao = -1; // -1 = ilimitado

	// Power-ups ativos
	private boolean invulneravel = false;
	private int tempoInvulnerabilidade = 0;

	public TanqueJogador(int x, int y, int tamanho) {
		super(x, y, tamanho, tamanho);
		this.vida = 3; // vida inicial
	}

	@Override
	public void atualizar() {
		// Atualizar cooldown de tiro
		if (shootCooldown > 0) shootCooldown--;

		// Atualizar invulnerabilidade
		if (invulneravel) {
			tempoInvulnerabilidade--;
			if (tempoInvulnerabilidade <= 0) {
				invulneravel = false;
			}
		}
	}

	@Override
	public void desenhar(Graphics g) {
		// Piscar se invulnerável
		if (invulneravel && tempoInvulnerabilidade % 10 < 5) {
			return; // não desenhar a cada 5 ticks
		}

		g.setColor(Color.GREEN);
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
	}

	// Setters para as flags de movimento
	public void setMovCima(boolean v) { movCima = v; if (v) direcao = Direcao.CIMA; }
	public void setMovBaixo(boolean v) { movBaixo = v; if (v) direcao = Direcao.BAIXO; }
	public void setMovEsquerda(boolean v) { movEsquerda = v; if (v) direcao = Direcao.ESQUERDA; }
	public void setMovDireita(boolean v) { movDireita = v; if (v) direcao = Direcao.DIREITA; }

	// Tenta mover o tanque de acordo com as flags, fazendo checagens de colisão
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
			if (novoX >= 0 && novoX + largura <= mapaW) {
				if (!verificaColisao(novoX, y, mat)) {
					x = novoX;
				}
			}
		}

		// Move Y separadamente
		if (dy != 0) {
			int novoY = y + dy;
			if (novoY >= 0 && novoY + altura <= mapaH) {
				if (!verificaColisao(x, novoY, mat)) {
					y = novoY;
				}
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
				if (mat[r][c] == 1 || mat[r][c] == 2) { // parede ou base
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Tiro atirar() {
		if (shootCooldown > 0) return null;
		if (municao == 0) return null;

		int tiroSize = Math.max(4, largura / 4);
		int tx = x + largura / 2 - tiroSize / 2;
		int ty = y + altura / 2 - tiroSize / 2;

		int dx = 0, dy = 0;
		int speed = 4 + nivelTiro; // velocidade aumenta com nível

		switch (direcao) {
			case CIMA: dy = -speed; break;
			case BAIXO: dy = speed; break;
			case ESQUERDA: dx = -speed; break;
			case DIREITA: dx = speed; break;
		}

		shootCooldown = shootCooldownMax;
		if (municao > 0) municao--;

		return new Tiro(tx, ty, tiroSize, tiroSize, dx, dy, Tiro.Shooter.JOGADOR);
	}

	@Override
	public boolean wantsToShoot() {
		return false; // controlado pelo ControleTeclado
	}

	@Override
	public void resetShootCooldown() {
		shootCooldown = 0;
	}

	@Override
	public void tomarDano(int dano) {
		if (!invulneravel) {
			super.tomarDano(dano);
		}
	}

	// Métodos para power-ups
	public void aplicarPowerUp(PowerUp.TipoPowerUp tipo) {
		switch (tipo) {
			case ESTRELA:
				nivelTiro = Math.min(3, nivelTiro + 1);
				break;
			case CAPACETE:
				invulneravel = true;
				tempoInvulnerabilidade = 300; // ~5 segundos
				break;
			case VIDA:
				vida++;
				break;
			case RELOGIO:
				// será implementado no sistema de jogo
				break;
			case PA:
				// será implementado para proteger a base
				break;
			case BOMBA:
				// será implementado para explodir todos os inimigos
				break;
		}
	}

	// Getters
	public Direcao getDirecao() { return direcao; }
	public boolean isInvulneravel() { return invulneravel; }
	public int getNivelTiro() { return nivelTiro; }
	public int getMunicao() { return municao; }
}
