package telas;

import jogo.*;
import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.List;

/**
 * Janela principal do jogo.
 * Design "Retro Arcade Deluxe" com Dificuldade e Ranking restaurados.
 */
public class TelasJogo extends JFrame {

	// PALETA DE CORES RETRO DELUXE
	private JTextArea areaRanking;
	private final Color COR_FUNDO = Color.BLACK;
	private final Color COR_TITULO_TOPO = new Color(255, 200, 0); // Dourado
	private final Color COR_TITULO_SOMBRA = new Color(180, 100, 0); // Laranja escuro
	private final Color COR_TEXTO_NORMAL = Color.WHITE;
	private final Color COR_DESTAQUE = Color.CYAN; // Azul elétrico para seleção
	private final Color COR_SUBTITULO = new Color(150, 150, 150); // Cinza

	private CardLayout layout;
	private JPanel container;
	private Configuracoes config;
	private Ranking ranking;

	private PainelJogoV2 painelJogoAtual;
	private Font fonteRetro;
	private PainelMenuRetro painelMenu;
	private PainelMenuAcao painelPausa;
	private PainelGameOver painelGameOver;

	public TelasJogo() {
		setTitle("Battle City DX");
		setSize(1024, 870);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);

		config = new Configuracoes();
		ranking = new Ranking();

		carregarFonte();

		layout = new CardLayout();
		container = new JPanel(layout);
		container.setBackground(COR_FUNDO);

		painelMenu = new PainelMenuRetro();

		painelPausa = new PainelMenuAcao("PAUSADO", COR_DESTAQUE);
		painelGameOver = new PainelGameOver();

		container.add(painelMenu, "MENU");
		container.add(criarTelaRankingRetro(), "RANKING");
		container.add(painelPausa, "PAUSE");
		container.add(painelGameOver, "GAMEOVER");

