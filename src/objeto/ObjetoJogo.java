package objeto;

import java.awt.*;

public abstract class ObjetoJogo {

	protected int x;
	protected int y;
	protected int largura;
	protected int altura;

	public ObjetoJogo(int x, int y, int largura, int altura) {
		this.x = x;
		this.y = y;
		this.largura = largura;
		this.altura = altura;
	}

	// Atualiza o estado do objeto (posição, lógica)
	public abstract void atualizar();

	// Desenha o objeto na tela
	public abstract void desenhar(Graphics g);

	public Rectangle getBounds() {
		return new Rectangle(x, y, largura, altura);
	}

	// Getters básicos
	public int getX() { return x; }
	public int getY() { return y; }
	public int getLargura() { return largura; }
	public int getAltura() { return altura; }
}
