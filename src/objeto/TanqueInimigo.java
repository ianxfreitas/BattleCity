package objeto;

import java.awt.*;
import java.util.Random;
import mundo.Mapa;

public class TanqueInimigo extends ObjetoJogo {

    public enum Direcao { CIMA, BAIXO, ESQUERDA, DIREITA }

    private Direcao direcao = Direcao.BAIXO;
    private int velocidade = 1; // inimigos podem ser mais lentos

    private Random rnd = new Random();

    // timers em ticks (cada tick é ~16ms)
    private int changeDirCooldown = 0;
    private int shootCooldown = 0;
    private boolean wantsToShoot = false;
    private int vida = 1; // inimigo simples com 1 de vida

    public TanqueInimigo(int x, int y, int tamanho) {
        super(x, y, tamanho, tamanho);
        this.direcao = Direcao.BAIXO;
        this.changeDirCooldown = rnd.nextInt(60) + 30;
        this.shootCooldown = rnd.nextInt(120) + 60;
    }

    @Override
    public void atualizar() {
        // decrementa contadores
        if (changeDirCooldown > 0) changeDirCooldown--;
        if (shootCooldown > 0) shootCooldown--;

        if (changeDirCooldown <= 0) {
            // escolhe nova direção aleatória
            int v = rnd.nextInt(4);
            switch (v) {
                case 0: direcao = Direcao.CIMA; break;
                case 1: direcao = Direcao.BAIXO; break;
                case 2: direcao = Direcao.ESQUERDA; break;
                default: direcao = Direcao.DIREITA; break;
            }
            changeDirCooldown = rnd.nextInt(80) + 20;
        }

        if (shootCooldown <= 0) {
            wantsToShoot = true;
        }
    }

    @Override
    public void desenhar(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, largura, altura);
    }

    // Move na direção atual, com checagem de colisão similar ao jogador
    public void moveIfPossible(Mapa mapa) {
        int dx = 0, dy = 0;
        switch (direcao) {
            case CIMA: dy = -velocidade; break;
            case BAIXO: dy = velocidade; break;
            case ESQUERDA: dx = -velocidade; break;
            case DIREITA: dx = velocidade; break;
        }

        if (dx == 0 && dy == 0) return;

        int mapaW = Mapa.COLUNAS * Mapa.TAMANHO;
        int mapaH = Mapa.LINHAS * Mapa.TAMANHO;

        int[][] mat = mapa.getMatriz();

        // Move X separadamente
        if (dx != 0) {
            int novoX = x + dx;
            if (novoX >= 0 && novoX + largura <= mapaW) {
                int leftCol = novoX / Mapa.TAMANHO;
                int rightCol = (novoX + largura - 1) / Mapa.TAMANHO;
                int topRow = y / Mapa.TAMANHO;
                int bottomRow = (y + altura - 1) / Mapa.TAMANHO;

                leftCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, leftCol));
                rightCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, rightCol));
                topRow = Math.max(0, Math.min(Mapa.LINHAS - 1, topRow));
                bottomRow = Math.max(0, Math.min(Mapa.LINHAS - 1, bottomRow));

                boolean colisao = false;
                for (int r = topRow; r <= bottomRow && !colisao; r++) {
                    for (int c = leftCol; c <= rightCol; c++) {
                        if (mat[r][c] == 1) { colisao = true; break; }
                    }
                }

                if (!colisao) {
                    x = novoX;
                } else {
                    // se colidir, muda de direção para tentar escapar
                    changeDirCooldown = 0;
                }
            }
        }

        // Move Y separadamente
        if (dy != 0) {
            int novoY = y + dy;
            if (novoY >= 0 && novoY + altura <= mapaH) {
                int leftCol = x / Mapa.TAMANHO;
                int rightCol = (x + largura - 1) / Mapa.TAMANHO;
                int topRow = novoY / Mapa.TAMANHO;
                int bottomRow = (novoY + altura - 1) / Mapa.TAMANHO;

                leftCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, leftCol));
                rightCol = Math.max(0, Math.min(Mapa.COLUNAS - 1, rightCol));
                topRow = Math.max(0, Math.min(Mapa.LINHAS - 1, topRow));
                bottomRow = Math.max(0, Math.min(Mapa.LINHAS - 1, bottomRow));

                boolean colisao = false;
                for (int r = topRow; r <= bottomRow && !colisao; r++) {
                    for (int c = leftCol; c <= rightCol; c++) {
                        if (mat[r][c] == 1) { colisao = true; break; }
                    }
                }

                if (!colisao) {
                    y = novoY;
                } else {
                    changeDirCooldown = 0;
                }
            }
        }
    }

    public Tiro atirar() {
        int tiroSize = Math.max(4, largura/4);
        int dx = 0, dy = 0;
        int tx = x + largura/2 - tiroSize/2;
        int ty = y + altura/2 - tiroSize/2;
        int speed = 5;
        switch (direcao) {
            case CIMA: dy = -speed; break;
            case BAIXO: dy = speed; break;
            case ESQUERDA: dx = -speed; break;
            case DIREITA: dx = speed; break;
        }
        return new Tiro(tx, ty, tiroSize, tiroSize, dx, dy, Tiro.Shooter.INIMIGO);
    }

    public boolean wantsToShoot() {
        return wantsToShoot;
    }

    public void resetShootCooldown() {
        wantsToShoot = false;
        shootCooldown = rnd.nextInt(120) + 60;
    }

    public void tomarDano(int d) {
        vida -= d;
        if (vida < 0) vida = 0;
    }

    public boolean estaVivo() { return vida > 0; }
}
