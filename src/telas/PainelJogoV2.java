package telas;

import jogo.*;
import mundo.*;
import objeto.*;
import controles.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Painel principal do jogo Battle City.
 * Responsável por renderizar e atualizar toda a lógica de jogo.
 */


public class PainelJogoV2 extends JPanel implements ActionListener {


	private Mapa mapa;
	private TanqueJogador jogador;
	private List<Tiro> tiros = new CopyOnWriteArrayList<>();
	private List<TanqueInimigo> inimigos = new CopyOnWriteArrayList<>();
	private List<PowerUp> powerUps = new ArrayList<>();
	private ControleTeclado controle;
	private Timer timer;
	private Runnable onGameOver; // Ação para quando o jogador perde
	private Runnable onPause;    // Ação para quando o jogo pausa
	private boolean gameOverProcessado = false;

	private String powerUpAtivoTexto = "NENHUM";

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

	// Tempo de fase (em milissegundos)
	private long tempoFaseMs;
	private long tempoRestanteMs;
	private long tempoPausadoInicio = 0;

	// Sistema de blocos destrutíveis
	private int[][] mapaAtual;

	// Painel HUD
	private int largoMapa;
	private int altoMapa;

	// Sistema de power-ups globais
	private boolean inimigosCongelados = false;
	private int tempoInimigosCongelados = 0;

	// Callback para game over
	public interface GameOverCallback {
		void onGameOver();
	}

	// Callback para pausa
	public interface PauseCallback {
		void onPausa();
	}

	private GameOverCallback gameOverCallback;
	private PauseCallback pauseCallback;

	public PainelJogoV2(Configuracoes config) {
		this.config = config != null ? config : new Configuracoes();
		this.gameOverCallback = null;
		this.pauseCallback = null;
		inicializarJogo();
	}

	public PainelJogoV2(Configuracoes config, GameOverCallback callback) {
		this.config = config != null ? config : new Configuracoes();
		this.gameOverCallback = callback;
		this.pauseCallback = null;
		inicializarJogo();
	}

	public PainelJogoV2(Configuracoes config, GameOverCallback gameOverCallback, PauseCallback pauseCallback) {
		this.config = config != null ? config : new Configuracoes();
		this.gameOverCallback = gameOverCallback;
		this.pauseCallback = pauseCallback;
		inicializarJogo();
	}

