package telas;

import mundo.*;
import javax.swing.*;
import java.awt.*;

public class PainelJogo extends JPanel {

    private Mapa mapa;

    public PainelJogo() {
        setBackground(Color.BLACK);

        mapa = new Mapa1();

        setPreferredSize(new Dimension(
                Mapa.COLUNAS * Mapa.TAMANHO,
                Mapa.LINHAS * Mapa.TAMANHO
        ));

        setFocusable(true);
        setRequestFocusEnabled(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        mapa.desenhar(g);


        g.setColor(Color.GREEN);
        g.fillRect(100, 100, 40, 40);
    }
}
