package objeto;

import java.awt.*;

/**
 * Representa um projétil/tiro no jogo.
 * Agora implementa Movimentavel e Runnable, rodando em sua própria Thread!
 */
public class Tiro extends ObjetoJogo implements Movimentavel, Runnable {

	public enum Shooter { JOGADOR, INIMIGO }

	private int dx;
	private int dy;

	private volatile boolean ativo = true;
	private volatile boolean pausado = false;

	private Shooter shooter;
	private int velocidade;

	public Tiro(int x, int y, int largura, int altura, int dx, int dy, Shooter shooter) {
		super(x, y, largura, altura);
		this.dx = dx;
		this.dy = dy;
		this.shooter = shooter;
		this.velocidade = 1;
	}

	public Tiro(int x, int y, int largura, int altura, int dx, int dy, Shooter shooter, int velocidade) {
		this(x, y, largura, altura, dx, dy, shooter);
		this.velocidade = velocidade;
	}

	// MÉTODOS DA THREAD (RUNNABLE)
	@Override
	public void run() {
		while (ativo) {
			if (!pausado) {
				// A Thread FAZ O TIRO ANDAR
				atualizar();
			}

			// O SEGREDO DA FÍSICA: O tiro precisa "dormir" 16 milissegundos (~60 FPS)
			// Se você não colocar isso, ele teleporta pra fora da tela e ignora colisões!
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mover() {
		x += dx * velocidade;
		y += dy * velocidade;

		// Se sair da tela, desativa (a Thread vai sair do while e encerrar)
		// Coloquei 2000 no limite para garantir que ele passe por trás do HUD novo sem sumir antes da hora!
		if (x < -largura || y < -altura || x > 2000 || y > 2000) {
			parar();
		}
	}

	@Override
	public void parar() {
		this.ativo = false;
	}

	@Override
	public boolean isEmMovimento() {
		return this.ativo;
	}

	@Override
	public void atualizar() {
		// ✅ IMPORTANTE: Chamar mover() para atualizar a posição do tiro
		mover();
	}

	// DESENHO E GETTERS/SETTERS
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

	public void setPausado(boolean pausado) { this.pausado = pausado; }

	public Shooter getShooter() { return shooter; }

	public int getDx() { return dx; }
	public int getDy() { return dy; }
	public int getVelocidade() { return velocidade; }
}