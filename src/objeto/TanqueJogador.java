package objeto;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import mundo.Mapa;

/**
 * Representa o tanque controlável pelo jogador.
 * Implementa a interface Atirador para poder disparar tiros.
 * Agora suporta IMAGENS DIFERENTES para cada tipo de tanque!
 */
public class TanqueJogador extends ObjetoJogo implements Atirador, Runnable {

	public enum Direcao { CIMA, BAIXO, ESQUERDA, DIREITA }

	public enum TipoTanque {
		AGIL("Ágil", 3, 25, 2),             // Mais rápido, mas tem 2 vidas
		BALANCEADO("Balanceado", 2, 40, 3), // O padrão de sempre (3 vidas)
		BLINDADO("Blindado", 1, 60, 5);     // Lento, mas é um tanque de guerra real (5 vidas)

		public final String nome;
		public final int velocidade;
		public final int shootCooldownMax;
		public final int vidaInicial;

		TipoTanque(String nome, int velocidade, int shootCooldownMax, int vidaInicial) {
			this.nome = nome;
			this.velocidade = velocidade;
			this.shootCooldownMax = shootCooldownMax;
			this.vidaInicial = vidaInicial;
		}
	}

	private Direcao direcao = Direcao.CIMA;

	// Armazena o tipo atual para sabermos qual imagem desenhar
	private TipoTanque tipoAtual = TipoTanque.BALANCEADO;
	private volatile boolean ativo = true;
	private volatile boolean pausado = false;
	private Mapa mapaReferencia;
	private int velocidade = 2;

	// flags de movimento
	private boolean movCima = false;
	private boolean movBaixo = false;
	private boolean movEsquerda = false;
	private boolean movDireita = false;

	// Sistema de tiro
	private int shootCooldown = 0;
	private int shootCooldownMax = 20;
	private int nivelTiro = 1;
	private int municao = -1;

	// Power-ups ativos
	private boolean invulneravel = false;
	private int tempoInvulnerabilidade = 0;
	private boolean baseProtegida = false;
	private int tempoBaseProtegida = 0;

	// NOVAS VÁRIAVEIS PARA AS IMAGENS DISTINTAS
	private BufferedImage imgAgil;
	private BufferedImage imgBalanceado;
	private BufferedImage imgBlindado;

	public TanqueJogador(int x, int y, int tamanho) {
		super(x, y, tamanho, tamanho);
		this.vida = 3;

		// Carrega as três imagens assim que o tanque é criado
		carregarTodasImagens();
	}

	/**
	 * Aplica as características e define o TIPO ATUAL do tanque.
	 */
	public void aplicarTipoTanque(TipoTanque tipo) {
		if (tipo != null) {
			this.tipoAtual = tipo;
			this.velocidade = tipo.velocidade;
			this.shootCooldownMax = tipo.shootCooldownMax;

			this.vida = tipo.vidaInicial;
		}
	}

	/**
	 * Tenta carregar as três imagens dos recursos.
	 * Usa um método auxiliar para evitar repetição de código try-catch.
	 */
	private void carregarTodasImagens() {
		imgAgil = carregarUmaImagem("/res/tanque_agil.png");
		imgBalanceado = carregarUmaImagem("/res/tanque_balanceado.png");
		imgBlindado = carregarUmaImagem("/res/tanque_blindado.png");

		// Logs de aviso se alguma falhar
		if(imgAgil == null) System.out.println("Aviso: tanque_agil.png não carregou.");
		if(imgBalanceado == null) System.out.println("Aviso: tanque_balanceado.png não carregou.");
		if(imgBlindado == null) System.out.println("Aviso: tanque_blindado.png não carregou.");
	}

	private BufferedImage carregarUmaImagem(String caminho) {
		try {
			InputStream is = getClass().getResourceAsStream(caminho);
			if (is != null) {
				return ImageIO.read(is);
			}
		} catch (IOException e) {

		}
		return null;
	}


	@Override
	public void atualizar() {
	}

