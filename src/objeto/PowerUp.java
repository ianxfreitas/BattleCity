package objeto;

import java.awt.*;

/**
 * Representa um power-up que o jogador pode coletar.
 * Power-ups concedem benefícios temporários ou permanentes.
 */
public class PowerUp extends ObjetoJogo {

	public enum TipoPowerUp {
		ESTRELA("Estrela", "Aumenta poder de tiro"),
		CAPACETE("Capacete", "Invulnerabilidade temporária"),
		PA("Pá", "Protege a base"),
		RELOGIO("Relógio", "Congela inimigos"),
		BOMBA("Bomba", "Explode tudo na tela"),
		VIDA("Vida Extra", "Ganha uma vida");

		public final String nome;
		public final String descricao;

		TipoPowerUp(String nome, String descricao) {
			this.nome = nome;
			this.descricao = descricao;
		}
	}

	private TipoPowerUp tipo;
	private int duracao; // em ticks (16ms cada), -1 para permanente
	private boolean ativo;

	public PowerUp(int x, int y, int tamanho, TipoPowerUp tipo) {
		super(x, y, tamanho, tamanho);
		this.tipo = tipo;
		this.ativo = true;

		// Definir duração baseado no tipo
		switch (tipo) {
			case CAPACETE, RELOGIO -> this.duracao = 300; // ~5 segundos
			default -> this.duracao = -1; // permanente
		}
	}

	@Override
	public void atualizar() {
		if (duracao > 0) {
			duracao--;
			if (duracao <= 0) {
				ativo = false;
			}
		}
	}

	@Override
	public void desenhar(Graphics g) {
		if (!ativo) return;

		Color cor = switch (tipo) {
			case ESTRELA -> Color.YELLOW;
			case CAPACETE -> Color.CYAN;
			case PA -> new Color(210, 105, 30); // chocolate
			case RELOGIO -> new Color(255, 192, 203); // rosa
			case BOMBA -> Color.RED;
			case VIDA -> Color.GREEN;
		};

		g.setColor(cor);
		g.fillRect(x, y, largura, altura);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, largura, altura);

		// Desenhar símbolo
		switch (tipo) {
			case ESTRELA:
				g.setColor(Color.BLACK);
				g.drawString("★", x + 2, y + altura - 2);
				break;
			case CAPACETE:
				g.setColor(Color.DARK_GRAY);
				g.fillArc(x + 2, y + 2, altura - 4, altura - 4, 0, 180);
				break;
			case PA:
				g.setColor(Color.BLACK);
				g.fillRect(x + 4, y + 2, 4, 8);
				g.fillRect(x + 2, y + 10, 8, 2);
				break;
			case RELOGIO:
				g.setColor(Color.BLACK);
				g.drawOval(x + 2, y + 2, altura - 4, altura - 4);
				g.drawLine(x + altura / 2, y + altura / 2, x + altura / 2, y + 4);
				g.drawLine(x + altura / 2, y + altura / 2, x + altura - 4, y + altura / 2);
				break;
			case BOMBA:
				g.setColor(Color.BLACK);
				g.fillOval(x + 3, y + 3, altura - 6, altura - 6);
				g.drawLine(x + altura / 2, y + 1, x + altura / 2, y + 3);
				break;
			case VIDA:
				g.setColor(Color.RED);
				g.drawString("❤", x, y + altura - 2);
				break;
		}
	}

	public TipoPowerUp getTipo() { return tipo; }
	public boolean isAtivo() { return ativo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }
	public int getDuracao() { return duracao; }
	public void setDuracao(int duracao) { this.duracao = duracao; }
}
