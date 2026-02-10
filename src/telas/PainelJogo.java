package telas;

import javax.swing.*;
import java.awt.*;

public class PainelJogo extends JPanel {

    public PainelJogo() {
        setBackground(Color.BLACK); // Fundo preto
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Aqui desenhamos o tanque (provisório)
        g.setColor(Color.GREEN);
        g.fillRect(100, 100, 40, 40);

        g.setColor(Color.WHITE);
        g.drawString("JOGO RODANDO...", 200, 200);
    }
}