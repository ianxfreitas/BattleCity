package objeto;

import java.awt.*;

/**
 * Representa um projétil/tiro no jogo.
 * Projéteis se movem em linha reta até colidir com algo.
 */
public class Tiro extends ObjetoJogo {

	public enum Shooter { JOGADOR, INIMIGO }

	private int dx;
	private int dy;
	private boolean ativo = true;
	private Shooter shooter;
	private int velocidade;

	public Tiro(int x, int y, int largura, int altura, int dx, int dy, Shooter shooter) {
		super(x, y, largura, altura);
		this.dx = dx;
		this.dy = dy;
		this.shooter = shooter;
		this.velocidade = 4; // pixels por frame
	}

	/**
	 * Construtor alternativo que permite especificar velocidade.
	 */
	public Tiro(int x, int y, int largura, int altura, int dx, int dy, Shooter shooter, int velocidade) {
		this(x, y, largura, altura, dx, dy, shooter);
		this.velocidade = velocidade;
	}

	@Override
	public void atualizar() {
		x += dx * velocidade;
		y += dy * velocidade;
		// se sair da tela, desativa (será removido pelo painel)
		if (x < -largura || y < -altura || x > 1000 || y > 1000) {
			ativo = false;
		}
	}

	@Override
	public void desenhar(Graphics g) {
		if (!ativo) return;
		g.setColor(shooter == Shooter.JOGADOR ? Color.YELLOW : Color.ORANGE);
		g.fillRect(x, y, largura, altura);
		
		// Pequeno destaque
		g.setColor(Color.WHITE);
		g.fillRect(x + 1, y + 1, Math.max(1, largura - 2), Math.max(1, altura - 2));
	}

	@Override
	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }

	public Shooter getShooter() { return shooter; }
	
	public int getDx() { return dx; }
	public int getDy() { return dy; }
	public int getVelocidade() { return velocidade; }
}
