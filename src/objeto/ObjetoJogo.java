package objeto;

import java.awt.*;

/**
 * Classe abstrata que representa um objeto genérico no jogo.
 * Todos os objetos do jogo (tanques, tiros, power-ups) estendem esta classe.
 */
public abstract class ObjetoJogo implements Colidivel, Temporizado {

	protected int x;
	protected int y;
	protected int largura;
	protected int altura;
	protected int vida = 1; // vida padrão

	public ObjetoJogo(int x, int y, int largura, int altura) {
		this.x = x;
		this.y = y;
		this.largura = largura;
		this.altura = altura;
	}

	// Atualiza o estado do objeto (posição, lógica)
	@Override
	public abstract void atualizar();

	// Desenha o objeto na tela
	public abstract void desenhar(Graphics g);

	@Override
	public boolean colideCom(Colidivel outro) {
		return getBounds().intersects(outro.getBounds());
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, largura, altura);
	}

	// Métodos relacionados a vida
	public void tomarDano(int dano) {
		this.vida -= dano;
	}

	public boolean estaVivo() {
		return vida > 0;
	}

	@Override
	public boolean isAtivo() {
		return estaVivo();
	}

	public int getVida() { return vida; }
	public void setVida(int vida) { this.vida = vida; }

	// Getters básicos
	public int getX() { return x; }
	public int getY() { return y; }
	public int getLargura() { return largura; }
	public int getAltura() { return altura; }

	// Setters para posição
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
}
