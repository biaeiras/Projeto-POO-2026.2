package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;

import model.Cor;

public class TelaPreparacaoPartida extends JFrame {

    private static final Color VERMELHO = new Color(166, 45, 48);
    private static final Color DOURADO = new Color(190, 145, 68);
    private static final Color PERGAMINHO = new Color(239, 225, 190);
    private static final Color PAPEL = new Color(250, 240, 216);
    private static final Color TEXTO = new Color(75, 51, 30);

    private final HashMap<Cor, CartelaPersonagem> personagens = new HashMap<>();
    private final JPanel conteudo = new JPanel(new BorderLayout());
    private final JPanel acoesRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    private final JLabel mensagem = new JLabel("Selecione de 2 a 5 personagens.");
    private final JLabel detalhe = new JLabel("0 de 5 personagens selecionados");
    private final JLabel titulo = new JLabel("ESCOLHA SEUS HERDEIROS");
    private final JLabel etapaPersonagens = new JLabel("01  PERSONAGENS");
    private final JLabel etapaConselheiros = new JLabel("02  CONSELHEIROS");
    private final JButton confirmarPersonagens = criarBotao("Confirmar personagens");
    private final JButton continuar = criarBotao("Ver minhas cartas");
    private final PainelCartaConselheiroSecreto primeiraCarta = new PainelCartaConselheiroSecreto();
    private final PainelCartaConselheiroSecreto segundaCarta = new PainelCartaConselheiroSecreto();

