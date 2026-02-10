package telas;

import mundo.Mapa;

import javax.swing.*;
import java.awt.*;

public class PainelJogo extends JPanel {

    private Mapa mapa;

    public PainelJogo() {
        setBackground(Color.BLACK);
        mapa = new Mapa();

        // garante foco futuramente
        setFocusable(true);
        setRequestFocusEnabled(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // desenha o mapa 13x13
        mapa.desenhar(g);

        // tanque provisório (continua simples)
        g.setColor(Color.GREEN);
        g.fillRect(100, 100, 40, 40);


    }
}
