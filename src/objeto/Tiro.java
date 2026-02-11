package objeto;

import java.awt.*;

public class Tiro extends ObjetoJogo {

	public enum Shooter { JOGADOR, INIMIGO }

	private int dx;
	private int dy;
	private boolean ativo = true;
	private Shooter shooter;

	public Tiro(int x, int y, int largura, int altura, int dx, int dy, Shooter shooter) {
		super(x, y, largura, altura);
		this.dx = dx;
		this.dy = dy;
		this.shooter = shooter;
	}

	@Override
	public void atualizar() {
		x += dx;
		y += dy;
		// se sair da tela, desativa (será removido pelo painel)
		if (x < -largura || y < -altura) ativo = false;
	}

	@Override
	public void desenhar(Graphics g) {
		if (!ativo) return;
		g.setColor(Color.YELLOW);
		g.fillRect(x, y, largura, altura);
	}

	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }

	public Shooter getShooter() { return shooter; }
}
