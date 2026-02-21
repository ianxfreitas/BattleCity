package telas;

import jogo.Configuracoes;
import jogo.Ranking;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de Game Over exibida quando o jogador perde todas as vidas.
 * Permite voltar ao lobby ou jogar novamente.
 */
public class TelaGameOver extends JPanel {

	private Configuracoes config;
	private Ranking ranking;
	private GameOverListener listener;

	private int pontuacaoFinal;
	private int faseFinal;

	/**
	 * Interface de callback para lidar com ações do Game Over.
	 */
	public interface GameOverListener {
		void onVoltarAoLobby();
		void onJogarNovamente();
	}

	public TelaGameOver(Configuracoes config, Ranking ranking, GameOverListener listener) {
		this.config = config;
		this.ranking = ranking;
		this.listener = listener;
		this.pontuacaoFinal = config.getPontuacao();
		this.faseFinal = config.getFase();

		inicializarUI();
	}

	private void inicializarUI() {
		setLayout(new GridBagLayout());
		setBackground(new Color(20, 20, 20));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 15, 10, 15);
		gbc.gridwidth = GridBagConstraints.REMAINDER;

		// Título "GAME OVER"
		JLabel lblGameOver = new JLabel("GAME OVER");
		lblGameOver.setFont(new Font("Arial", Font.BOLD, 72));
		lblGameOver.setForeground(new Color(255, 50, 50));
		lblGameOver.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridy = 0;
		add(lblGameOver, gbc);

		// Espaço vazio
		gbc.gridy = 1;
		add(Box.createVerticalStrut(40), gbc);

		// Painel de informações
		JPanel panelInfo = criarPainelInformacoes();
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		add(panelInfo, gbc);

		// Espaço vazio
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		add(Box.createVerticalStrut(40), gbc);

		// Painel de botões
		JPanel panelBotoes = criarPainelBotoes();
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(panelBotoes, gbc);

		// Espaço vazio no final
		gbc.gridy = 5;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.VERTICAL;
		add(Box.createVerticalGlue(), gbc);
	}

	private JPanel criarPainelInformacoes() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(new Color(40, 40, 40));
		panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

		// Nome do jogador
		JLabel lblNome = new JLabel("Jogador: " + config.getNomeJogador());
		lblNome.setFont(new Font("Arial", Font.PLAIN, 18));
		lblNome.setForeground(Color.WHITE);
		lblNome.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblNome);

		panel.add(Box.createVerticalStrut(15));

		// Pontuação final
		JLabel lblPontuacao = new JLabel("Pontuação Final: " + pontuacaoFinal);
		lblPontuacao.setFont(new Font("Arial", Font.BOLD, 20));
		lblPontuacao.setForeground(new Color(100, 200, 100));
		lblPontuacao.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblPontuacao);

		panel.add(Box.createVerticalStrut(15));

		// Fase atingida
		JLabel lblFase = new JLabel("Fase Atingida: " + faseFinal);
		lblFase.setFont(new Font("Arial", Font.PLAIN, 18));
		lblFase.setForeground(Color.WHITE);
		lblFase.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblFase);

		panel.add(Box.createVerticalStrut(15));

		// Verificar se qualifica para ranking
		int rankDo = ranking.getRank(config.getNomeJogador());
		JLabel lblRanking = new JLabel();
		if (rankDo > 0 && rankDo <= 10) {
			lblRanking.setText("🏆 Você está no TOP 10! Posição: #" + rankDo);
			lblRanking.setForeground(new Color(255, 215, 0));
		} else if (ranking.qualificaParaRanking(pontuacaoFinal)) {
			lblRanking.setText("⭐ Nova pontuação de TOP 10!");
			lblRanking.setForeground(new Color(255, 215, 0));
		} else {
			lblRanking.setText("Melhor sorte na próxima!");
			lblRanking.setForeground(new Color(200, 200, 200));
		}
		lblRanking.setFont(new Font("Arial", Font.PLAIN, 16));
		lblRanking.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblRanking);

		return panel;
	}

	private JPanel criarPainelBotoes() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
		panel.setBackground(new Color(20, 20, 20));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

		// Botão "Jogar Novamente"
		JButton btnJogarNovamente = new JButton("JOGAR NOVAMENTE");
		btnJogarNovamente.setBackground(new Color(34, 139, 34));
		btnJogarNovamente.setForeground(Color.WHITE);
		btnJogarNovamente.setFont(new Font("Arial", Font.BOLD, 16));
		btnJogarNovamente.setFocusPainted(false);
		btnJogarNovamente.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnJogarNovamente.addActionListener(e -> {
			// Salvar score no ranking se qualificar
			if (ranking.qualificaParaRanking(pontuacaoFinal)) {
				ranking.adicionarJogador(config.getNomeJogador(), pontuacaoFinal, faseFinal);
			}
			// Reset configurações para nova partida
			config.resetarParaNovaPartida();
			if (listener != null) {
				listener.onJogarNovamente();
			}
		});
		panel.add(btnJogarNovamente);

		// Botão "Voltar ao Lobby"
		JButton btnVoltarLobby = new JButton("VOLTAR AO LOBBY");
		btnVoltarLobby.setBackground(new Color(70, 130, 180));
		btnVoltarLobby.setForeground(Color.WHITE);
		btnVoltarLobby.setFont(new Font("Arial", Font.BOLD, 16));
		btnVoltarLobby.setFocusPainted(false);
		btnVoltarLobby.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnVoltarLobby.addActionListener(e -> {
			// Salvar score no ranking se qualificar
			if (ranking.qualificaParaRanking(pontuacaoFinal)) {
				ranking.adicionarJogador(config.getNomeJogador(), pontuacaoFinal, faseFinal);
			}
			// Reset configurações
			config.resetarParaNovaPartida();
			if (listener != null) {
				listener.onVoltarAoLobby();
			}
		});
		panel.add(btnVoltarLobby);

		return panel;
	}
}
