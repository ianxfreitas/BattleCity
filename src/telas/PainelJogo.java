package telas;

import mundo.*;
import objeto.*;
import controles.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

public class PainelJogo extends JPanel implements ActionListener {

    private Mapa mapa;
    private TanqueJogador jogador;
    private java.util.List<Tiro> tiros = new ArrayList<>();
    private java.util.List<TanqueInimigo> inimigos = new ArrayList<>();
    private ControleTeclado controle;
    private Timer timer;

    public PainelJogo() {
        this(new Mapa1());
    }

    // construtor que recebe um mapa específico
    public PainelJogo(Mapa mapa) {
        setBackground(Color.BLACK);

        this.mapa = mapa;

        setPreferredSize(new Dimension(
                Mapa.COLUNAS * Mapa.TAMANHO,
                Mapa.LINHAS * Mapa.TAMANHO
        ));

        // Criar tanque do jogador um pouco menor que o tile para evitar ficar preso nas paredes
        int tamanhoTanque = Math.max(8, Mapa.TAMANHO * 3 / 4); // por exemplo 12 se TAMANHO=16
        // Posicionar o tanque no centro da célula central do mapa
        int centerCol = Mapa.COLUNAS / 2;
        int centerRow = Mapa.LINHAS / 2;
        int cellX = centerCol * Mapa.TAMANHO;
        int cellY = centerRow * Mapa.TAMANHO;
        int startX = cellX + (Mapa.TAMANHO - tamanhoTanque) / 2;
        int startY = cellY + (Mapa.TAMANHO - tamanhoTanque) / 2;
        jogador = new TanqueJogador(startX, startY, tamanhoTanque);

        // Spawna alguns inimigos em posições fixas (cantos ou células seguras)
        int enemySize = tamanhoTanque; // mesmo tamanho do jogador
        // exemplo: canto superior esquerdo, canto superior direito, inferior esquerdo
        inimigos.add(new TanqueInimigo(Mapa.TAMANHO + (Mapa.TAMANHO - enemySize)/2, Mapa.TAMANHO + (Mapa.TAMANHO - enemySize)/2, enemySize));
        inimigos.add(new TanqueInimigo((Mapa.COLUNAS-2)*Mapa.TAMANHO + (Mapa.TAMANHO - enemySize)/2, Mapa.TAMANHO + (Mapa.TAMANHO - enemySize)/2, enemySize));
        inimigos.add(new TanqueInimigo(Mapa.TAMANHO + (Mapa.TAMANHO - enemySize)/2, (Mapa.LINHAS-2)*Mapa.TAMANHO + (Mapa.TAMANHO - enemySize)/2, enemySize));

        // Controle de teclado
        controle = new ControleTeclado(jogador);
        controle.setOnAtiraListener(tiro -> {
            tiros.add(tiro);
        });
        addKeyListener(controle);

        setFocusable(true);
        requestFocusInWindow();

        // Timer simples para atualizar o jogo ~60 FPS (16ms)
        timer = new Timer(16, this);
        timer.start();
    }

    // Permite trocar o mapa em tempo de execução
    public void setMapa(Mapa mapa) {
        this.mapa = mapa;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapa != null) {
            mapa.desenhar(g);
        }

        // desenha o jogador
        if (jogador != null) jogador.desenhar(g);

        // desenha inimigos
        for (TanqueInimigo enemy : inimigos) {
            enemy.desenhar(g);
        }

        // desenha tiros
        for (Tiro t : tiros) {
            t.desenhar(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // mover/jogar
        if (jogador != null) {
            // aplica movimento com checagem de colisão
            jogador.moveIfPossible(mapa);
            jogador.atualizar();
        }

        // atualizar tiros e remover inativos (ou fora dos limites)
        Iterator<Tiro> it = tiros.iterator();
        int panelW = getWidth();
        int panelH = getHeight();
        while (it.hasNext()) {
            Tiro t = it.next();
            t.atualizar();

            boolean remover = false;

            // checa se o tiro saiu da área visível
            if (!t.isAtivo() || t.getX() + t.getLargura() < 0 || t.getY() + t.getAltura() < 0
                    || t.getX() > panelW || t.getY() > panelH) {
                remover = true;
            }

            // colisão com parede do mapa (célula 1)
            int col = t.getX() / Mapa.TAMANHO;
            int row = t.getY() / Mapa.TAMANHO;
            if (!remover && row >= 0 && row < Mapa.LINHAS && col >= 0 && col < Mapa.COLUNAS) {
                if (mapa.getMatriz()[row][col] == 1) {
                    remover = true;
                }
            }

            // colisão com tanques
            if (!remover) {
                // se foi tiro do jogador, atinge inimigos
                if (t.getShooter() == Tiro.Shooter.JOGADOR) {
                    Iterator<TanqueInimigo> itEn = inimigos.iterator();
                    while (itEn.hasNext()) {
                        TanqueInimigo enemy = itEn.next();
                        if (enemy.estaVivo() && enemy.getBounds().intersects(t.getBounds())) {
                            enemy.tomarDano(1);
                            remover = true;
                            if (!enemy.estaVivo()) itEn.remove();
                            break;
                        }
                    }
                } else if (t.getShooter() == Tiro.Shooter.INIMIGO) {
                    // tiro inimigo atinge jogador
                    if (jogador != null && jogador.estaVivo() && jogador.getBounds().intersects(t.getBounds())) {
                        jogador.tomarDano(1);
                        remover = true;
                        // opcional: tratar morte do jogador
                    }
                }
            }

            if (remover) it.remove();
        }

        // atualizar inimigos (movimento e atirar)
        for (TanqueInimigo enemy : inimigos) {
            enemy.atualizar();
            enemy.moveIfPossible(mapa);
            if (enemy.wantsToShoot()) {
                tiros.add(enemy.atirar());
                enemy.resetShootCooldown();
            }
        }

        repaint();
    }
}
