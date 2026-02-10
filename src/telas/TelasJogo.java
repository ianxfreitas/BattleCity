package telas;

import javax.swing.*;
import java.awt.*;

public class TelasJogo extends JFrame {

    private CardLayout layout;
    private JPanel container;

    // --- DADOS DO JOGO ---
    private String nomeJogador;
    private String dificuldade;
    private String mapaSelecionado = "Aleatório"; // Novo: guarda o mapa escolhido

    // Variáveis dos botões de mapa (para podermos mudar a cor deles depois)
    private JButton btnMapAleatorio, btnMap1, btnMap2, btnMap3;

    public TelasJogo() {
        setTitle("Battle City - Configuração");
        setSize(520, 550); // Aumentei um pouquinho a altura
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        layout = new CardLayout();
        container = new JPanel(layout);

        // Adicionando as telas
        container.add(menuInicial(), "MENU");
        container.add(telaConfiguracao(), "CONFIG");
        container.add(telaRanking(), "RANKING");
        container.add(new PainelJogo(), "JOGO"); // Mantive comentado caso você ainda não tenha a classe PainelJogo pronta

        // --- TELA DE JOGO PROVISÓRIA (apenas para teste, se não tiver o PainelJogo) ---
        JPanel jogoTeste = new JPanel();
        jogoTeste.setBackground(Color.BLACK);
        jogoTeste.add(new JLabel("JOGO INICIADO", JLabel.CENTER));
        container.add(jogoTeste, "JOGO");
        // --------------------------------------------------------------------------

        add(container);
        layout.show(container, "MENU");

        setVisible(true);
    }

    /* =======================
       1) MENU INICIAL
       ======================= */
    private JPanel menuInicial() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 120, 50, 120));

        JLabel titulo = new JLabel("BATTLE CITY", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));

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
       2) TELA CONFIGURAÇÃO (MODIFICADA)
       ======================= */
    private JPanel telaConfiguracao() {
        // Layout principal: 9 linhas para caber tudo organizado
        JPanel panel = new JPanel(new GridLayout(9, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        // --- NOME ---
        JLabel lblNome = new JLabel("Nome do Jogador:");
        JTextField campoNome = new JTextField();

        // --- DIFICULDADE (Agrupada em um painel horizontal) ---
        JLabel lblDificuldade = new JLabel("Dificuldade:");
        JPanel panelDificuldade = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JRadioButton facil = new JRadioButton("Fácil");
        JRadioButton medio = new JRadioButton("Médio");
        JRadioButton dificil = new JRadioButton("Difícil");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(facil); grupo.add(medio); grupo.add(dificil);
        facil.setSelected(true);

        panelDificuldade.add(facil);
        panelDificuldade.add(medio);
        panelDificuldade.add(dificil);

        // --- SELEÇÃO DE MAPA (NOVO) ---
        JLabel lblMapa = new JLabel("Selecione o Mapa:");

        // Painel para os 4 botões ficarem lado a lado
        JPanel panelMapas = new JPanel(new GridLayout(1, 4, 5, 0));

        btnMapAleatorio = new JButton("?");
        btnMap1 = new JButton("M1");
        btnMap2 = new JButton("M2");
        btnMap3 = new JButton("M3");

        // Dicas (aparece ao passar o mouse)
        btnMapAleatorio.setToolTipText("Aleatório");
        btnMap1.setToolTipText("Floresta");
        btnMap2.setToolTipText("Deserto");
        btnMap3.setToolTipText("Cidade");

        // Ações dos botões (Chama o método que pinta o botão de verde/azul)
        btnMapAleatorio.addActionListener(e -> selecionarBotaoMapa("Aleatório", btnMapAleatorio));
        btnMap1.addActionListener(e -> selecionarBotaoMapa("Mapa 1", btnMap1));
        btnMap2.addActionListener(e -> selecionarBotaoMapa("Mapa 2", btnMap2));
        btnMap3.addActionListener(e -> selecionarBotaoMapa("Mapa 3", btnMap3));

        panelMapas.add(btnMapAleatorio);
        panelMapas.add(btnMap1);
        panelMapas.add(btnMap2);
        panelMapas.add(btnMap3);

        // Define o padrão inicial visualmente
        selecionarBotaoMapa("Aleatório", btnMapAleatorio);

        // --- BOTÕES DE AÇÃO ---
        JButton confirmar = new JButton("CONFIRMAR E JOGAR");
        confirmar.setBackground(new Color(50, 205, 50)); // Verde
        confirmar.setForeground(Color.WHITE);

        JButton voltar = new JButton("Voltar");

        confirmar.addActionListener(e -> {
            if (campoNome.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Digite o nome do jogador!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            nomeJogador = campoNome.getText();
            dificuldade = facil.isSelected() ? "Fácil" : medio.isSelected() ? "Médio" : "Difícil";

            // Lógica do sorteio se for Aleatório
            String mapaFinal = mapaSelecionado;
            if (mapaSelecionado.equals("Aleatório")) {
                int sorteio = (int) (Math.random() * 3) + 1; // Sorteia 1, 2 ou 3
                mapaFinal = "Mapa " + sorteio;
            }

            // Exibe resumo (pode remover depois)
            JOptionPane.showMessageDialog(this,
                    "Piloto: " + nomeJogador +
                            "\nDificuldade: " + dificuldade +
                            "\nMapa Escolhido: " + mapaFinal + " (" + mapaSelecionado + ")");

            layout.show(container, "JOGO");

            // Tenta focar no jogo (se o painel permitir)
            container.getComponent(3).requestFocus();
        });

        voltar.addActionListener(e -> layout.show(container, "MENU"));

        // Adicionando tudo ao painel principal
        panel.add(lblNome);
        panel.add(campoNome);
        panel.add(lblDificuldade);
        panel.add(panelDificuldade); // Adicionei o painel de radios, não os radios soltos
        panel.add(lblMapa);
        panel.add(panelMapas);       // Adicionei o painel de botões de mapa
        panel.add(Box.createVerticalStrut(10)); // Espaço vazio
        panel.add(confirmar);
        panel.add(voltar);

        return panel;
    }

    // --- MÉTODO AUXILIAR PARA PINTAR OS BOTÕES DE MAPA ---
    private void selecionarBotaoMapa(String mapa, JButton botaoAtivo) {
        this.mapaSelecionado = mapa;

        // 1. Reseta a cor de TODOS os botões para o padrão
        JButton[] todosBotoes = {btnMapAleatorio, btnMap1, btnMap2, btnMap3};
        for (JButton btn : todosBotoes) {
            btn.setBackground(UIManager.getColor("Button.background"));
            btn.setForeground(Color.BLACK);
        }

        // 2. Pinta SÓ o botão clicado de Azul
        botaoAtivo.setBackground(new Color(100, 149, 237)); // Azul Cornflower
        botaoAtivo.setForeground(Color.WHITE);
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
        ranking.setText("1. Ana - 5000\n" + "2. João - 4200\n" + "3. Maria - 3900\n");

        JButton voltar = new JButton("Voltar");
        voltar.addActionListener(e -> layout.show(container, "MENU"));

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(ranking), BorderLayout.CENTER);
        panel.add(voltar, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        // Usa o invokeLater para garantir segurança na thread gráfica
        SwingUtilities.invokeLater(() -> new TelasJogo());
    }
}