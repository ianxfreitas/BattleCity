package controles;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import objeto.TanqueJogador;
import objeto.Tiro;

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
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				jogador.setMovBaixo(true);
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				jogador.setMovEsquerda(true);
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				jogador.setMovDireita(true);
				break;
			case KeyEvent.VK_SPACE:
				if (listener != null) listener.onAtira(jogador.atirar());
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				jogador.setMovCima(false);
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				jogador.setMovBaixo(false);
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				jogador.setMovEsquerda(false);
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				jogador.setMovDireita(false);
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
