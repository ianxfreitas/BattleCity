package telas;

import jogo.*;
import mundo.*;
import objeto.*;
import controles.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Painel principal do jogo Battle City.
 * Responsável por renderizar e atualizar toda a lógica de jogo.
 */
public class PainelJogoV2 extends JPanel implements ActionListener {

	private Mapa mapa;
	private TanqueJogador jogador;
	private List<Tiro> tiros = new ArrayList<>();
	private List<TanqueInimigo> inimigos = new ArrayList<>();
	private List<PowerUp> powerUps = new ArrayList<>();
	private ControleTeclado controle;
	private Timer timer;

	// Configurações do jogo
	private Configuracoes config;
	private Random rand = new Random();

	// Estados do jogo
	private boolean pausado = false;
	private boolean gameOver = false;
	private boolean faseCompleta = false;
	private int inimigosRestantes;
	private long inicioFase;
	private int totalInimigosInicial;

	// Painel HUD (será adicionado ao lado)
	private int largoMapa;
	private int altoMapa;

	// Callback para game over
	public interface GameOverCallback {
		void onGameOver();
	}

	private GameOverCallback gameOverCallback;

	public PainelJogoV2(Configuracoes config) {
		this.config = config != null ? config : new Configuracoes();
		this.gameOverCallback = null;
		inicializarJogo();
	}

	public PainelJogoV2(Configuracoes config, GameOverCallback callback) {
		this.config = config != null ? config : new Configuracoes();
		this.gameOverCallback = callback;
		inicializarJogo();
	}

	private void inicializarJogo() {
		setBackground(Color.BLACK);

		// Selecionar mapa baseado em configuração
		this.mapa = selecionarMapa();

		largoMapa = Mapa.COLUNAS * Mapa.TAMANHO;
		altoMapa = Mapa.LINHAS * Mapa.TAMANHO;

		setPreferredSize(new Dimension(largoMapa, altoMapa));
		setMinimumSize(new Dimension(largoMapa, altoMapa));
		setMaximumSize(new Dimension(largoMapa, altoMapa));
		setSize(new Dimension(largoMapa, altoMapa));

		// Criar tanque do jogador
		int tamanhoTanque = Mapa.TAMANHO * 3 / 4;
		int startX = (Mapa.COLUNAS / 2) * Mapa.TAMANHO + (Mapa.TAMANHO - tamanhoTanque) / 2;
		int startY = (Mapa.LINHAS - 2) * Mapa.TAMANHO + (Mapa.TAMANHO - tamanhoTanque) / 2;
		jogador = new TanqueJogador(startX, startY, tamanhoTanque);
		jogador.setVida(config.getVidas());

		// Spawnar inimigos baseado em dificuldade
		spawnInimigos();

		// Controle de teclado
		controle = new ControleTeclado(jogador);
		controle.setOnAtiraListener(tiro -> {
			if (!pausado && !gameOver && tiro != null) {
				tiros.add(tiro);
			}
		});
		addKeyListener(controle);

		setFocusable(true);
		requestFocusInWindow();

		// Timer ~60 FPS
		timer = new Timer(16, this);
		timer.start();

		inicioFase = System.currentTimeMillis();
	}

	private Mapa selecionarMapa() {
		String mapaSelecionado = config.getMapaSelecionado();
		return switch (mapaSelecionado) {
			case "Mapa1" -> new Mapa1();
			case "Mapa2" -> new Mapa2();
			case "Mapa3" -> new Mapa3();
			default -> {
				int mapa = rand.nextInt(3);
				yield switch (mapa) {
					case 1 -> new Mapa2();
					case 2 -> new Mapa3();
					default -> new Mapa1();
				};
			}
		};
	}