		add(container);
		mostrarMenu();
		setVisible(true);
	}

	private void carregarFonte() {
		try {
			InputStream is = getClass().getResourceAsStream("/res/PressStart2P.ttf");
			if (is != null) {
				fonteRetro = Font.createFont(Font.TRUETYPE_FONT, is);
			} else {
				fonteRetro = new Font("DialogInput", Font.BOLD, 20);
			}
		} catch (Exception e) {
			fonteRetro = new Font("Monospaced", Font.BOLD, 20);
		}
	}

	private void mostrarMenu() {
		layout.show(container, "MENU");
		SwingUtilities.invokeLater(() -> {
			painelMenu.resetarMenu();
			painelMenu.requestFocusInWindow(); // Garante que o menu aceite teclado de novo
			container.revalidate();
			container.repaint();
		});
	}

	// PAINEL DE MENU RETRO DELUXE
	private class PainelMenuRetro extends JPanel {
		// Estados: 0=Menu, 1=Nome, 2=Dificuldade, 3=Tanque, 4=Mapa
		private int estado = 0;

		private int cursorPrincipal = 0;   // 0=Jogar, 1=Ranking, 2=Sair
		public int cursorDificuldade = 1;  // 0=Fácil, 1=Médio, 2=Difícil (Médio como padrão)
		private int cursorTanque = 0;      // 0=Ágil, 1=Balanceado, 2=Blindado
		public int cursorMapa = 0;         // 0=Aleatório, 1=M1, 2=M2, 3=M3

		public String nomeDigitado = "";
		private boolean piscar = true;
		private Timer timerPiscar;

		public PainelMenuRetro() {
			setBackground(COR_FUNDO);
			setFocusable(true);

			timerPiscar = new Timer(400, e -> {
				piscar = !piscar;
				repaint();
			});
			timerPiscar.start();

			InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
			ActionMap am = getActionMap();

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
			am.put("moveUp", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (estado == 0) {
						cursorPrincipal = (cursorPrincipal - 1 < 0) ? 2 : cursorPrincipal - 1;
					} else if (estado == 2) {
						cursorDificuldade = (cursorDificuldade - 1 < 0) ? 2 : cursorDificuldade - 1;
					} else if (estado == 3) {
						cursorTanque = (cursorTanque - 1 < 0) ? 2 : cursorTanque - 1;
					} else if (estado == 4) {
						cursorMapa = (cursorMapa - 1 < 0) ? 3 : cursorMapa - 1;
					}
					repaint();
				}
			});

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
			am.put("moveDown", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (estado == 0) {
						cursorPrincipal = (cursorPrincipal + 1 > 2) ? 0 : cursorPrincipal + 1;
					} else if (estado == 2) {
						cursorDificuldade = (cursorDificuldade + 1 > 2) ? 0 : cursorDificuldade + 1;
					} else if (estado == 3) {
						cursorTanque = (cursorTanque + 1 > 2) ? 0 : cursorTanque + 1;
					} else if (estado == 4) {
						cursorMapa = (cursorMapa + 1 > 3) ? 0 : cursorMapa + 1;
					}
					repaint();
				}
			});

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "select");
			am.put("select", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (estado == 0) {
						if (cursorPrincipal == 0) estado = 1;
						else if (cursorPrincipal == 1) {
							atualizarRanking(); // <<--- CHAMA A ATUALIZAÇÃO ANTES DE TROCAR
							layout.show(container, "RANKING");
						}
						else if (cursorPrincipal == 2) System.exit(0);
					} else if (estado == 2) {
						estado = 3;
					} else if (estado == 3) {
						estado = 4;
					} else if (estado == 4) {
						iniciarJogoArcade();
					}
					repaint();
				}
			});

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
			am.put("escape", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (estado > 0) estado--;
					repaint();
				}
			});

			// KEYLISTENER PARA DIGITAÇÃO DE NOME
			addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyTyped(java.awt.event.KeyEvent e) {
					if (estado == 1) { // Apenas durante digitação de nome
						char c = e.getKeyChar();
						
						// Permitir caracteres alfanuméricos e alguns símbolos
						if (Character.isLetterOrDigit(c) || c == ' ' || c == '_' || c == '-') {
							if (nomeDigitado.length() < 20) { // Limitar a 20 caracteres
								nomeDigitado += c;
								repaint();
							}
						}
						
						e.consume();
					}
				}
				
				@Override
				public void keyPressed(java.awt.event.KeyEvent e) {
					if (estado == 1) {
						int tecla = e.getKeyCode();
						
						// Backspace para apagar
						if (tecla == java.awt.event.KeyEvent.VK_BACK_SPACE) {
							if (nomeDigitado.length() > 0) {
								nomeDigitado = nomeDigitado.substring(0, nomeDigitado.length() - 1);
								repaint();
							}
							e.consume();
						}
						// Enter para confirmar
						else if (tecla == java.awt.event.KeyEvent.VK_ENTER) {
							if (!nomeDigitado.trim().isEmpty()) {
								estado = 2; // Ir para dificuldade
								repaint();
							}
							e.consume();
						}
						// ESC para voltar
						else if (tecla == java.awt.event.KeyEvent.VK_ESCAPE) {
							estado = 0; // Voltar ao menu
							repaint();
							e.consume();
						}
					}
				}
			});

		}

		public void resetarMenu() {
			estado = 0;
			cursorPrincipal = 0;
			cursorDificuldade = 1;
			cursorTanque = 0;
			cursorMapa = 0;
			nomeDigitado = "";
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			int h = getHeight();

			if (estado == 0) { // MENU PRINCIPAL
				desenharTituloRetro(g2d, "BATTLE CITY", h / 4, 60f);
				desenharTextoCentralizado(g2d, "EDICAO DELUXE", h / 4 + 50, fonteRetro.deriveFont(20f), COR_DESTAQUE);

				int menuY = h / 2 + 20;
				desenharOpcaoRetro(g2d, "1 PLAYER GAME", menuY, cursorPrincipal == 0);
				desenharOpcaoRetro(g2d, "RANKING", menuY + 50, cursorPrincipal == 1);
				desenharOpcaoRetro(g2d, "EXIT", menuY + 100, cursorPrincipal == 2);
				desenharRodape(g2d, "PRESS START / ENTER", h - 50);

			} else if (estado == 1) { // DIGITAR NOME
				desenharTituloRetro(g2d, "REGISTRO", h / 4, 40f);
				desenharTextoCentralizado(g2d, "DIGITE SEU CODINOME:", h / 4 + 60, fonteRetro.deriveFont(18f), COR_SUBTITULO);

				int boxY = h / 2 - 20;
				String cursorBlink = (piscar ? "_" : "");
				g2d.setFont(fonteRetro.deriveFont(32f));
				g2d.setColor(COR_DESTAQUE);
				FontMetrics fm = g2d.getFontMetrics();
				String textoFinal = nomeDigitado + cursorBlink;
				g2d.drawString(textoFinal, (getWidth() - fm.stringWidth(textoFinal)) / 2, boxY);
				desenharRodape(g2d, "[ENTER] CONFIRMA  [ESC] VOLTA", h - 50);

			} else if (estado == 2) { // DIFICULDADE
				desenharTituloRetro(g2d, "DIFICULDADE", h / 5, 30f);
				int menuY = h / 2 - 50;
				desenharOpcaoRetro(g2d, "RECRUTA (FACIL)", menuY, cursorDificuldade == 0);
				desenharOpcaoRetro(g2d, "VETERANO (MEDIO)", menuY + 45, cursorDificuldade == 1);
				desenharOpcaoRetro(g2d, "ELITE (DIFICIL)", menuY + 90, cursorDificuldade == 2);
				desenharRodape(g2d, "[ENTER] PROXIMO  [ESC] VOLTA", h - 50);

			} else if (estado == 3) { // ESCOLHER TANQUE
				desenharTituloRetro(g2d, "SELECIONE O TANQUE", h / 5, 30f);
				int menuY = h / 2 - 50;
				desenharOpcaoRetro(g2d, "MODELO AGIL", menuY, cursorTanque == 0);
				desenharOpcaoRetro(g2d, "MODELO BALANCEADO", menuY + 45, cursorTanque == 1);
				desenharOpcaoRetro(g2d, "MODELO BLINDADO", menuY + 90, cursorTanque == 2);

				int statsY = h / 2 + 120;
				String stats = "";
				if (cursorTanque == 0) stats = "VIDA: [==  ] VEL: [>>>>]";
				if (cursorTanque == 1) stats = "VIDA: [=== ] VEL: [>>> ]";
				if (cursorTanque == 2) stats = "VIDA: [====] VEL: [>>  ]";

				desenharTextoCentralizado(g2d, "ATRIBUTOS:", statsY, fonteRetro.deriveFont(16f), COR_SUBTITULO);
				desenharTextoCentralizado(g2d, stats, statsY + 30, fonteRetro.deriveFont(18f), COR_DESTAQUE);
				desenharRodape(g2d, "[ENTER] PROXIMO  [ESC] VOLTA", h - 50);

			} else if (estado == 4) { // ESCOLHER MAPA
				desenharTituloRetro(g2d, "ZONA DE BATALHA", h / 5, 30f);
				int menuY = h / 2 - 60;
				desenharOpcaoRetro(g2d, "ALEATORIO [?]", menuY, cursorMapa == 0);
				desenharOpcaoRetro(g2d, "CIDADE (M1)", menuY + 45, cursorMapa == 1);
				desenharOpcaoRetro(g2d, "FLORESTA (M2)", menuY + 90, cursorMapa == 2);
				desenharOpcaoRetro(g2d, "DESERTO (M3)", menuY + 135, cursorMapa == 3);
				desenharRodape(g2d, "[ENTER] INICIAR JOGO!", h - 50);
			}
		}

		private void desenharTituloRetro(Graphics2D g2d, String texto, int y, float tamanho) {
			g2d.setFont(fonteRetro.deriveFont(tamanho));
			FontMetrics fm = g2d.getFontMetrics();
			int x = (getWidth() - fm.stringWidth(texto)) / 2;
			g2d.setColor(COR_TITULO_SOMBRA);
			g2d.drawString(texto, x + 4, y + 4);
			g2d.setColor(COR_TITULO_TOPO);
			g2d.drawString(texto, x, y);
		}

		private void desenharOpcaoRetro(Graphics2D g2d, String texto, int y, boolean selecionado) {
			g2d.setFont(fonteRetro.deriveFont(22f));
			String prefixo = "   ";
			g2d.setColor(COR_TEXTO_NORMAL);
			if (selecionado) {
				prefixo = (piscar ? ">> " : "   ");
				g2d.setColor(COR_DESTAQUE);
			}
			String textoCompleto = prefixo + texto;
			FontMetrics fm = g2d.getFontMetrics();
			int x = (getWidth() - fm.stringWidth(">> " + texto)) / 2;
			g2d.drawString(textoCompleto, x, y);
		}

		private void desenharTextoCentralizado(Graphics2D g2d, String texto, int y, Font fonte, Color cor) {
			g2d.setFont(fonte);
			g2d.setColor(cor);
			FontMetrics fm = g2d.getFontMetrics();
			int x = (getWidth() - fm.stringWidth(texto)) / 2;
			g2d.drawString(texto, x, y);
		}

		private void desenharRodape(Graphics2D g2d, String texto, int y) {
			g2d.setFont(fonteRetro.deriveFont(14f));
			g2d.setColor(COR_SUBTITULO);
			FontMetrics fm = g2d.getFontMetrics();
			int x = (getWidth() - fm.stringWidth(texto)) / 2;
			g2d.drawString(texto, x, y);
		}
	}

	// INICIALIZAÇÃO DO JOGO
	private void iniciarJogoArcade() {
		String difEscolhida = "Médio";
		if (painelMenu.cursorDificuldade == 0) difEscolhida = "Fácil";
		if (painelMenu.cursorDificuldade == 2) difEscolhida = "Difícil";

		String tanqueEscolhido = "Balanceado";
		if (painelMenu.cursorTanque == 0) tanqueEscolhido = "Ágil";
		if (painelMenu.cursorTanque == 2) tanqueEscolhido = "Blindado";

		String mapaEscolhido = "Aleatório";
		if (painelMenu.cursorMapa == 1) mapaEscolhido = "Mapa1";
		if (painelMenu.cursorMapa == 2) mapaEscolhido = "Mapa2";
		if (painelMenu.cursorMapa == 3) mapaEscolhido = "Mapa3";

		config.setNomeJogador(painelMenu.nomeDigitado);
		config.setDificuldade(difEscolhida);
		config.setTipoTanque(tanqueEscolhido);
		config.setMapaSelecionado(mapaEscolhido);
		config.setJogoEmAndamento(true);

		painelJogoAtual = new PainelJogoV2(config,
				() -> {
					// AÇÃO DE GAME OVER
					layout.show(container, "GAMEOVER");
					SwingUtilities.invokeLater(() -> {
						container.revalidate();
						container.repaint();
						painelGameOver.requestFocusInWindow();
					});
				},
				() -> {
					layout.show(container, "PAUSE");
					SwingUtilities.invokeLater(() -> {
						container.revalidate();
						container.repaint();
						painelPausa.resetarCursor();
						painelPausa.requestFocusInWindow();
					});
				}
		);

		container.add(painelJogoAtual, "JOGO");
		layout.show(container, "JOGO");
		painelJogoAtual.requestFocusInWindow();
	}

	// TELA DE RANKING
	private JPanel criarTelaRankingRetro() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(COR_FUNDO);

		JLabel titulo = new JLabel("TOP 10 RANKING", SwingConstants.CENTER);
		if (fonteRetro != null) titulo.setFont(fonteRetro.deriveFont(28f));
		titulo.setForeground(COR_TITULO_TOPO);
		titulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

		areaRanking = new JTextArea();
		areaRanking.setEditable(false);
		areaRanking.setFocusable(false); // IMPORTANTE: Impede o texto de roubar o teclado
		areaRanking.setBackground(COR_FUNDO);
		areaRanking.setForeground(COR_TEXTO_NORMAL);

		if (fonteRetro != null) areaRanking.setFont(fonteRetro.deriveFont(16f));
		else areaRanking.setFont(new Font("Monospaced", Font.PLAIN, 16));

		areaRanking.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel panelBotao = new JPanel();
		panelBotao.setBackground(COR_FUNDO);

		JButton btnVoltar = new JButton("VOLTAR (ESC)");
		btnVoltar.setBackground(new Color(40, 40, 40));
		btnVoltar.setForeground(COR_TEXTO_NORMAL);
		if (fonteRetro != null) btnVoltar.setFont(fonteRetro.deriveFont(14f));
		btnVoltar.setFocusPainted(false);
		btnVoltar.setBorder(BorderFactory.createLineBorder(COR_DESTAQUE, 2));

		// Ação do Botão
		btnVoltar.addActionListener(e -> {
			System.out.println("Botão Voltar clicado!"); // Debug no console
			mostrarMenu();
		});

		panelBotao.add(btnVoltar);
		panelBotao.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

		// READICIONANDO O ESC (Essa parte é essencial para o teclado funcionar)
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "voltarMenu");
		panel.getActionMap().put("voltarMenu", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println("ESC pressionado!"); // Debug no console
				mostrarMenu();
			}
		});

		panel.add(titulo, BorderLayout.NORTH);
		panel.add(areaRanking, BorderLayout.CENTER);
		panel.add(panelBotao, BorderLayout.SOUTH);

		panel.setFocusable(true);
		return panel;
	}

	private void exibirTelaSimples(String titulo, Color corDestaque) {
		config.resetarParaNovaPartida();
		JOptionPane.showMessageDialog(this, titulo + "\n(Pressione OK para voltar ao menu)");
		if (painelJogoAtual != null) container.remove(painelJogoAtual);
		container.add(painelMenu, "MENU");
		mostrarMenu();
	}

	private JPanel criarTelaPlaceholderRetro(String texto) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBackground(COR_FUNDO);
		JLabel l = new JLabel(texto);
		if (fonteRetro != null) l.setFont(fonteRetro.deriveFont(30f));
		else l.setFont(new Font("Monospaced", Font.BOLD, 30));
		l.setForeground(COR_TITULO_TOPO);
		p.add(l);
		return p;
	}

	private class PainelMenuAcao extends JPanel {
		private String titulo;
		private Color corTitulo;
		private int cursor = 0; // 0=Continuar, 1=Reiniciar Fase, 2=Novo Jogo
		private boolean piscar = true;
		private Timer timerPiscar;

		public PainelMenuAcao(String titulo, Color corTitulo) {
			this.titulo = titulo;
			this.corTitulo = corTitulo;
			setBackground(COR_FUNDO);

			setFocusable(true);
			addComponentListener(new java.awt.event.ComponentAdapter() {
				@Override
				public void componentShown(java.awt.event.ComponentEvent e) {
					requestFocusInWindow();
				}
			});

			timerPiscar = new Timer(400, e -> {
				piscar = !piscar;
				repaint();
			});
			timerPiscar.start();

			InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
			ActionMap am = getActionMap();

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
			am.put("moveUp", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cursor = (cursor - 1 < 0) ? 2 : cursor - 1;
					repaint();
				}
			});

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
			am.put("moveDown", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cursor = (cursor + 1 > 2) ? 0 : cursor + 1;
					repaint();
				}
			});

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "select");
			am.put("select", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					executarAcao();
				}
			});

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
			am.put("escape", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cursor = 0;
					executarAcao();
				}
			});
		}

		private void executarAcao() {
			if (cursor == 0) {
				// OPÇÃO 1: CONTINUAR
				layout.show(container, "JOGO");
				if (painelJogoAtual != null) {
					painelJogoAtual.alternarPausa();
					painelJogoAtual.requestFocusInWindow();
				}

			} else if (cursor == 1) {
				// OPÇÃO 2: REINICIAR FASE
				if (painelJogoAtual != null) {
					painelJogoAtual.reiniciarFaseAtual();
				}
				layout.show(container, "JOGO");
				if (painelJogoAtual != null) {
					painelJogoAtual.requestFocusInWindow();
				}

			} else if (cursor == 2) {
				// OPÇÃO 3: NOVO JOGO / SAIR
				if (painelJogoAtual != null) {
					painelJogoAtual.finalizarJogo();
					container.remove(painelJogoAtual);
					painelJogoAtual = null;
				}
				config.resetarParaNovaPartida();
				voltarParaMenu();
			}
			cursor = 0;
		}

		private void voltarParaMenu() {
			layout.show(container, "MENU");
			SwingUtilities.invokeLater(() -> {
				container.revalidate();
				container.repaint();
				painelMenu.resetarMenu();
				painelMenu.requestFocusInWindow();
			});
		}

		public void resetarCursor() {
			this.cursor = 0;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			int h = getHeight();

			g2d.setFont(fonteRetro != null ? fonteRetro.deriveFont(50f) : new Font("Monospaced", Font.BOLD, 50));
			FontMetrics fm = g2d.getFontMetrics();
			int xTitulo = (getWidth() - fm.stringWidth(titulo)) / 2;

			g2d.setColor(COR_TITULO_SOMBRA);
			g2d.drawString(titulo, xTitulo + 4, h / 4 + 4);
			g2d.setColor(corTitulo);
			g2d.drawString(titulo, xTitulo, h / 4);

			int menuY = h / 2;
			desenharOpcao(g2d, "CONTINUAR", menuY, cursor == 0);
			desenharOpcao(g2d, "REINICIAR FASE", menuY + 50, cursor == 1);
			desenharOpcao(g2d, "NOVO JOGO / SAIR", menuY + 100, cursor == 2);
		}

		private void desenharOpcao(Graphics2D g2d, String texto, int y, boolean selecionado) {
			g2d.setFont(fonteRetro != null ? fonteRetro.deriveFont(20f) : new Font("Monospaced", Font.BOLD, 20));
			String prefixo = selecionado ? (piscar ? ">> " : "   ") : "   ";
			g2d.setColor(selecionado ? COR_DESTAQUE : COR_TEXTO_NORMAL);

			String textoCompleto = prefixo + texto;
			FontMetrics fm = g2d.getFontMetrics();
			int x = (getWidth() - fm.stringWidth(">> " + texto)) / 2;
			g2d.drawString(textoCompleto, x, y);
		}
	}

	private class PainelGameOver extends JPanel {
		private boolean piscar = true;
		private Timer timerPiscar;

		public PainelGameOver() {
			setBackground(COR_FUNDO);
			setFocusable(true);

			timerPiscar = new Timer(400, e -> {
				piscar = !piscar;
				repaint();
			});
			timerPiscar.start();

			addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
					voltarAoMenuInicial();
				}
			});

			InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			ActionMap am = getActionMap();

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "voltar");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "voltar");

			am.put("voltar", new AbstractAction() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					voltarAoMenuInicial();
				}
			});
		}

		private void voltarAoMenuInicial() {
			System.out.println("OPA! O botão foi apertado/clicado!");
			// Limpa o jogo atual da memória
			if (painelJogoAtual != null) {
				painelJogoAtual.setPausado(true);
				container.remove(painelJogoAtual);
				painelJogoAtual = null;
			}
			// Volta para a tela inicial
			mostrarMenu();
			container.revalidate();
			container.repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			int h = getHeight();

			// Desenha o título GAME OVER grandão em vermelho
			g2d.setFont(fonteRetro != null ? fonteRetro.deriveFont(60f) : new Font("Monospaced", Font.BOLD, 60));
			FontMetrics fm = g2d.getFontMetrics();
			String titulo = "GAME OVER";
			int xTitulo = (getWidth() - fm.stringWidth(titulo)) / 2;

			g2d.setColor(COR_TITULO_SOMBRA);
			g2d.drawString(titulo, xTitulo + 4, h / 3 + 4);
			g2d.setColor(Color.RED);
			g2d.drawString(titulo, xTitulo, h / 3);

			// Desenha a única opção disponível piscando
			g2d.setFont(fonteRetro != null ? fonteRetro.deriveFont(20f) : new Font("Monospaced", Font.BOLD, 20));
			String texto = (piscar ? ">> VOLTAR AO MENU <<" : "   VOLTAR AO MENU   ");
			fm = g2d.getFontMetrics();
			int xTexto = (getWidth() - fm.stringWidth(">> VOLTAR AO MENU <<")) / 2;

			g2d.setColor(COR_DESTAQUE);
			g2d.drawString(texto, xTexto, h / 2 + 50);
		}
	}

	public void atualizarRanking() {
		this.ranking.carregar();

		StringBuilder sb = new StringBuilder();
		sb.append("POS | JOGADOR          | PONTOS | FASE\n");
		sb.append("========================================\n");

		// Use o getTop10() para garantir que só apareçam os 10 melhores
		List<Ranking.Jogador> lista = ranking.getTop10();

		for (int i = 0; i < lista.size(); i++) {
			Ranking.Jogador j = lista.get(i);
			sb.append(String.format("%2dº | %-16s | %6d | %2d\n",
					(i + 1), j.nome, j.pontuacao, j.fase));
		}

		if (areaRanking != null) {
			areaRanking.setText(sb.toString());
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(TelasJogo::new);
	}
}
