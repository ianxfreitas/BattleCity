package telas;

import jogo.*;
import mundo.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * DEPRECATED - Use TelasJogo.java ao invés.
 * Versão alternativa do TelasJogo.
 */
class TelasJogoNova extends JFrame {

	private CardLayout layout;
	private JPanel container;
	private Configuracoes config;
	private Ranking ranking;

	private String nomeJogador = "Jogador";
	private String dificuldade = "Médio";
	private String tipoTanque = "Balanceado";
	private int vidas = 3;
	private String mapaSelecionado = "Aleatório";

	private JButton btnMapAleatorio, btnMap1, btnMap2, btnMap3;

	public TelasJogoNova() {
		setTitle("Battle City");
		setSize(600, 700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);

		config = new Configuracoes();
		ranking = new Ranking();

		layout = new CardLayout();
		container = new JPanel(layout);

		// Adicionar telas
		container.add(criarMenuInicial(), "MENU");
		container.add(criarTelaConfiguracao(), "CONFIG");
		container.add(criarTelaRanking(), "RANKING");

		add(container);
		layout.show(container, "MENU");

		setVisible(true);
	}

	// ==================== MENU INICIAL ====================
	private JPanel criarMenuInicial() {
		JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
		panel.setBackground(new Color(30, 30, 30));
		panel.setBorder(BorderFactory.createEmptyBorder(80, 120, 80, 120));

		JLabel titulo = new JLabel("BATTLE CITY", SwingConstants.CENTER);
		titulo.setFont(new Font("Arial", Font.BOLD, 48));
		titulo.setForeground(new Color(100, 200, 100));

		JButton btnJogar = new JButton("▶ Jogar");
		JButton btnRanking = new JButton("🏆 Ranking");
		JButton btnSair = new JButton("✕ Sair");

		estilizarBotao(btnJogar);
		estilizarBotao(btnRanking);
		estilizarBotao(btnSair);

		btnJogar.addActionListener(e -> layout.show(container, "CONFIG"));
		btnRanking.addActionListener(e -> layout.show(container, "RANKING"));
		btnSair.addActionListener(e -> System.exit(0));

		panel.add(titulo);
		panel.add(btnJogar);
		panel.add(btnRanking);
		panel.add(btnSair);

		return panel;
	}

	// ==================== TELA CONFIGURAÇÃO ====================
	private JPanel criarTelaConfiguracao() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(new Color(40, 40, 40));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

		// TÍTULO
		JLabel titulo = new JLabel("CONFIGURAÇÃO DO JOGO");
		titulo.setFont(new Font("Arial", Font.BOLD, 24));
		titulo.setForeground(Color.WHITE);
		titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

		// NOME
		JLabel lblNome = new JLabel("Nome do Jogador:");
		lblNome.setForeground(Color.WHITE);
		JTextField campoNome = new JTextField(15);
		campoNome.setText("Jogador");

		JPanel panelNome = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelNome.setBackground(new Color(40, 40, 40));
		panelNome.add(lblNome);
		panelNome.add(campoNome);

		// DIFICULDADE
		JLabel lblDificuldade = new JLabel("Dificuldade:");
		lblDificuldade.setForeground(Color.WHITE);
		JRadioButton rbFacil = new JRadioButton("Fácil");
		JRadioButton rbMedio = new JRadioButton("Médio");
		JRadioButton rbDificil = new JRadioButton("Difícil");
		rbMedio.setSelected(true);

		ButtonGroup grupoDificuldade = new ButtonGroup();
		grupoDificuldade.add(rbFacil);
		grupoDificuldade.add(rbMedio);
		grupoDificuldade.add(rbDificil);

		for (JRadioButton rb : new JRadioButton[]{rbFacil, rbMedio, rbDificil}) {
			rb.setBackground(new Color(40, 40, 40));
			rb.setForeground(Color.WHITE);
		}