	private void spawnInimigos() {
		inimigos.clear();
		int tamanhoTanque = Mapa.TAMANHO * 3 / 4;

		// Número de inimigos baseado em dificuldade e fase
		int numInimigos = 3 + (config.getFase() - 1);
		if ("Fácil".equals(config.getDificuldade())) {
			numInimigos = Math.max(1, numInimigos - 1);
		} else if ("Difícil".equals(config.getDificuldade())) {
			numInimigos = numInimigos + 2;
		}

		totalInimigosInicial = numInimigos;
		inimigosRestantes = numInimigos;

		// Posições de spawn nos cantos
		int[][] spawnPoints = {
			{Mapa.TAMANHO, Mapa.TAMANHO},
			{(Mapa.COLUNAS - 2) * Mapa.TAMANHO, Mapa.TAMANHO},
			{Mapa.TAMANHO, (Mapa.LINHAS - 2) * Mapa.TAMANHO},
			{(Mapa.COLUNAS - 2) * Mapa.TAMANHO, (Mapa.LINHAS - 2) * Mapa.TAMANHO}
		};

		for (int i = 0; i < numInimigos; i++) {
			int[] spawn = spawnPoints[i % spawnPoints.length];
			int centerOffset = (Mapa.TAMANHO - tamanhoTanque) / 2;
			int x = spawn[0] + centerOffset;
			int y = spawn[1] + centerOffset;

			// Variar tipo de inimigo
			TanqueInimigo.TipoInimigo tipo = switch (config.getDificuldade()) {
				case "Difícil" -> rand.nextBoolean() ? TanqueInimigo.TipoInimigo.RAPIDO : TanqueInimigo.TipoInimigo.ARMADO;
				case "Fácil" -> TanqueInimigo.TipoInimigo.NORMAL;
				default -> i % 2 == 0 ? TanqueInimigo.TipoInimigo.NORMAL : TanqueInimigo.TipoInimigo.RAPIDO;
			};

			inimigos.add(new TanqueInimigo(x, y, tamanhoTanque, tipo));
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Desenhar mapa
		if (mapa != null) {
			mapa.desenhar(g);
		}

		// Desenhar tanque do jogador
		if (jogador != null) {
			jogador.desenhar(g);
		}

		// Desenhar inimigos
		for (TanqueInimigo enemy : inimigos) {
			enemy.desenhar(g);
		}

		// Desenhar tiros
		for (Tiro t : tiros) {
			t.desenhar(g);
		}

		// Desenhar power-ups
		for (PowerUp pu : powerUps) {
			pu.desenhar(g);
		}

		// Desenhar HUD
		desenharHUD(g);
	}

	private void desenharHUD(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 14));

		int margemY = 10;
		int margemX = 10;

		// Vidas
		g.drawString("Vidas: " + jogador.getVida(), margemX, margemY + 20);

		// Pontuação
		g.drawString("Pontuação: " + config.getPontuacao(), margemX, margemY + 40);

		// Fase
		g.drawString("Fase: " + config.getFase(), margemX, margemY + 60);

		// Inimigos restantes
		g.drawString("Inimigos: " + inimigosRestantes, margemX, margemY + 80);

		// Status
		if (pausado) {
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Arial", Font.BOLD, 24));
			g.drawString("PAUSADO", largoMapa / 2 - 60, altoMapa / 2);
		}

