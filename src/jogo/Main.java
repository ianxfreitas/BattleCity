package jogo;

import telas.TelasJogo;

/**
 * Classe principal que inicia a aplicação Battle City.
 */
public class Main {
	public static void main(String[] args) {
		// Executar a interface gráfica no Event Dispatch Thread
		javax.swing.SwingUtilities.invokeLater(() -> {
			TelasJogo frame = new TelasJogo();
			frame.setVisible(true);
		});
	}
}
