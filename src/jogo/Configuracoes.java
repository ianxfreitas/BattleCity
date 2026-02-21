package jogo;

/**
 * Classe que armazena as configurações do jogo e dados do jogador.
 * Responsável por manter informações entre telas.
 */
public class Configuracoes {

	// Dados do jogador
	private String nomeJogador;
	private int vidas;
	private int pontuacao;
	private int fase;
	private String dificuldade; // "Fácil", "Médio", "Difícil"
	private String tipoTanque; // "Ágil", "Balanceado", "Blindado"
	private String mapaSelecionado; // "Mapa1", "Mapa2", "Mapa3", "Aleatório"
	private boolean jogoEmAndamento;

	public Configuracoes() {
		this.nomeJogador = "Jogador";
		this.vidas = 3;
		this.pontuacao = 0;
		this.fase = 1;
		this.dificuldade = "Médio";
		this.tipoTanque = "Balanceado";
		this.mapaSelecionado = "Aleatório";
		this.jogoEmAndamento = false;
	}

	// Getters e Setters
	public String getNomeJogador() { return nomeJogador; }
	public void setNomeJogador(String nome) { this.nomeJogador = nome; }

	public int getVidas() { return vidas; }
	public void setVidas(int vidas) { this.vidas = vidas; }
	public void decrementarVida() { this.vidas--; }
	public void incrementarVida() { this.vidas++; }

	public int getPontuacao() { return pontuacao; }
	public void setPontuacao(int pontuacao) { this.pontuacao = pontuacao; }
	public void adicionarPontos(int pontos) { this.pontuacao += pontos; }

	public int getFase() { return fase; }
	public void setFase(int fase) { this.fase = fase; }
	public void avancarFase() { this.fase++; }

	public String getDificuldade() { return dificuldade; }
	public void setDificuldade(String dificuldade) { this.dificuldade = dificuldade; }

	public String getTipoTanque() { return tipoTanque; }
	public void setTipoTanque(String tipo) { this.tipoTanque = tipo; }

	public String getMapaSelecionado() { return mapaSelecionado; }
	public void setMapaSelecionado(String mapa) { this.mapaSelecionado = mapa; }

	public boolean isJogoEmAndamento() { return jogoEmAndamento; }
	public void setJogoEmAndamento(boolean ativo) { this.jogoEmAndamento = ativo; }

	// Método para resetar configurações para nova partida
	public void resetarParaNovaPartida() {
		this.vidas = 3;
		this.pontuacao = 0;
		this.fase = 1;
		this.jogoEmAndamento = false;
	}
}
