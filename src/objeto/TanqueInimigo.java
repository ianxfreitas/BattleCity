package objeto;

import java.util.Random;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.InputStream;
import mundo.Mapa;

/**
 * Representa um tanque inimigo com IA.
 * Diferentes tipos de inimigos com comportamentos distintos.
 */
public class TanqueInimigo extends ObjetoJogo implements Atirador, Runnable {

	public enum Direcao { CIMA, BAIXO, ESQUERDA, DIREITA }

	public enum TipoInimigo {
		NORMAL(2, 2, "Normal"),
		RAPIDO(2, 4, "Rápido"),
		ARMADO(3, 1, "Armado"),
		BLINDADO(4, 1, "Blindado");

		public final int vida;
		public final int velocidade;
		public final String nome;

		TipoInimigo(int vida, int velocidade, String nome) {
			this.vida = vida;
			this.velocidade = velocidade;
			this.nome = nome;
		}
	}

	private volatile boolean ativo = true;
	private volatile boolean pausado = false;
	private Mapa mapaReferencia;

	// Campo para a imagem do tanque inimigo
	private BufferedImage imagem;
	private TipoInimigo tipo;
	private Direcao direcao = Direcao.BAIXO;
	private int velocidade; // pixels por atualização
	private Random rand = new Random();

	// timers em ticks (cada tick é ~16ms)
	private int changeDirCooldown = rand.nextInt(60) + 30;
	private int shootCooldown = rand.nextInt(120) + 60;
	private boolean wantsToShoot = false;
	private boolean congelado = false;
	private int tempoCongelamento = 0;
	private int tickDano = 0; // Usado para o efeito de piscar quando machucado

	public TanqueInimigo(int x, int y, int tamanho) {
		this(x, y, tamanho, TipoInimigo.NORMAL);
	}

	public TanqueInimigo(int x, int y, int tamanho, TipoInimigo tipo) {
		super(x, y, tamanho, tamanho);
		this.tipo = tipo;
		this.velocidade = tipo.velocidade;
		this.vida = tipo.vida; // Agora ele puxa o "2" direto do enum!
		this.direcao = Direcao.BAIXO;
		this.changeDirCooldown = rand.nextInt(60) + 30;
		this.shootCooldown = rand.nextInt(120) + 60;
		carregarImagem();
	}

	@Override
	public void atualizar() {
	}

	@Override
	public void desenhar(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// Se temos imagem, usar drawImage; caso contrário, usar fallback
		if (imagem != null) {
			// Aplicar rotação conforme a direção
			double rotacao = 0;
			switch (direcao) {
				case CIMA:    rotacao = 0; break;
				case BAIXO:   rotacao = Math.PI; break; // 180 graus
				case ESQUERDA: rotacao = -Math.PI / 2; break; // -90 graus
				case DIREITA:  rotacao = Math.PI / 2; break; // 90 graus
			}

			// Salvar estado original do Graphics
			AffineTransform transformAntigo = g2d.getTransform();

			// Aplicar rotação no centro da imagem
			int cx = x + largura / 2;
			int cy = y + altura / 2;
			g2d.rotate(rotacao, cx, cy);

			// Desenhar a imagem principal
			g2d.drawImage(imagem, x, y, largura, altura, null);

			// Restaurar estado original para não girar o resto do mapa!
			g2d.setTransform(transformAntigo);

		} else {
			// FALLBACK: desenhar retângulo vermelho se a imagem falhar
			g.setColor(Color.RED);
			g.fillRect(x, y, largura, altura);

			// Desenhar canhão amarelo na direção atual
			g.setColor(Color.YELLOW);
			int cx = x + largura / 2;
			int cy = y + altura / 2;
			int canoSize = largura / 3;

			switch (direcao) {
				case CIMA:    g.fillRect(cx - 1, y - canoSize, 2, canoSize); break;
				case BAIXO:   g.fillRect(cx - 1, y + altura, 2, canoSize); break;
				case ESQUERDA: g.fillRect(x - canoSize, cy - 1, canoSize, 2); break;
				case DIREITA:  g.fillRect(x + largura, cy - 1, canoSize, 2); break;
			}
		}

		// --- EFEITO VISUAL DE DANO ---
		// Se a vida atual for menor que a vida máxima (tipo.vida) e não estiver morto
		if (this.vida < tipo.vida && this.vida > 0) {
			// Pisca a cada 10 ticks (faz aparecer e sumir a cor de dano)
			if (tickDano % 20 < 10) {
				// Uma cor avermelhada transparente (efeito de machucado/fogo)
				g2d.setColor(new Color(255, 50, 0, 120));
				g2d.fillRect(x, y, largura, altura);
			}
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

	private void carregarImagem() {
		try {
			// Nome do arquivo PNG do seu inimigo
			var inputStream = getClass().getResourceAsStream("/res/inimigo.png");
			if (inputStream == null) {
				imagem = null; // null = usar fallback
				return;
			}
			imagem = ImageIO.read(inputStream);
		} catch (IOException | IllegalArgumentException e) {
			imagem = null; // null = usar fallback
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
				// Blocos que IMPEDEM movimento: PAREDE, AGUA, TIJOLO, ACO, BASE
				// Tanques podem passar por: VAZIO, ARVORE
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
		shootCooldown = rand.nextInt(120) + 60;
	}

	public TipoInimigo getTipo() { return tipo; }
	public Direcao getDirecao() { return direcao; }

	public void congelar(int duracao) {
		this.congelado = true;
		this.tempoCongelamento = duracao;
	}

	@Override
	public void run() {
		while (ativo && estaVivo()) {
			if (!pausado && mapaReferencia != null) {
				changeDirCooldown--;
				shootCooldown--;

				if (congelado) {
					tempoCongelamento--;
					if (tempoCongelamento <= 0) {
						congelado = false;
					}
				} else {
					moveIfPossible(mapaReferencia);

					if (shootCooldown <= 0) {
						wantsToShoot = true;
						shootCooldown = rand.nextInt(120) + 60; // Reseta o cronômetro para ele não atirar igual metralhadora
					}
					if (changeDirCooldown <= 0) {
						mudarDirecao();
						changeDirCooldown = rand.nextInt(100) + 50;
					}
				}
			}

			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				parar();
				Thread.currentThread().interrupt();
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

	// Método que sorteia uma nova direção aleatória para o tanque
	private void mudarDirecao() {
		Direcao[] direcoes = Direcao.values(); // Pega as 4 direções possíveis
		this.direcao = direcoes[rand.nextInt(direcoes.length)]; // Sorteia uma
	}

	public boolean isWantsToShoot() {
		return wantsToShoot;
	}

	public void setWantsToShoot(boolean wantsToShoot) {
		this.wantsToShoot = wantsToShoot;
	}

	public boolean isCongelado() { return congelado; }
}