package jogo;

import telas.TelasJogo;

public class Main {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> {
			TelasJogo frame = new TelasJogo();
			frame.setVisible(true);
		});
	}
}
