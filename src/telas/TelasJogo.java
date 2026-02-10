package telas;

import javax.swing.*;
import java.awt.*;

public class TelasJogo extends JFrame {

    private CardLayout layout;
    private JPanel container;

    // dados do jogador
    private String nomeJogador;
    private String dificuldade;

    public TelasJogo() {
        setTitle("Battle City");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        layout = new CardLayout();
        container = new JPanel(layout);

        container.add(menuInicial(), "MENU");
        container.add(telaConfiguracao(), "CONFIG");
        container.add(telaRanking(), "RANKING");
        container.add(new PainelJogo(), "JOGO");

        add(container);
        layout.show(container, "MENU");

        setVisible(true);
    }

    /* =======================
       1) MENU INICIAL
       ======================= */
    private JPanel menuInicial() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        JLabel titulo = new JLabel("BATTLE CITY", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 26));

        JButton jogar = new JButton("Jogar");
        JButton ranking = new JButton("Ranking");
        JButton sair = new JButton("Sair");

        jogar.addActionListener(e -> layout.show(container, "CONFIG"));
        ranking.addActionListener(e -> layout.show(container, "RANKING"));
        sair.addActionListener(e -> System.exit(0));

        panel.add(titulo);
        panel.add(jogar);
        panel.add(ranking);
        panel.add(sair);

        return panel;
    }

    /* =======================
       2) TELA CONFIGURAÇÃO
       ======================= */
    private JPanel telaConfiguracao() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        JLabel lblNome = new JLabel("Nome do Jogador:");
        JTextField campoNome = new JTextField();

        JLabel lblDificuldade = new JLabel("Dificuldade:");

        JRadioButton facil = new JRadioButton("Fácil");
        JRadioButton medio = new JRadioButton("Médio");
        JRadioButton dificil = new JRadioButton("Difícil");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(facil);
        grupo.add(medio);
        grupo.add(dificil);
        facil.setSelected(true);

        JButton confirmar = new JButton("Confirmar");
        JButton voltar = new JButton("Voltar");

        confirmar.addActionListener(e -> {
            if (campoNome.getText().isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Digite o nome do jogador!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            nomeJogador = campoNome.getText();
            dificuldade = facil.isSelected() ? "Fácil" :
                    medio.isSelected() ? "Médio" : "Difícil";

            JOptionPane.showMessageDialog(this,
                    "Jogador: " + nomeJogador +
                            "\nDificuldade: " + dificuldade);

            layout.show(container, "JOGO");

            container.getComponent(3).requestFocus();

        });

        voltar.addActionListener(e -> layout.show(container, "MENU"));

        panel.add(lblNome);
        panel.add(campoNome);
        panel.add(lblDificuldade);
        panel.add(facil);
        panel.add(medio);
        panel.add(dificil);
        panel.add(confirmar);
        panel.add(voltar);

        return panel;
    }

    /* =======================
       3) TELA RANKING
       ======================= */
    private JPanel telaRanking() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("RANKING", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        JTextArea ranking = new JTextArea();
        ranking.setEditable(false);
        ranking.setText(
                "1. Ana - 5000\n" + "2. João - 4200\n" +
                        "3. Maria - 3900\n"
        );

        JButton voltar = new JButton("Voltar");
        voltar.addActionListener(e -> layout.show(container, "MENU"));

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(ranking), BorderLayout.CENTER);
        panel.add(voltar, BorderLayout.SOUTH);

        return panel;
    }
    public static void main(String[] args) {
        new TelasJogo();
    }
}