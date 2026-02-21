package telas;

import jogo.*;

import javax.swing.*;

/**
 * DEPRECATED - Use TelasJogo.java ao invés
 */
class TelasJogo_Temp extends JFrame {
	
	public TelasJogo_Temp() {
		// Inicializar como TelasJogo_new
		TelasJogo_New telaReal = new TelasJogo_New();
		// Transferir propriedades se necessário
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(TelasJogo_New::new);
	}
}
