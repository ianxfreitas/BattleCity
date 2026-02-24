package objeto;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

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
	private int duracao;
	private boolean ativo;

	// Variáveis para TODAS as imagens
	private BufferedImage imgEstrela;
	private BufferedImage imgCapacete;
	private BufferedImage imgPa;
	private BufferedImage imgRelogio;
	private BufferedImage imgBomba;
	private BufferedImage imgVida;

	public PowerUp(int x, int y, int tamanho, TipoPowerUp tipo) {
		super(x, y, tamanho, tamanho);
		this.tipo = tipo;
		this.ativo = true;

		// Definir duração baseado no tipo
		switch (tipo) {
			case CAPACETE, RELOGIO -> this.duracao = 300;
			default -> this.duracao = -1;
		}

		carregarImagens();
	}

	// CARREGAMENTO DE IMAGENS
	private void carregarImagens() {
		imgEstrela = carregarUmaImagem("/res/estrela.png");
		imgCapacete = carregarUmaImagem("/res/capacete.png");
		imgPa = carregarUmaImagem("/res/pa.png");
		imgRelogio = carregarUmaImagem("/res/relogio.png");
		imgBomba = carregarUmaImagem("/res/bomba.png");
		imgVida = carregarUmaImagem("/res/vida.png");
	}

	// Método ajudante para não precisar repetir try-catch várias vezes
	private BufferedImage carregarUmaImagem(String caminho) {
		try {
			InputStream is = getClass().getResourceAsStream(caminho);
			if (is != null) {
				return ImageIO.read(is);
			}
		} catch (Exception e) {
			// Se der erro, simplesmente retorna null e o jogo usa o fallback
		}
		return null;
	}

	// LÓGICA DO JOGO E DESENHO
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

		// CHECAGEM DE IMAGENS: Se a imagem existir, desenha e encerra o método (return)
		if (tipo == TipoPowerUp.ESTRELA && imgEstrela != null) { g.drawImage(imgEstrela, x, y, largura, altura, null); return; }
		if (tipo == TipoPowerUp.CAPACETE && imgCapacete != null) { g.drawImage(imgCapacete, x, y, largura, altura, null); return; }
		if (tipo == TipoPowerUp.PA && imgPa != null) { g.drawImage(imgPa, x, y, largura, altura, null); return; }
		if (tipo == TipoPowerUp.RELOGIO && imgRelogio != null) { g.drawImage(imgRelogio, x, y, largura, altura, null); return; }
		if (tipo == TipoPowerUp.BOMBA && imgBomba != null) { g.drawImage(imgBomba, x, y, largura, altura, null); return; }
		if (tipo == TipoPowerUp.VIDA && imgVida != null) { g.drawImage(imgVida, x, y, largura, altura, null); return; }

		// FALLBACK ORIGINAL: Usado caso as imagens não carreguem
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

		// Desenhar símbolo original
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