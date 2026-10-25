
package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    private JPanel areaMapa;
    private JPanel painelLateral;
    private JPanel painelMensagens;
    private JPanel secaoUltimaRodada; 
    private PainelCartasConselheirosAberto painelCartas;
    
    private JLabel avisoRodada; 
    

    private final Color vermelho = new Color(166, 45, 48);
    private final Color dourado = new Color(190, 145, 68);
    private final Color pergaminho = new Color(239, 225, 190);
    private final Color texto = new Color(75, 51, 30);
    private final Color azul = new Color(48, 83, 105);

    public TelaPrincipal() {
        setTitle("Herdeiros de Khan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        getContentPane().setBackground(pergaminho);
        setLayout(new BorderLayout(8, 8));

        criarCabecalho();
        criarMapa();
        criarPainelLateral();
        criarMensagens();
    }

    private void criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(vermelho);
        cabecalho.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel titulo = new JLabel("HERDEIROS DE KHAN");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Serif", Font.BOLD, 22));

        JLabel subtitulo = new JLabel("Mapa da partida");
        subtitulo.setForeground(new Color(255, 235, 210));
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        cabecalho.add(titulo, BorderLayout.WEST);
        cabecalho.add(subtitulo, BorderLayout.EAST);

        add(cabecalho, BorderLayout.NORTH);
    }

    private void criarMapa() {
        areaMapa = new JPanel(new BorderLayout());
        areaMapa.setBackground(new Color(214, 190, 145));
        areaMapa.setBorder(new CompoundBorder(
            new LineBorder(dourado, 4),
            new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel tituloMapa = new JLabel(
            "<html><center><b>MAPA DO JOGO</b><br><br>"
            + "Área reservada para o tabuleiro<br>"
            + "Cidades, caminhos, peças e tesouros"
            + "</center></html>",
            SwingConstants.CENTER
        );

        tituloMapa.setForeground(texto);
        tituloMapa.setFont(new Font("Serif", Font.PLAIN, 18));

        areaMapa.add(tituloMapa, BorderLayout.CENTER);

        JPanel molduraMapa = new JPanel(new BorderLayout());
        molduraMapa.setBackground(pergaminho);
        molduraMapa.setBorder(new EmptyBorder(8, 8, 8, 4));
        molduraMapa.add(areaMapa, BorderLayout.CENTER);

        add(molduraMapa, BorderLayout.CENTER);
    }

    private void criarPainelLateral() {
        painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(
            painelLateral, BoxLayout.Y_AXIS
        ));
        painelLateral.setBackground(pergaminho);
        painelLateral.setPreferredSize(new Dimension(285, 0));
        painelLateral.setBorder(new EmptyBorder(8, 4, 8, 8));

        JPanel navegacao = new JPanel(new GridLayout(1, 2, 6, 0));
        navegacao.setOpaque(false);
        navegacao.setMaximumSize(new Dimension(280, 42));

        JButton botaoMapa = criarBotao("Mapa", azul);
        JButton botaoJogador = criarBotao("Jogador", azul);
        
        
        botaoJogador.addActionListener(e -> {
            JFrame janelaAtual =
                (JFrame) SwingUtilities.getWindowAncestor(painelLateral);

            if (janelaAtual != null) {
                janelaAtual.setContentPane(new TelaJogador());
                janelaAtual.revalidate();
                janelaAtual.repaint();
            } else {
                System.out.println("Não foi possível encontrar a janela.");
            }
        });

        
        navegacao.add(botaoMapa);
        navegacao.add(botaoJogador);

        painelLateral.add(navegacao);
        painelLateral.add(Box.createVerticalStrut(10));
        
        
        //Seção que mostra o jogador que irá jogar

        JPanel vez = criarSecao("VEZ DE JOGAR");

        JLabel jogadorAtual = new JLabel("●  Altani", SwingConstants.CENTER);
        jogadorAtual.setForeground(texto);
        jogadorAtual.setFont(new Font("SansSerif", Font.BOLD, 16));
        jogadorAtual.setAlignmentX(Component.CENTER_ALIGNMENT);
        jogadorAtual.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel corJogador = new JLabel("Cor: não definida", SwingConstants.CENTER);
        corJogador.setForeground(texto);
        corJogador.setAlignmentX(Component.CENTER_ALIGNMENT);
        corJogador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        vez.add(jogadorAtual);
        vez.add(corJogador);

        adicionarSecao(vez);
        
        
        //Se essa rodada for a última 
        
        secaoUltimaRodada = criarSecao("ÚLTIMA RODADA");
        avisoRodada = new JLabel("<html>A última rodada começou!</html>" );
        avisoRodada.setForeground(vermelho);
        avisoRodada.setAlignmentX(Component.LEFT_ALIGNMENT);
        secaoUltimaRodada.add(avisoRodada);
        
        secaoUltimaRodada.setVisible(false); 

        adicionarSecao(secaoUltimaRodada);
        
  
  

        JPanel conselheiros = criarSecao("CARTAS DE CONSELHEIROS ABERTO");

        painelCartas = new PainelCartasConselheirosAberto();
        painelCartas.carregarCartasIniciais();

        conselheiros.add(painelCartas);

        adicionarSecao(conselheiros);
        
    
        JPanel placar = criarSecao("PLACAR DE VOTOS");
        JLabel explicacao = new JLabel("Mais votos aparece primeiro");
        explicacao.setFont(new Font("SansSerif", Font.ITALIC, 11));
        explicacao.setForeground(texto);
        explicacao.setAlignmentX(Component.LEFT_ALIGNMENT);
        placar.add(explicacao);
        
        
        //Depois vai atualizar passando a lista de votos por jogador real 

        placar.add(criarLinhaPlacar("1º Jogador A", 0));
        placar.add(criarLinhaPlacar("2º Jogador B", 0));
        placar.add(criarLinhaPlacar("3º Jogador C", 0));
        placar.add(criarLinhaPlacar("4º Jogador D", 0));
        adicionarSecao(placar);

        painelLateral.add(Box.createVerticalGlue());

        JScrollPane rolagem = new JScrollPane(painelLateral);
        rolagem.setBorder(null);
        rolagem.getViewport().setBackground(pergaminho);
        rolagem.setHorizontalScrollBarPolicy(
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(rolagem, BorderLayout.EAST);
    }

    private JPanel criarSecao(String titulo) {
        JPanel secao = new JPanel();
        secao.setLayout(new BoxLayout(secao, BoxLayout.Y_AXIS));
        secao.setBackground(new Color(247, 237, 211));
        secao.setBorder(new CompoundBorder(
            new LineBorder(dourado, 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        secao.setAlignmentX(Component.CENTER_ALIGNMENT);
        secao.setMaximumSize(new Dimension(
            Integer.MAX_VALUE, 180
        ));

        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        rotulo.setForeground(vermelho);

        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        rotulo.setHorizontalAlignment(SwingConstants.LEFT);
        rotulo.setPreferredSize(new Dimension(250, 20));
        rotulo.setMaximumSize(new Dimension(250, 20));
        
        secao.add(rotulo);
        secao.add(Box.createVerticalStrut(8));

        return secao;
    }

    private void adicionarSecao(JPanel secao) {
        secao.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelLateral.add(secao);
        painelLateral.add(Box.createVerticalStrut(8));
    }

    private JButton criarBotao(String titulo, Color cor) {
        JButton botao = new JButton(titulo);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("SansSerif", Font.BOLD, 13));
        botao.setFocusPainted(false);
        botao.setBorder(new CompoundBorder(
            new LineBorder(dourado, 2),
            new EmptyBorder(7, 10, 7, 10)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private void criarMensagens() {
        painelMensagens = new JPanel(new BorderLayout(4, 4));
        painelMensagens.setBackground(new Color(207, 224, 232));
        painelMensagens.setBorder(new CompoundBorder(
            new LineBorder(azul, 2),
            new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel titulo = new JLabel("MENSAGENS DO JOGO");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        titulo.setForeground(azul);

        JLabel mensagem = new JLabel(
            "A partida aparecerá aqui quando as ações forem conectadas."
        );
        mensagem.setForeground(texto);

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.add(titulo);
        conteudo.add(Box.createVerticalStrut(4));
        conteudo.add(mensagem);

        painelMensagens.add(conteudo, BorderLayout.CENTER);

        JPanel margem = new JPanel(new BorderLayout());
        margem.setBackground(pergaminho);
        margem.setBorder(new EmptyBorder(0, 8, 8, 8));
        margem.add(painelMensagens, BorderLayout.CENTER);

        add(margem, BorderLayout.SOUTH);
    }
    
    public void mostrarUltimaRodada(String mensagem) {
        avisoRodada.setText("<html>" + mensagem + "</html>");
        secaoUltimaRodada.setVisible(true);

        painelLateral.revalidate();
        painelLateral.repaint();
    }
    
    private JPanel criarLinhaPlacar(String nome, int votos) {
    	JPanel linha = new JPanel(new BorderLayout(5,0)); 
    	linha.setOpaque(false);
    	linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
    	
    	JLabel nomeJogador = new JLabel(nome);
    	nomeJogador.setForeground(texto);
    	
    	JLabel valorVotos = new JLabel(String.valueOf(votos), SwingConstants.CENTER);
    	
    	valorVotos.setOpaque(true);
    	valorVotos.setBackground(Color.WHITE);
    	valorVotos.setBorder(new LineBorder(dourado));
    	valorVotos.setPreferredSize(new Dimension(40,24));
    	
    	linha.add(nomeJogador, BorderLayout.CENTER); 
    	linha.add(valorVotos, BorderLayout.EAST); 
    	
    	return linha; 
    	
    }
}
