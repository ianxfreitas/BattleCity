package objeto;

/**
 * Interface que define o comportamento de qualquer entidade que se move no jogo.
 * O professor pediu para descrever "o que faz", não "o que é".
 */
public interface Movimentavel {
    void mover();
    void parar();
    boolean isEmMovimento();
}