    public TelaPreparacaoPartida() {
        setTitle("Herdeiros de Khan - Preparação");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 850);
        setMinimumSize(new Dimension(960, 700));
        setMaximumSize(new Dimension(1400, 1050));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(PERGAMINHO);
        raiz.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        raiz.add(criarCabecalho(), BorderLayout.NORTH);
        conteudo.setOpaque(false);
        raiz.add(conteudo, BorderLayout.CENTER);
        raiz.add(criarRodape(), BorderLayout.SOUTH);
        setContentPane(raiz);
        mostrarSelecaoPersonagens();
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout(0, 12));
        cabecalho.setOpaque(false);
        JPanel faixa = new JPanel(new BorderLayout());
        faixa.setBackground(VERMELHO);
        faixa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, DOURADO),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        titulo.setFont(new Font("Serif", Font.BOLD, 25));
        titulo.setForeground(Color.WHITE);
        JLabel marca = new JLabel("HERDEIROS DE KHAN");
        marca.setFont(new Font("Serif", Font.BOLD, 13));
        marca.setForeground(new Color(255, 231, 184));
        faixa.add(titulo, BorderLayout.WEST);
        faixa.add(marca, BorderLayout.EAST);
        cabecalho.add(faixa, BorderLayout.NORTH);

        JPanel etapas = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        etapas.setOpaque(false);
        etapaPersonagens.setFont(new Font("SansSerif", Font.BOLD, 12));
        etapaConselheiros.setFont(new Font("SansSerif", Font.BOLD, 12));
        etapas.add(etapaPersonagens);
        etapas.add(etapaConselheiros);
        cabecalho.add(etapas, BorderLayout.SOUTH);
        return cabecalho;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout(20, 0));
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, DOURADO),
            BorderFactory.createEmptyBorder(14, 4, 0, 4)));
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 5));
        textos.setOpaque(false);
        mensagem.setFont(new Font("SansSerif", Font.BOLD, 14));
        mensagem.setForeground(TEXTO);
        detalhe.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detalhe.setForeground(TEXTO);
        textos.add(mensagem);
        textos.add(detalhe);
        acoesRodape.setOpaque(false);
        rodape.add(textos, BorderLayout.CENTER);
        rodape.add(acoesRodape, BorderLayout.EAST);
        return rodape;
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setUI(new BasicButtonUI());
        botao.setFont(new Font("SansSerif", Font.BOLD, 14));
        botao.setBackground(VERMELHO);
        botao.setForeground(Color.WHITE);
        botao.setOpaque(true);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DOURADO, 2),
            BorderFactory.createEmptyBorder(12, 22, 12, 22)));
        return botao;
    }

    private void mostrarSelecaoPersonagens() {
        etapaPersonagens.setForeground(VERMELHO);
        etapaConselheiros.setForeground(new Color(129, 106, 75));
        JPanel grade = new JPanel(new GridBagLayout());
        grade.setOpaque(false);
        ((GridBagLayout) grade.getLayout()).columnWeights = new double[] {1, 1, 1, 1, 1, 1};
        adicionarPersonagem(grade, Cor.VERDE, "Altani", "Verde", 0, 0);
        adicionarPersonagem(grade, Cor.VERMELHO, "Chagatai", "Vermelho", 2, 0);
        adicionarPersonagem(grade, Cor.CINZA, "Jochi", "Cinza", 4, 0);
        adicionarPersonagem(grade, Cor.AMARELO, "Tolui", "Amarelo", 1, 1);
        adicionarPersonagem(grade, Cor.AZUL, "Ogedei", "Azul", 3, 1);
        conteudo.add(grade, BorderLayout.CENTER);
        acoesRodape.add(confirmarPersonagens);
        confirmarPersonagens.setEnabled(false);
    }

    private void adicionarPersonagem(JPanel grade, Cor cor, String nome,
            String nomeCor, int coluna, int linha) {
        CartelaPersonagem cartela = new CartelaPersonagem(cor, nome, nomeCor);
        personagens.put(cor, cartela);
        GridBagConstraints posicao = new GridBagConstraints();
        posicao.gridx = coluna;
        posicao.gridy = linha;
        posicao.gridwidth = 2;
        posicao.weightx = 1;
        posicao.weighty = 1;
        posicao.fill = GridBagConstraints.BOTH;
        posicao.insets = new Insets(6, 6, 6, 6);
        grade.add(cartela, posicao);
    }

    public JPanel getCartelaPersonagem(Cor cor) {
        return personagens.get(cor);
    }

    public void destacarFocoTeclado(Cor cor, boolean destacar) {
        CartelaPersonagem cartela = personagens.get(cor);
        cartela.focoPorTeclado = destacar;
        cartela.repaint();
    }

    public void alternarPersonagem(Cor cor) {
        CartelaPersonagem cartela = personagens.get(cor);
        cartela.selecionada = !cartela.selecionada;
        cartela.repaint();
        int quantidade = getCoresEscolhidas().size();
        confirmarPersonagens.setEnabled(quantidade >= 2);
        mensagem.setForeground(TEXTO);
        mensagem.setText(quantidade >= 2
            ? "Seu grupo está pronto. Confirme para escolher os conselheiros."
            : "Selecione pelo menos 2 personagens para continuar.");
        detalhe.setText(quantidade + " de 5 personagens selecionados");
    }

    public ArrayList<Cor> getCoresEscolhidas() {
        ArrayList<Cor> cores = new ArrayList<>();
        for (Cor cor : Cor.values()) {
            if (personagens.get(cor).selecionada) {
                cores.add(cor);
            }
        }
        return cores;
    }

    private void prepararEtapaConselheiros(String textoTitulo) {
        conteudo.removeAll();
        acoesRodape.removeAll();
        titulo.setText(textoTitulo);
        etapaPersonagens.setForeground(new Color(129, 106, 75));
        etapaConselheiros.setForeground(VERMELHO);
        mensagem.setForeground(TEXTO);
    }

    public void mostrarVezDeEscolher(String personagem) {
        prepararEtapaConselheiros("UM CONSELHEIRO PARA CADA HERDEIRO");
        primeiraCarta.definirCarta(null);
        segundaCarta.definirCarta(null);
        JPanel passagem = new JPanel(new GridLayout(1, 2, 28, 0));
        passagem.setOpaque(false);
        passagem.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        passagem.add(new ImagemCartela(personagem));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Box.createVerticalGlue());
        JLabel vez = new JLabel("É a vez de " + personagem);
        vez.setFont(new Font("Serif", Font.BOLD, 34));
        vez.setForeground(VERMELHO);
        textos.add(vez);
        textos.add(Box.createVerticalStrut(18));
        JLabel instrucao = new JLabel("<html>Dois conselheiros aguardam sua escolha.<br>"
            + "Você poderá ficar com apenas um deles.</html>");
        instrucao.setFont(new Font("SansSerif", Font.PLAIN, 17));
        instrucao.setForeground(TEXTO);
        textos.add(instrucao);
        textos.add(Box.createVerticalStrut(18));
        JLabel privacidade = new JLabel("<html>As cartas são secretas.<br>"
            + "Passe a tela para " + personagem + " antes de continuar.</html>");
        privacidade.setFont(new Font("SansSerif", Font.ITALIC, 14));
        privacidade.setForeground(TEXTO);
        textos.add(privacidade);
        textos.add(Box.createVerticalGlue());
        passagem.add(textos);
        conteudo.add(passagem, BorderLayout.CENTER);
        mensagem.setText("Somente " + personagem + " deve ver as próximas cartas.");
        detalhe.setText("A carta escolhida será mantida em segredo durante a partida.");
        acoesRodape.add(continuar);
        atualizarLayout();
    }

    public void mostrarConselheiros(String personagem, ArrayList<String> opcoes) {
        if (opcoes == null || opcoes.size() != 2 || opcoes.contains(null)) {
            throw new IllegalArgumentException("Devem ser fornecidas duas opções de conselheiro.");
        }
        prepararEtapaConselheiros("A ESCOLHA DE " + personagem.toUpperCase(Locale.ROOT));
        primeiraCarta.carregarCarta(opcoes.get(0).toLowerCase(Locale.ROOT) + ".jpg");
        segundaCarta.carregarCarta(opcoes.get(1).toLowerCase(Locale.ROOT) + ".jpg");
        JPanel cartas = new JPanel(new GridLayout(1, 2, 32, 0));
        cartas.setOpaque(false);
        cartas.setBorder(BorderFactory.createEmptyBorder(4, 70, 4, 70));
        cartas.add(criarMolduraCarta(primeiraCarta, "PRIMEIRA OPÇÃO"));
        cartas.add(criarMolduraCarta(segundaCarta, "SEGUNDA OPÇÃO"));
        conteudo.add(cartas, BorderLayout.CENTER);
        mensagem.setText("Clique diretamente na carta para escolher seu conselheiro.");
        detalhe.setText("Escolha uma carta. A outra será descartada.");
        atualizarLayout();
    }

    private JPanel criarMolduraCarta(PainelCartaConselheiroSecreto carta, String nome) {
        JPanel moldura = new JPanel(new BorderLayout(0, 10));
        moldura.setBackground(PAPEL);
        moldura.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DOURADO),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        JLabel legenda = new JLabel(nome, SwingConstants.CENTER);
        legenda.setFont(new Font("SansSerif", Font.BOLD, 12));
        legenda.setForeground(TEXTO);
        moldura.add(legenda, BorderLayout.NORTH);
        moldura.add(carta, BorderLayout.CENTER);
        return moldura;
    }

    private void atualizarLayout() {
        conteudo.revalidate();
        conteudo.repaint();
        acoesRodape.revalidate();
        acoesRodape.repaint();
    }

    public void mostrarMensagem(String texto) {
        mensagem.setForeground(VERMELHO);
        mensagem.setText(texto);
    }

    public JButton getBotaoConfirmarPersonagens() { return confirmarPersonagens; }
    public JButton getBotaoContinuar() { return continuar; }
    public PainelCartaConselheiroSecreto getPrimeiraCarta() { return primeiraCarta; }
    public PainelCartaConselheiroSecreto getSegundaCarta() { return segundaCarta; }

    private static BufferedImage carregarCartela(String nome) {
        String caminho = "/tabuleiros/" + nome.toLowerCase(Locale.ROOT) + ".jpg";
        java.net.URL recurso = TelaPreparacaoPartida.class.getResource(caminho);
        if (recurso == null) {
            throw new IllegalStateException("Imagem não encontrada: " + caminho);
        }
        try {
            BufferedImage imagem = ImageIO.read(recurso);
            if (imagem == null) throw new IllegalStateException("Imagem inválida: " + caminho);
            return imagem;
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar cartela: " + caminho, e);
        }
    }

    private static void desenharImagem(Graphics2D g, BufferedImage imagem,
            int x, int y, int largura, int altura) {
        if (largura <= 0 || altura <= 0) return;
        double escala = Math.min((double) largura / imagem.getWidth(),
            (double) altura / imagem.getHeight());
        int w = (int) (imagem.getWidth() * escala);
        int h = (int) (imagem.getHeight() * escala);
        g.drawImage(imagem, x + (largura - w) / 2, y + (altura - h) / 2, w, h, null);
    }

    private static class CartelaPersonagem extends JPanel {
        private final Cor cor;
        private final String nome;
        private final String nomeCor;
        private final BufferedImage imagem;
        private boolean selecionada;
        private boolean focoPorTeclado;

        CartelaPersonagem(Cor cor, String nome, String nomeCor) {
            this.cor = cor;
            this.nome = nome;
            this.nomeCor = nomeCor;
            imagem = carregarCartela(nome);
            setOpaque(false);
            setPreferredSize(new Dimension(340, 260));
            setMinimumSize(new Dimension(0, 0));
            setFocusable(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            getAccessibleContext().setAccessibleName(nome + ", " + nomeCor);
            getAccessibleContext().setAccessibleDescription("Selecione ou desmarque este personagem.");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setColor(selecionada ? new Color(255, 245, 214) : PAPEL);
                g2.fillRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);
                g2.setColor(selecionada ? VERMELHO : DOURADO);
                g2.setStroke(new BasicStroke(selecionada ? 3 : 1));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);
                if (focoPorTeclado && hasFocus()) {
                    g2.setColor(DOURADO);
                    g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND, 0, new float[] {4, 4}, 0));
                    g2.drawRoundRect(7, 7, getWidth() - 15, getHeight() - 15, 8, 8);
                }
                g2.setFont(new Font("Serif", Font.BOLD, 21));
                g2.setColor(TEXTO);
                g2.drawString(nome, 16, 31);
                g2.setColor(ViewAPI.getColor(cor));
                g2.fillOval(getWidth() - 33, 18, 12, 12);
                desenharImagem(g2, imagem, 12, 42, getWidth() - 24, getHeight() - 78);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.setColor(TEXTO);
                g2.drawString(nomeCor.toUpperCase(Locale.ROOT), 16, getHeight() - 17);
                String estado = selecionada ? "SELECIONADO" : "CLIQUE PARA ESCOLHER";
                g2.setColor(selecionada ? VERMELHO : TEXTO);
                g2.drawString(estado, getWidth() - g2.getFontMetrics().stringWidth(estado) - 16,
                    getHeight() - 17);
            } finally {
                g2.dispose();
            }
        }
    }

    private static class ImagemCartela extends JPanel {
        private final BufferedImage imagem;
        ImagemCartela(String personagem) {
            imagem = carregarCartela(personagem);
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                desenharImagem(g2, imagem, 0, 0, getWidth(), getHeight());
            } finally {
                g2.dispose();
            }
        }
    }
}