		if (gameOver) {
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.BOLD, 28));
			g.drawString("GAME OVER", largoMapa / 2 - 80, altoMapa / 2);
		}

		if (faseCompleta) {
			g.setColor(Color.GREEN);
			g.setFont(new Font("Arial", Font.BOLD, 24));
			g.drawString("FASE COMPLETA!", largoMapa / 2 - 90, altoMapa / 2);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (gameOver) {
			finalizarJogo();
			return;
		}

		if (pausado) {
			repaint();
			return;
		}

		// Atualizar jogador
		if (jogador != null && jogador.estaVivo()) {
			jogador.moveIfPossible(mapa);
			jogador.atualizar();
		}

		// Atualizar tiros
		atualizarTiros();

		// Atualizar inimigos
		atualizarInimigos();

		// Atualizar power-ups
		atualizarPowerUps();

		// Verificar condições de vitória/derrota
		verificarEstadoJogo();

		repaint();
	}

	private void atualizarTiros() {
		Iterator<Tiro> it = tiros.iterator();
		while (it.hasNext()) {
			Tiro t = it.next();
			t.atualizar();

			boolean remover = false;

			// Fora dos limites
			if (!t.isAtivo() || t.getX() < -t.getLargura() || t.getY() < -t.getAltura() ||
				t.getX() > largoMapa + 20 || t.getY() > altoMapa + 20) {
				remover = true;
			}

			// Colisão com mapa
			if (!remover && GerenciadorColisao.verificaColisaoComMapa(t, mapa)) {
				remover = true;
			}

			// Colisão com tanques do jogador
			if (!remover && t.getShooter() == Tiro.Shooter.JOGADOR) {
				var inimigo = GerenciadorColisao.tiroAcertaInimigo(t, inimigos);
				if (inimigo.isPresent()) {
					TanqueInimigo en = inimigo.get();
					en.tomarDano(1);
					config.adicionarPontos(100);
					if (!en.estaVivo()) {
						inimigosRestantes--;
						// 20% de chance de drop power-up
						if (rand.nextDouble() < 0.2) {
							gerarPowerUp(en.getX(), en.getY());
						}
					}
					remover = true;
				}
			}

			// Colisão com tanque do jogador
			if (!remover && t.getShooter() == Tiro.Shooter.INIMIGO) {
				if (GerenciadorColisao.tiroAcertaJogador(t, jogador)) {
					jogador.tomarDano(1);
					remover = true;
				}
			}

			if (remover) it.remove();
		}
	}

	private void atualizarInimigos() {
		for (TanqueInimigo enemy : inimigos) {
			if (enemy.estaVivo()) {
				enemy.atualizar();
				enemy.moveIfPossible(mapa);
				if (enemy.wantsToShoot()) {
					Tiro tiro = enemy.atirar();
					if (tiro != null) {
						tiros.add(tiro);
					}
				}
			}
		}

		// Remover inimigos mortos
		inimigos.removeIf(e -> !e.estaVivo());
	}

	private void atualizarPowerUps() {
		Iterator<PowerUp> it = powerUps.iterator();
		while (it.hasNext()) {
			PowerUp pu = it.next();
			pu.atualizar();

			// Colisão com jogador
			if (jogador != null && pu.isAtivo() && jogador.getBounds().intersects(pu.getBounds())) {
				aplicarPowerUp(pu.getTipo());
				pu.setAtivo(false);
			}

			if (!pu.isAtivo()) {
				it.remove();
			}
		}
	}

	private void gerarPowerUp(int x, int y) {
		PowerUp.TipoPowerUp[] tipos = PowerUp.TipoPowerUp.values();
		PowerUp.TipoPowerUp tipo = tipos[rand.nextInt(tipos.length)];
		int tamanho = Mapa.TAMANHO / 2;
		powerUps.add(new PowerUp(x + (Mapa.TAMANHO - tamanho) / 2,
			y + (Mapa.TAMANHO - tamanho) / 2, tamanho, tipo));
	}

	private void aplicarPowerUp(PowerUp.TipoPowerUp tipo) {
		jogador.aplicarPowerUp(tipo);
		config.adicionarPontos(50);
	}

	private void verificarEstadoJogo() {
		// Jogador morreu
		if (!jogador.estaVivo()) {
			config.decrementarVida();
			if (config.getVidas() <= 0) {
				gameOver = true;
				finalizarJogo();
				// Chamar callback de game over
				if (gameOverCallback != null) {
					SwingUtilities.invokeLater(() -> gameOverCallback.onGameOver());
				}
			} else {
				// Reiniciar fase
				jogador.setVida(1);
				int startX = (Mapa.COLUNAS / 2) * Mapa.TAMANHO + (Mapa.TAMANHO - jogador.getLargura()) / 2;
				int startY = (Mapa.LINHAS - 2) * Mapa.TAMANHO + (Mapa.TAMANHO - jogador.getAltura()) / 2;
				jogador.setX(startX);
				jogador.setY(startY);
			}
		}

		// Fase completa (sem inimigos)
		if (inimigosRestantes == 0 && !faseCompleta) {
			faseCompleta = true;
			proximaFase();
		}
	}

	private void proximaFase() {
		config.avancarFase();
		config.adicionarPontos(500);
		
		// Aumentar dificuldade (30% mais tanques)
		spawnInimigos();
		
		faseCompleta = false;
		tiros.clear();
		powerUps.clear();
		inicioFase = System.currentTimeMillis();
	}

	private void finalizarJogo() {
		timer.stop();
		// Será tratado pela janela principal
	}

	// Getters para a janela saber o estado
	public boolean isGameOver() { return gameOver; }
	public boolean isPausado() { return pausado; }
	public void setPausado(boolean pausado) { this.pausado = pausado; }
	public Configuracoes getConfig() { return config; }
	public TanqueJogador getJogador() { return jogador; }
}
