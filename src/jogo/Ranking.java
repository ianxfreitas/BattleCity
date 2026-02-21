package jogo;

import java.io.*;
import java.util.*;

/**
 * Gerencia o ranking de jogadores com persistência em arquivo.
 * Carrega e salva os 10 melhores scores em um arquivo de texto.
 */
public class Ranking {

	private static final String ARQUIVO_RANKING = "ranking.txt";
	private static final int MAX_JOGADORES = 10;

	private List<Jogador> jogadores;

	public static class Jogador implements Comparable<Jogador> {
		public String nome;
		public int pontuacao;
		public int fase;
		public long timestamp;

		public Jogador(String nome, int pontuacao, int fase) {
			this.nome = nome;
			this.pontuacao = pontuacao;
			this.fase = fase;
			this.timestamp = System.currentTimeMillis();
		}

		@Override
		public int compareTo(Jogador outro) {
			// Ordena por pontuação (maior primeiro)
			if (this.pontuacao != outro.pontuacao) {
				return Integer.compare(outro.pontuacao, this.pontuacao);
			}
			// Se pontuação igual, ordena por fase (maior primeiro)
			return Integer.compare(outro.fase, this.fase);
		}

		@Override
		public String toString() {
			return nome + "," + pontuacao + "," + fase + "," + timestamp;
		}
	}

	public Ranking() {
		jogadores = new ArrayList<>();
		carregar();
	}

	/**
	 * Carrega o ranking do arquivo, ou cria um novo se não existir.
	 */
	public void carregar() {
		jogadores.clear();
		File file = new File(ARQUIVO_RANKING);

		if (!file.exists()) {
			salvar(); // Criar arquivo vazio se não existir
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String linha;
			while ((linha = reader.readLine()) != null) {
				try {
					String[] partes = linha.split(",");
					if (partes.length >= 3) {
						String nome = partes[0];
						int pontuacao = Integer.parseInt(partes[1]);
						int fase = Integer.parseInt(partes[2]);
						jogadores.add(new Jogador(nome, pontuacao, fase));
					}
				} catch (NumberFormatException e) {
					System.err.println("Erro ao parsing linha de ranking: " + linha);
				}
			}
			Collections.sort(jogadores);
		} catch (IOException e) {
			System.err.println("Erro ao carregar ranking: " + e.getMessage());
		}
	}

	/**
	 * Salva o ranking no arquivo.
	 */
	public void salvar() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_RANKING))) {
			for (Jogador j : jogadores) {
				writer.println(j.toString());
			}
		} catch (IOException e) {
			System.err.println("Erro ao salvar ranking: " + e.getMessage());
		}
	}

	/**
	 * Verifica se a pontuação qualifica para o ranking.
	 */
	public boolean qualificaParaRanking(int pontuacao) {
		if (jogadores.size() < MAX_JOGADORES) {
			return true;
		}
		return pontuacao > jogadores.get(jogadores.size() - 1).pontuacao;
	}

	/**
	 * Adiciona um novo jogador ao ranking (se qualificar).
	 */
	public void adicionarJogador(String nome, int pontuacao, int fase) {
		if (!qualificaParaRanking(pontuacao)) {
			System.out.println("Pontuação não qualifica para o ranking.");
			return;
		}

		Jogador novo = new Jogador(nome, pontuacao, fase);
		jogadores.add(novo);
		Collections.sort(jogadores);

		// Manter apenas os 10 melhores
		if (jogadores.size() > MAX_JOGADORES) {
			jogadores = jogadores.subList(0, MAX_JOGADORES);
		}

		salvar();
	}

	/**
	 * Retorna a lista de jogadores no ranking.
	 */
	public List<Jogador> getJogadores() {
		return new ArrayList<>(jogadores);
	}

	/**
	 * Retorna o rank de um jogador (1-based), ou -1 se não estiver no ranking.
	 */
	public int getRank(String nome) {
		for (int i = 0; i < jogadores.size(); i++) {
			if (jogadores.get(i).nome.equals(nome)) {
				return i + 1;
			}
		}
		return -1;
	}

	/**
	 * Retorna o melhor score de um jogador, ou -1 se nunca jogou.
	 */
	public int getMelhorScore(String nome) {
		for (Jogador j : jogadores) {
			if (j.nome.equals(nome)) {
				return j.pontuacao;
			}
		}
		return -1;
	}

	/**
	 * Limpa todo o ranking (útil para testes).
	 */
	public void limpar() {
		jogadores.clear();
		salvar();
	}
}
