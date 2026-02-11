package telas;

import mundo.*;
import javax.swing.*;
import java.awt.*;

public class PainelJogo extends JPanel {

    private Mapa mapa;

    // Construtor padrão mantém compatibilidade e usa Mapa1
    public PainelJogo() {
        this(new Mapa1());
    }

    // Novo construtor que aceita um mapa específico
    public PainelJogo(Mapa mapa) {
        setBackground(Color.BLACK);

        this.mapa = mapa;

        setPreferredSize(new Dimension(
                Mapa.COLUNAS * Mapa.TAMANHO,
                Mapa.LINHAS * Mapa.TAMANHO
        ));

        setFocusable(true);
        setRequestFocusEnabled(true);
    }

    // Permite trocar o mapa em tempo de execução
    public void setMapa(Mapa mapa) {
        this.mapa = mapa;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Protege caso mapa seja nulo
        if (mapa != null) {
            mapa.desenhar(g);
        }

        // Exemplo de desenho adicional (pode remover/ajustar)
        g.setColor(Color.GREEN);
        g.fillRect(100, 100, 40, 40);
    }
}