		JPanel panelDificuldade = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelDificuldade.setBackground(new Color(40, 40, 40));
		panelDificuldade.add(lblDificuldade);
		panelDificuldade.add(rbFacil);
		panelDificuldade.add(rbMedio);
		panelDificuldade.add(rbDificil);

		// VIDAS
		JLabel lblVidas = new JLabel("Vidas Iniciais:");
		lblVidas.setForeground(Color.WHITE);
		JSpinner spinVidas = new JSpinner(new SpinnerNumberModel(3, 1, 9, 1));

		JPanel panelVidas = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelVidas.setBackground(new Color(40, 40, 40));
		panelVidas.add(lblVidas);
		panelVidas.add(spinVidas);

		// TIPO DE TANQUE
		JLabel lblTanque = new JLabel("Tipo de Tanque:");
		lblTanque.setForeground(Color.WHITE);
		JComboBox<String> comboTanque = new JComboBox<>(new String[]{"Ágil", "Balanceado", "Blindado"});

		JPanel panelTanque = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelTanque.setBackground(new Color(40, 40, 40));
		panelTanque.add(lblTanque);
		panelTanque.add(comboTanque);

		// SELEÇÃO DE MAPA
		JLabel lblMapa = new JLabel("Selecione o Mapa:", SwingConstants.CENTER);
		lblMapa.setForeground(Color.WHITE);

		btnMapAleatorio = new JButton("?");
		btnMap1 = new JButton("M1");
		btnMap2 = new JButton("M2");
		btnMap3 = new JButton("M3");

		for (JButton btn : new JButton[]{btnMapAleatorio, btnMap1, btnMap2, btnMap3}) {
			btn.setPreferredSize(new Dimension(50, 50));
			btn.setFont(new Font("Arial", Font.BOLD, 16));
		}

		btnMapAleatorio.addActionListener(e -> selecionarMapa("Aleatório", btnMapAleatorio));
		btnMap1.addActionListener(e -> selecionarMapa("Mapa1", btnMap1));
		btnMap2.addActionListener(e -> selecionarMapa("Mapa2", btnMap2));
		btnMap3.addActionListener(e -> selecionarMapa("Mapa3", btnMap3));

		selecionarMapa("Aleatório", btnMapAleatorio);

		JPanel panelMapas = new JPanel(new GridLayout(1, 4, 10, 0));
		panelMapas.setBackground(new Color(40, 40, 40));
		panelMapas.add(btnMapAleatorio);
		panelMapas.add(btnMap1);
		panelMapas.add(btnMap2);
		panelMapas.add(btnMap3);

		// BOTÕES DE AÇÃO
		JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		panelBotoes.setBackground(new Color(40, 40, 40));

		JButton btnConfirmar = new JButton("JOGAR");
		btnConfirmar.setBackground(new Color(34, 139, 34));
		btnConfirmar.setForeground(Color.WHITE);
		btnConfirmar.setFont(new Font("Arial", Font.BOLD, 16));

		JButton btnVoltar = new JButton("Voltar");
		estilizarBotao(btnVoltar);

		btnConfirmar.addActionListener(e -> {
			if (campoNome.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Digite seu nome!", "Erro", JOptionPane.ERROR_MESSAGE);
				return;
			}

			nomeJogador = campoNome.getText().trim();
			dificuldade = rbFacil.isSelected() ? "Fácil" : rbDificil.isSelected() ? "Difícil" : "Médio";
			vidas = (int) spinVidas.getValue();
			tipoTanque = (String) comboTanque.getSelectedItem();

			// Configurar objeto config
			config.setNomeJogador(nomeJogador);
			config.setDificuldade(dificuldade);
			config.setTipoTanque(tipoTanque);
			config.setVidas(vidas);
			config.setMapaSelecionado(mapaSelecionado);
			config.setJogoEmAndamento(true);

			// Iniciar jogo
			iniciarJogo();
		});

		btnVoltar.addActionListener(e -> layout.show(container, "MENU"));

		panelBotoes.add(btnConfirmar);
		panelBotoes.add(btnVoltar);