	private void inicializarJogo() {
		setBackground(Color.BLACK);

		// Selecionar mapa baseado em configuração
		Mapa mapaBase = selecionarMapa();

		// Copiar matriz do mapa para permitir destruição de blocos
		int[][] mapaOriginal = mapaBase.getMatriz();
		this.mapaAtual = new int[mapaOriginal.length][];
		for (int i = 0; i < mapaOriginal.length; i++) {
			this.mapaAtual[i] = mapaOriginal[i].clone();
		}

		final int[][] matrizFinal = this.mapaAtual;
		this.mapa = new Mapa() {
			@Override
			public int[][] getMatriz() {
				return matrizFinal;
			}
		};

		largoMapa = Mapa.COLUNAS * Mapa.TAMANHO;
		altoMapa = Mapa.LINHAS * Mapa.TAMANHO;

		int larguraComHUD = largoMapa + 192;

		setPreferredSize(new Dimension(larguraComHUD, altoMapa));
		setMinimumSize(new Dimension(larguraComHUD, altoMapa));
		setMaximumSize(new Dimension(larguraComHUD, altoMapa));
		setSize(new Dimension(larguraComHUD, altoMapa));

		// Criar tanque do jogador
		int tamanhoTanque = Mapa.TAMANHO * 3 / 4;

		int startX = (4 * Mapa.TAMANHO) + (Mapa.TAMANHO - tamanhoTanque) / 2;
		int startY = (11 * Mapa.TAMANHO) + (Mapa.TAMANHO - tamanhoTanque) / 2;

		jogador = new TanqueJogador(startX, startY, tamanhoTanque);
		jogador.setVida(config.getVidas());

		// Aplicar tipo de tanque selecionado
		aplicarTipoTanque();

		jogador.setMapaReferencia(mapa);
		jogador.setPausado(false);
		new Thread(jogador).start();

		// Spawnar inimigos baseado em dificuldade
		spawnInimigos();

		// Controle de teclado
		controle = new ControleTeclado(jogador);
		controle.setOnAtiraListener(tiro -> {
			if (!pausado && !gameOver && tiro != null) {
				tiros.add(tiro);
				new Thread(tiro).start();
			}
		});
		addKeyListener(controle);

		// KeyListener para pausa (ESC)
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !gameOver) {
					alternarPausa();
					e.consume();
				}
			}
		});

		setFocusable(true);
		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentShown(java.awt.event.ComponentEvent e) {
				requestFocusInWindow();
			}
		});
		requestFocusInWindow();

		timer = new Timer(16, this);
		timer.start();

		inicioFase = System.currentTimeMillis();
		inicializarTempoFase();
	}

	/**
	 * Calcula o tempo disponível para a fase atual.
	 * Fase 1: 30 segundos
	 * Fase 2: 40 segundos
	 * Fase 3: 50 segundos
	 * E assim por diante, adicionando 10 segundos a cada fase.
	 */
	private void inicializarTempoFase() {
		int faseAtual = config.getFase();
		long tempoBase = 30 * 1000; // 30 segundos em milissegundos
		long tempoAdicional = (faseAtual - 1) * 10 * 1000; // 10 segundos por fase
		tempoFaseMs = tempoBase + tempoAdicional;
		tempoRestanteMs = tempoFaseMs;
		inicioFase = System.currentTimeMillis();
	}

	/**
	 * Aplica as características do tipo de tanque selecionado.
	 */
	private void aplicarTipoTanque() {
		String tipo = config.getTipoTanque();
		if (tipo != null) {
			switch (tipo) {
				case "Ágil":
					jogador.aplicarTipoTanque(TanqueJogador.TipoTanque.AGIL);
					break;
				case "Balanceado":
					jogador.aplicarTipoTanque(TanqueJogador.TipoTanque.BALANCEADO);
					break;
				case "Blindado":
					jogador.aplicarTipoTanque(TanqueJogador.TipoTanque.BLINDADO);
					break;
				default:
					jogador.aplicarTipoTanque(TanqueJogador.TipoTanque.BALANCEADO);
			}
		}
	}

	/**
	 * Destrói um bloco na matriz de mapa
	*/
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

			TanqueInimigo inimigo = new TanqueInimigo(x, y, tamanhoTanque, tipo);
			inimigo.setMapaReferencia(mapa);
			inimigos.add(inimigo);
			new Thread(inimigo).start();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (mapa != null) {
			mapa.desenhar(g);
		}

		for (PowerUp pu : powerUps) {
			pu.desenhar(g);
		}

		if (jogador != null) {
			jogador.desenhar(g);
		}
		for (TanqueInimigo enemy : inimigos) {
			enemy.desenhar(g);
		}

		if (mapa != null) {
			mapa.desenharArvores(g);
		}

		for (Tiro t : tiros) {
			t.desenhar(g);
		}

		desenharHUD(g);
	}

	private void desenharHUD(Graphics g) {
		// Posição X onde começa o painel lateral
		int hudX = largoMapa;

		g.setColor(new Color(40, 40, 40));
		g.fillRect(hudX, 0, 192, altoMapa);

		g.setColor(Color.WHITE);
		g.drawLine(hudX, 0, hudX, altoMapa);

		// Configuração da fonte e margens
		g.setFont(new Font("Arial", Font.BOLD, 16));
		int margemY = 50;
		int margemX = hudX + 20;
		int espacamento = 45; // Espaço entre as linhas

		// Vidas Restantes
		int vidas = (jogador != null) ? jogador.getVida() : 0;
		g.setColor(Color.WHITE);
		g.drawString("VIDAS: " + vidas, margemX, margemY);
		margemY += espacamento;

		// Pontuação
		g.drawString("PONTOS: " + config.getPontuacao(), margemX, margemY);
		margemY += espacamento;

		// Fase Atual
		g.drawString("FASE: " + config.getFase(), margemX, margemY);
		margemY += espacamento;

		// Inimigos Restantes
		g.drawString("INIMIGOS: " + inimigosRestantes, margemX, margemY);
		margemY += espacamento;

		// Tempo Restante
		long tempoAtual = System.currentTimeMillis() - inicioFase;
		tempoRestanteMs = Math.max(0, tempoFaseMs - tempoAtual);
		int segundosRestantes = (int) (tempoRestanteMs / 1000);

		if (segundosRestantes <= 10) {
			g.setColor(new Color(255, 50, 50)); // Vermelho
		} else if (segundosRestantes <= 20) {
			g.setColor(new Color(255, 200, 0)); // Amarelo
		} else {
			g.setColor(Color.WHITE);
		}
		g.drawString("TEMPO: " + segundosRestantes + "s", margemX, margemY);
		margemY += espacamento;

		// Power-up Ativo (ATUALIZADO)
		g.setColor(Color.WHITE);
		g.drawString("POWER-UP:", margemX, margemY);

		// Se não tiver power-up, pinta de cinza. Se tiver, pinta de amarelo!
		if (powerUpAtivoTexto != null && powerUpAtivoTexto.equals("NENHUM")) {
			g.setColor(Color.GRAY);
		} else {
			g.setColor(Color.YELLOW);
		}

		// Escreve o nome do power-up que o jogador pegou por último
		g.drawString(powerUpAtivoTexto, margemX, margemY + 25);

		// AVISOS DE STATUS NO MEIO DA TELA (Pausado, Game Over)
		if (pausado) {
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Arial", Font.BOLD, 36));
			g.drawString("PAUSADO", largoMapa / 2 - 80, altoMapa / 2);
		}

		if (gameOver) {
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.BOLD, 36));
			g.drawString("GAME OVER", largoMapa / 2 - 100, altoMapa / 2);
		}

		if (faseCompleta) {
			g.setColor(Color.GREEN);
			g.setFont(new Font("Arial", Font.BOLD, 36));
			g.drawString("FASE COMPLETA!", largoMapa / 2 - 150, altoMapa / 2);
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

		// Atualizar tiros
		atualizarTiros();

		// Atualizar inimigos (apenas remove mortos)
		atualizarInimigos();

		// Atualizar power-ups
		atualizarPowerUps();

		// Verificar condições de vitória/derrota
		verificarEstadoJogo();

		repaint();
	}

	private void atualizarTiros() {
		for (Tiro t : tiros) {

			boolean remover = false;

			if (!t.isAtivo() || t.getX() < -t.getLargura() || t.getY() < -t.getAltura() ||
					t.getX() > largoMapa + 20 || t.getY() > altoMapa + 20) {
				remover = true;
			}

			// Colisão com mapa - REGRA DE DESTRUIÇÃO
			if (!remover) {
				int col = t.getX() / Mapa.TAMANHO;
				int row = t.getY() / Mapa.TAMANHO;

				if (row >= 0 && row < Mapa.LINHAS && col >= 0 && col < Mapa.COLUNAS) {
					int tipoBloco = mapaAtual[row][col];

					if (tipoBloco == Mapa.TIJOLO) {
						// TIJOLO: Remove tiro E destroi o bloco na matriz
						mapaAtual[row][col] = Mapa.VAZIO;
						t.parar();
						remover = true;
					} else if (tipoBloco == Mapa.ACO) {
						// ACO: Remove tiro, mas matriz não muda
						t.parar();
						remover = true;
					} else if (tipoBloco == Mapa.PAREDE) {
						// PAREDE: Remove tiro
						t.parar();
						remover = true;
					} else if (tipoBloco == Mapa.BASE) {
						// BASE: Remove tiro E aciona Game Over (se não estiver protegida)
						if (!jogador.isBaseProtegida()) {
							gameOver = true;
						}
						t.parar();
						remover = true;
					} else if (tipoBloco == Mapa.AGUA || tipoBloco == Mapa.ARVORE) {
						// AGUA e ARVORE: Tiro passa direto, sem colisão
						remover = false;
					}
				}
			}

			// Colisão com tanques inimigos
			if (!remover && t.getShooter() == Tiro.Shooter.JOGADOR) {
				var inimigo = GerenciadorColisao.tiroAcertaInimigo(t, inimigos);
				if (inimigo.isPresent()) {
					TanqueInimigo en = inimigo.get();
					en.tomarDano(1);
					config.adicionarPontos(100);
					if (!en.estaVivo()) {
						inimigosRestantes--;
						// 20% de chance de drop power-up em posição aleatória
						if (rand.nextDouble() < 0.2) {
							gerarPowerUpAleatorio();
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

			// Remover tiro se necessário
			if (remover) {
				tiros.remove(t);
			}
		}
	}

	private void atualizarInimigos() {
		// Verificar se inimigos querem atirar e criar tiros
		for (TanqueInimigo inimigo : inimigos) {
			if (inimigo.isWantsToShoot() && !pausado && !gameOver) {
				Tiro tiro = inimigo.atirar();
				if (tiro != null) {
					tiros.add(tiro);
					new Thread(tiro).start();
				}
				inimigo.setWantsToShoot(false);
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

			// Colisão com jogador (Se o item está na tela e o jogador encostou)
			if (jogador != null && pu.isAtivo() && jogador.getBounds().intersects(pu.getBounds())) {
				aplicarPowerUp(pu.getTipo()); // Aplica o efeito
				pu.setAtivo(false); // Marca para sumir
			}

			// Remove da lista se foi pego ou se o tempo de tela dele acabou
			if (!pu.isAtivo()) {
				it.remove();
			}
		}

		if (inimigosCongelados) {
			tempoInimigosCongelados--;
			if (tempoInimigosCongelados <= 0) {
				inimigosCongelados = false;
				for (TanqueInimigo enemy : inimigos) {
					enemy.congelar(0);
				}
			}
		}
	}

	private void ativarPowerUpPa() {
		alterarProtecaoDaBase(mundo.Mapa.ACO);

		// Cria um timer de 15 segundos para reverter o efeito
		javax.swing.Timer timerPa = new javax.swing.Timer(15000, e -> {
			// Volta os blocos para Tijolo (Mapa.TIJOLO que vale 3)
			alterarProtecaoDaBase(mundo.Mapa.TIJOLO);
			repaint(); // Atualiza a tela para mostrar que o aço sumiu
		});

		timerPa.setRepeats(false); // O timer só roda uma vez e para
		timerPa.start();
	}

	private void alterarProtecaoDaBase(int tipoBloco) {
		int[][] matriz = mapa.getMatriz();

		int linhaAguia = 12;
		int colunaAguia = 6;

		// Esquerda
		if (colunaAguia - 1 >= 0) {
			matriz[linhaAguia][colunaAguia - 1] = tipoBloco;
		}
		// Topo-Esquerda
		if (linhaAguia - 1 >= 0 && colunaAguia - 1 >= 0) {
			matriz[linhaAguia - 1][colunaAguia - 1] = tipoBloco;
		}
		// Topo (Em cima)
		if (linhaAguia - 1 >= 0) {
			matriz[linhaAguia - 1][colunaAguia] = tipoBloco;
		}
		// Topo-Direita
		if (linhaAguia - 1 >= 0 && colunaAguia + 1 < mundo.Mapa.COLUNAS) {
			matriz[linhaAguia - 1][colunaAguia + 1] = tipoBloco;
		}
		// Direita
		if (colunaAguia + 1 < mundo.Mapa.COLUNAS) {
			matriz[linhaAguia][colunaAguia + 1] = tipoBloco;
		}

		// Pede pro Java desenhar a tela de novo com os novos blocos
		repaint();
	}

	private void gerarPowerUp(int x, int y) {
		PowerUp.TipoPowerUp[] tipos = PowerUp.TipoPowerUp.values();
		PowerUp.TipoPowerUp tipo = tipos[rand.nextInt(tipos.length)];
		int tamanho = Mapa.TAMANHO / 2;
		powerUps.add(new PowerUp(x + (Mapa.TAMANHO - tamanho) / 2,
			y + (Mapa.TAMANHO - tamanho) / 2, tamanho, tipo));
	}

	private void gerarPowerUpAleatorio() {
		// Encontra uma posição aleatória vazia (valor 0 na matriz)
		int tentativas = 0;
		int maxTentativas = 50;

		while (tentativas < maxTentativas) {
			int colAleatororia = rand.nextInt(Mapa.COLUNAS);
			int linhaAleatoria = rand.nextInt(Mapa.LINHAS);

			// Verificar se a posição está vazia
			if (mapaAtual[linhaAleatoria][colAleatororia] == Mapa.VAZIO) {
				// Converter para coordenadas de pixel
				int x = colAleatororia * Mapa.TAMANHO;
				int y = linhaAleatoria * Mapa.TAMANHO;

				// Gerar power-up aleatório
				PowerUp.TipoPowerUp[] tipos = PowerUp.TipoPowerUp.values();
				PowerUp.TipoPowerUp tipo = tipos[rand.nextInt(tipos.length)];
				int tamanho = Mapa.TAMANHO / 2;

				powerUps.add(new PowerUp(
					x + (Mapa.TAMANHO - tamanho) / 2,
					y + (Mapa.TAMANHO - tamanho) / 2,
					tamanho,
					tipo
				));
				return;
			}
			tentativas++;
		}

		for (int linha = 0; linha < Mapa.LINHAS; linha++) {
			for (int col = 0; col < Mapa.COLUNAS; col++) {
				if (mapaAtual[linha][col] == Mapa.VAZIO) {
					int x = col * Mapa.TAMANHO;
					int y = linha * Mapa.TAMANHO;

					PowerUp.TipoPowerUp[] tipos = PowerUp.TipoPowerUp.values();
					PowerUp.TipoPowerUp tipo = tipos[rand.nextInt(tipos.length)];
					int tamanho = Mapa.TAMANHO / 2;

					powerUps.add(new PowerUp(
						x + (Mapa.TAMANHO - tamanho) / 2,
						y + (Mapa.TAMANHO - tamanho) / 2,
						tamanho,
						tipo
					));
					return;
				}
			}
		}
	}

	private void explodir_todos_inimigos() {
		// Mata todos os inimigos visíveis e atualiza o contador
		for (TanqueInimigo enemy : inimigos) {
			if (enemy.estaVivo()) { // Garante que só afeta os que ainda estão vivos no ecrã
				enemy.tomarDano(999); // Dano massivo para matar
				config.adicionarPontos(100); // Bônus por cada inimigo destruído

				inimigosRestantes--;
			}
		}
	}

	private void aplicarPowerUp(PowerUp.TipoPowerUp tipo) {
		switch (tipo) {
			case ESTRELA:
				jogador.aplicarPowerUp(tipo);
				config.adicionarPontos(50);
				powerUpAtivoTexto = "ESTRELA (TIRO)";
				break;

			case CAPACETE:
				jogador.aplicarPowerUp(tipo);
				config.adicionarPontos(50);
				powerUpAtivoTexto = "CAPACETE (ESCUDO)";
				break;

			case VIDA:
				jogador.aplicarPowerUp(tipo);
				config.adicionarPontos(50);
				powerUpAtivoTexto = "+1 VIDA";
				break;

			case RELOGIO:
				// Congela inimigos por 10 segundos
				inimigosCongelados = true;
				tempoInimigosCongelados = 600; // ~10 segundos a 60FPS
				for (TanqueInimigo enemy : inimigos) {
					enemy.congelar(600);
				}

				config.adicionarPontos(50);
				powerUpAtivoTexto = "RELÓGIO (GELO)";
				break;

			case PA:
				// Protege a base
				jogador.aplicarPowerUp(tipo); // Pode manter se o jogador também ganha algo
				ativarPowerUpPa();
				config.adicionarPontos(50);
				powerUpAtivoTexto = "PÁ (BASE BLINDADA)";
				break;

			case BOMBA:
				explodir_todos_inimigos();
				config.adicionarPontos(50);
				powerUpAtivoTexto = "BOMBA (KABOOM)";
				break;
		}
	}

	private void verificarEstadoJogo() {
		// BASE foi destruída (gameOver ativado nos tiros)
		if (gameOver) {
			acionarGameOverMenu();
			return;
		}

		// Tempo da fase acabou
		long tempoAtual = System.currentTimeMillis() - inicioFase;
		if (tempoAtual >= tempoFaseMs) {
			acionarGameOverMenu();
			return;
		}

		// Jogador tomou tiro
		if (!jogador.estaVivo()) {
			config.decrementarVida();

			if (config.getVidas() <= 0) {
				// Acabaram as vidas de vez
				acionarGameOverMenu();
				return;
			} else {
				// Perdeu 1 vida mas ainda tem mais, recomeça do ponto inicial!
				jogador.setVida(1);
				int startX = (4 * Mapa.TAMANHO) + (Mapa.TAMANHO - jogador.getLargura()) / 2;
				int startY = (11 * Mapa.TAMANHO) + (Mapa.TAMANHO - jogador.getAltura()) / 2;
				jogador.setX(startX);
				jogador.setY(startY);
			}
		}

		// Fase completa (matou todos os inimigos)
		if (inimigosRestantes == 0 && !faseCompleta) {
			faseCompleta = true;
			proximaFase();
		}
	}

	private void proximaFase() {
		config.avancarFase();
		config.adicionarPontos(500);

		// Resetar blocos para a próxima fase
		Mapa mapaBase = selecionarMapa();
		int[][] mapaOriginal = mapaBase.getMatriz();
		for (int i = 0; i < mapaOriginal.length; i++) {
			mapaAtual[i] = mapaOriginal[i].clone();
		}

		//RESETAR A POSIÇÃO DO JOGADOR
		if (jogador != null) {
			// Calcula a posição inicial (Coluna 4, Linha 11 - padrão do seu mapa)
			int startX = (4 * Mapa.TAMANHO) + (Mapa.TAMANHO - jogador.getLargura()) / 2;
			int startY = (11 * Mapa.TAMANHO) + (Mapa.TAMANHO - jogador.getAltura()) / 2;

			// Reposiciona o tanque
			jogador.setX(startX);
			jogador.setY(startY);

		}

		// Aumentar dificuldade (30% mais tanques)
		spawnInimigos();

		faseCompleta = false;
		tiros.clear();
		powerUps.clear();
		inicioFase = System.currentTimeMillis();
		inicializarTempoFase();
	}

	public void finalizarJogo() {
		timer.stop();

		if (jogador != null) jogador.parar();
		for (TanqueInimigo enemy : inimigos) enemy.parar();
		for (Tiro t : tiros) t.parar();

		if (!gameOverProcessado) {
			gameOverProcessado = true;

			Ranking rankingPersistente = new Ranking();
			rankingPersistente.carregar();
			rankingPersistente.adicionarJogador(config.getNomeJogador(), config.getPontuacao(), config.getFase());

			System.out.println("DEBUG: Ranking salvo. Chamando callback de Game Over...");

			if (gameOverCallback != null) {
				gameOverCallback.onGameOver();
			}

			repaint();
		}
	}

	private void acionarGameOverMenu() {
		if (gameOverProcessado) return;
		gameOverProcessado = true;
		gameOver = true;

		finalizarJogo(); // Para os tanques e o tempo do jogo
		repaint(); // Desenha a palavra GAME OVER na tela e a explosão

		Timer delay = new Timer(2500, evt -> {
			if (gameOverCallback != null) {
				gameOverCallback.onGameOver(); // Manda o TelasJogo abrir a tela de GAMEOVER

				//Obriga a tela principal do Java a atualizar e mostrar o Menu!
				Container parent = getParent();
				if (parent != null) {
					parent.revalidate();
					parent.repaint();
				}
			}
		});
		delay.setRepeats(false);
		delay.start();
	}

	// Getters para a janela saber o estado
	public boolean isGameOver() { return gameOver; }
	public boolean isPausado() { return pausado; }
	public void setPausado(boolean pausado) { this.pausado = pausado; }
	public Configuracoes getConfig() { return config; }
	public TanqueJogador getJogador() { return jogador; }

	/**
	 * Alterna o estado de pausa e notifica os listeners.
	 */
	public void alternarPausa() {
		pausado = !pausado;

		if (pausado) {
			// Grava o exato momento em que o jogo foi pausado
			tempoPausadoInicio = System.currentTimeMillis();
		} else {
			// Quando voltar do pause, "devolve" os segundos que ficaram parados pro cronômetro!
			if (tempoPausadoInicio > 0) {
				long tempoFicouPausado = System.currentTimeMillis() - tempoPausadoInicio;
				inicioFase += tempoFicouPausado;
				tempoPausadoInicio = 0;
			}
		}

		// Pausar/Despausar TODAS as entidades com Threads
		if (jogador != null) {
			jogador.setPausado(pausado);
		}

		for (TanqueInimigo enemy : inimigos) {
			enemy.setPausado(pausado);
		}

		for (Tiro t : tiros) {
			t.setPausado(pausado);
		}

		if (pausado && pauseCallback != null) {
			SwingUtilities.invokeLater(() -> pauseCallback.onPausa());
		}
		repaint();
	}

	/**
	 * Retira o jogo do estado de pausa e ajusta o relógio para voltar de onde parou.
	 */
	public void continuarJogo() {
		// Só despausa se realmente estiver pausado
		if (this.pausado) {
			this.pausado = false;

			// Devolve os segundos que o jogo ficou parado para o cronômetro não bugar
			if (tempoPausadoInicio > 0) {
				long tempoFicouPausado = System.currentTimeMillis() - tempoPausadoInicio;
				inicioFase += tempoFicouPausado;
				tempoPausadoInicio = 0;
			}

			// Garante que os tiros voltem a se mover
			if (tiros != null) {
				for (Tiro t : tiros) {
					t.setPausado(false);
				}
			}

			repaint(); // Atualiza a tela
		}
	}

	/**
	 * Reseta completamente a fase atual, recriando mapa, inimigos e jogador.
	 */
	public void reiniciarFaseAtual() {
		// Limpa os bloqueios de fim de jogo e pausa
		this.gameOver = false;
		this.faseCompleta = false;
		this.pausado = false;
		this.tempoPausadoInicio = 0; // Zera o relógio do pause

		// Se o jogador deu Game Over, restauramos as vidas iniciais
		if (config.getVidas() <= 0) {
			config.setVidas(3);
		}

		// Limpa todos os itens e tiros da tela
		this.tiros.clear();
		this.powerUps.clear();
		this.inimigosCongelados = false;

		// Reconstrói os blocos quebráveis do mapa
		Mapa mapaBase = selecionarMapa();
		int[][] mapaOriginal = mapaBase.getMatriz();
		for (int i = 0; i < mapaOriginal.length; i++) {
			this.mapaAtual[i] = mapaOriginal[i].clone();
		}

		// Reposiciona o tanque do jogador na base
		if (jogador != null) {
			int tamanhoTanque = Mapa.TAMANHO * 3 / 4;
			int startX = (4 * Mapa.TAMANHO) + (Mapa.TAMANHO - tamanhoTanque) / 2;
			int startY = (11 * Mapa.TAMANHO) + (Mapa.TAMANHO - tamanhoTanque) / 2;

			jogador.setX(startX);
			jogador.setY(startY);
			jogador.setVida(config.getVidas());
			// jogador.removerPowerUps();
		}

		// Recria os inimigos nas posições iniciais
		spawnInimigos();

		// Zera o cronômetro da fase para começar de novo
		inicializarTempoFase();

		// Religa o motor do jogo
		if (!timer.isRunning()) {
			timer.start();
		}

		repaint();
	}

	public void setPauseCallback(PauseCallback callback) {
		this.pauseCallback = callback;
	}
}