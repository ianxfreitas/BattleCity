package controles;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import objeto.TanqueJogador;
import objeto.Tiro;

/**
 * Gerencia os controles de teclado do jogador.
 * (setas/WASD) e tiro (espaço).
 * Modificado para travar o movimento em apenas 4 direções (sem diagonal).
 */
public class ControleTeclado extends KeyAdapter {

	private TanqueJogador jogador;
	private OnAtiraListener listener;

	public ControleTeclado(TanqueJogador jogador) {
		this.jogador = jogador;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				jogador.setMovCima(true);
				jogador.setMovBaixo(false);
				jogador.setMovEsquerda(false);
				jogador.setMovDireita(false);
				e.consume();
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				jogador.setMovBaixo(true);
				jogador.setMovCima(false);
				jogador.setMovEsquerda(false);
				jogador.setMovDireita(false);
				e.consume();
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				jogador.setMovEsquerda(true);
				jogador.setMovDireita(false);
				jogador.setMovCima(false);
				jogador.setMovBaixo(false);
				e.consume();
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				jogador.setMovDireita(true);
				jogador.setMovEsquerda(false);
				jogador.setMovCima(false);
				jogador.setMovBaixo(false);
				e.consume();
				break;
			case KeyEvent.VK_SPACE:
				Tiro tiro = jogador.atirar();
				if (listener != null && tiro != null) {
					listener.onAtira(tiro);
				}
				e.consume();
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Quando solta a tecla, apenas desliga a direção correspondente
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				jogador.setMovCima(false);
				e.consume();
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				jogador.setMovBaixo(false);
				e.consume();
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				jogador.setMovEsquerda(false);
				e.consume();
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				jogador.setMovDireita(false);
				e.consume();
				break;
		}
	}

	public void setOnAtiraListener(OnAtiraListener listener) {
		this.listener = listener;
	}

	public interface OnAtiraListener {
		void onAtira(Tiro tiro);
	}
}