		// ASSEMBLAGEM FINAL
		panel.add(Box.createVerticalStrut(20));
		panel.add(titulo);
		panel.add(Box.createVerticalStrut(20));
		panel.add(panelNome);
		panel.add(Box.createVerticalStrut(10));
		panel.add(panelDificuldade);
		panel.add(Box.createVerticalStrut(10));
		panel.add(panelVidas);
		panel.add(Box.createVerticalStrut(10));
		panel.add(panelTanque);
		panel.add(Box.createVerticalStrut(20));
		panel.add(lblMapa);
		panel.add(panelMapas);
		panel.add(Box.createVerticalStrut(30));
		panel.add(panelBotoes);
		panel.add(Box.createVerticalGlue());

		return panel;
	}

	private void selecionarMapa(String mapa, JButton botao) {
		mapaSelecionado = mapa;
		for (JButton btn : new JButton[]{btnMapAleatorio, btnMap1, btnMap2, btnMap3}) {
			btn.setBackground(UIManager.getColor("Button.background"));
			btn.setForeground(Color.BLACK);
		}
		botao.setBackground(new Color(100, 149, 237));
		botao.setForeground(Color.WHITE);
	}

	// ==================== TELA RANKING ====================
	private JPanel criarTelaRanking() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(40, 40, 40));

		JLabel titulo = new JLabel("TOP 10 RANKING", SwingConstants.CENTER);
		titulo.setFont(new Font("Arial", Font.BOLD, 28));
		titulo.setForeground(new Color(255, 215, 0));
		titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

		JTextArea areaRanking = new JTextArea();
		areaRanking.setEditable(false);
		areaRanking.setBackground(new Color(50, 50, 50));
		areaRanking.setForeground(Color.WHITE);
		areaRanking.setFont(new Font("Monospaced", Font.PLAIN, 14));
		areaRanking.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Carregar ranking
		StringBuilder sb = new StringBuilder();
		sb.append("POSIÇÃO | JOGADOR          | PONTOS | FASE\n");
		sb.append("=========================================\n");
		List<Ranking.Jogador> jogadores = ranking.getJogadores();
		if (jogadores.isEmpty()) {
			sb.append("  -    |   Nenhum          | 0    |  -\n");
		} else {
			for (int i = 0; i < jogadores.size(); i++) {
				Ranking.Jogador j = jogadores.get(i);
				sb.append(String.format("%3d    | %-16s | %5d | %3d\n", i + 1, j.nome, j.pontuacao, j.fase));
			}
		}

		areaRanking.setText(sb.toString());

		JScrollPane scrollPane = new JScrollPane(areaRanking);
		scrollPane.getViewport().setBackground(new Color(50, 50, 50));

		JButton btnVoltar = new JButton("Voltar");
		estilizarBotao(btnVoltar);
		btnVoltar.addActionListener(e -> layout.show(container, "MENU"));

		JPanel panelBotao = new JPanel();
		panelBotao.setBackground(new Color(40, 40, 40));
		panelBotao.add(btnVoltar);

		panel.add(titulo, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(panelBotao, BorderLayout.SOUTH);

		return panel;
	}

	// ==================== MÉTODOS AUXILIARES ====================
	private void estilizarBotao(JButton btn) {
		btn.setBackground(new Color(70, 130, 180));
		btn.setForeground(Color.WHITE);
		btn.setFont(new Font("Arial", Font.BOLD, 14));
		btn.setFocusPainted(false);
	}

	private void iniciarJogo() {
		// Remover painel de jogo anterior se existir
		for (Component c : container.getComponents()) {
			if (c instanceof PainelJogoV2) {
				container.remove(c);
			}
		}

		// Criar novo painel de jogo
		PainelJogoV2 painelJogo = new PainelJogoV2(config);
		container.add(painelJogo, "JOGO");
		container.revalidate();
		container.repaint();
		layout.show(container, "JOGO");
		painelJogo.requestFocusInWindow();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(TelasJogoNova::new);
	}
}