	@Override
	public void desenhar(Graphics g) {
		// Pisca se invulnerável e estiver acabando o tempo
		if (invulneravel && tempoInvulnerabilidade < 50 && tempoInvulnerabilidade % 10 < 5) {
			return;
		}

		Graphics2D g2d = (Graphics2D) g;

		BufferedImage imagemParaDesenhar = null;
		switch (this.tipoAtual) {
			case AGIL -> imagemParaDesenhar = imgAgil;
			case BLINDADO -> imagemParaDesenhar = imgBlindado;
			default -> imagemParaDesenhar = imgBalanceado; // Balanceado é o padrão
		}

		// Se a imagem escolhida existe, desenha com rotação
		if (imagemParaDesenhar != null) {
			double rotacao = 0;
			switch (direcao) {
				case CIMA: rotacao = 0; break;
				case BAIXO: rotacao = Math.PI; break;
				case ESQUERDA: rotacao = -Math.PI / 2; break;
				case DIREITA: rotacao = Math.PI / 2; break;
			}

			AffineTransform transformAntigo = g2d.getTransform();
			int cx = x + largura / 2;
			int cy = y + altura / 2;
			g2d.rotate(rotacao, cx, cy);

			// Usa a imagem selecionada aqui
			g2d.drawImage(imagemParaDesenhar, x, y, largura, altura, null);

			g2d.setTransform(transformAntigo);
		} else {
			// FALLBACK: se a imagem do tipo atual não carregou, desenha o quadrado verde
			g.setColor(Color.GREEN);
			// Muda ligeiramente a cor do fallback baseado no tipo, só para diferenciar
			if(tipoAtual == TipoTanque.AGIL) g.setColor(new Color(100, 255, 100));
			if(tipoAtual == TipoTanque.BLINDADO) g.setColor(new Color(0, 150, 0));

			g.fillRect(x, y, largura, altura);
			g.setColor(Color.YELLOW);
			int cx = x + largura / 2;
			int cy = y + altura / 2;
			int canoSize = largura / 3;
			switch (direcao) {
				case CIMA: g.fillRect(cx - 1, y - canoSize, 2, canoSize); break;
				case BAIXO: g.fillRect(cx - 1, y + altura, 2, canoSize); break;
				case ESQUERDA: g.fillRect(x - canoSize, cy - 1, canoSize, 2); break;
				case DIREITA: g.fillRect(x + largura, cy - 1, canoSize, 2); break;
			}
		}

		if (invulneravel) {
			g2d.setColor(Color.CYAN);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawOval(x - 4, y - 4, largura + 8, altura + 8);
			g2d.setColor(new Color(0, 255, 255, 60));
			g2d.fillOval(x - 4, y - 4, largura + 8, altura + 8);
		}
	}

	// Setters para as flags de movimento
	public void setMovCima(boolean v) { movCima = v; if (v) direcao = Direcao.CIMA; }
	public void setMovBaixo(boolean v) { movBaixo = v; if (v) direcao = Direcao.BAIXO; }
	public void setMovEsquerda(boolean v) { movEsquerda = v; if (v) direcao = Direcao.ESQUERDA; }
	public void setMovDireita(boolean v) { movDireita = v; if (v) direcao = Direcao.DIREITA; }

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

		if (dx != 0) {
			int novoX = x + dx;
			if (novoX >= 0 && novoX + largura <= mapaW) {
				if (!verificaColisao(novoX, y, mat)) {
					x = novoX;
				}
			}
		}
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
				int tipoBloco = mat[r][c];
				if (tipoBloco == Mapa.PAREDE || tipoBloco == Mapa.AGUA ||
						tipoBloco == Mapa.TIJOLO || tipoBloco == Mapa.ACO ||
						tipoBloco == Mapa.BASE) {
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
		int speed = 4 + nivelTiro;

		switch (direcao) {
			case CIMA: dy = -speed; break;
			case BAIXO: dy = speed; break;
			case ESQUERDA: dx = -speed; break;
			case DIREITA: dx = speed; break;
		}

		shootCooldown = shootCooldownMax;
		if (municao > 0) municao--;

		controles.GerenciadorSom.tocarSom("/res/tiro.wav");

		return new Tiro(tx, ty, tiroSize, tiroSize, dx, dy, Tiro.Shooter.JOGADOR);
	}

	@Override
	public boolean wantsToShoot() { return false; }

	@Override
	public void resetShootCooldown() { shootCooldown = 0; }

	@Override
	public void tomarDano(int dano) {
		if (!invulneravel) {
			super.tomarDano(dano);
		}
	}

	public void aplicarPowerUp(PowerUp.TipoPowerUp tipo) {
		switch (tipo) {
			case ESTRELA:
				nivelTiro = Math.min(3, nivelTiro + 1);
				break;
			case CAPACETE:
				invulneravel = true;
				tempoInvulnerabilidade = 300;
				break;
			case VIDA:
				vida++;
				break;
			case RELOGIO: break;
			case PA:
				baseProtegida = true;
				tempoBaseProtegida = 600;
				break;
			case BOMBA: break;
		}
	}

	// Getters
	public Direcao getDirecao() { return direcao; }
	public boolean isInvulneravel() { return invulneravel; }
	public boolean isBaseProtegida() { return baseProtegida; }
	public int getNivelTiro() { return nivelTiro; }
	public int getMunicao() { return municao; }
	public TipoTanque getTipoAtual() { return tipoAtual; }

	/**
	 * Reseta a posição do tanque para o local de respawn da fase
	 * e garante que ele comece virado para cima.
	 */
	public void resetarPosicao(int novoX, int novoY) {
		this.x = novoX;
		this.y = novoY;
		this.direcao = Direcao.CIMA;

		// Se quiser que ele pare de se mover automaticamente ao mudar de fase:
		this.movCima = false;
		this.movBaixo = false;
		this.movEsquerda = false;
		this.movDireita = false;
	}

	public void removerPowerUps() {
	}

	@Override
	public void run() {
		while (ativo && estaVivo()) {
			if (!pausado && mapaReferencia != null) {
				// ✅ Atualizar cooldowns
				if (shootCooldown > 0) shootCooldown--;
				
				if (invulneravel) {
					tempoInvulnerabilidade--;
					if (tempoInvulnerabilidade <= 0) {
						invulneravel = false;
					}
				}
				moveIfPossible(mapaReferencia);
			}

			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setMapaReferencia(Mapa mapa) {
		this.mapaReferencia = mapa;
	}

	public void parar() {
		this.ativo = false;
	}

	public void setPausado(boolean pausado) {
		this.pausado = pausado;
	}
}
