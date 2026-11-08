package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import model.ObservadoJogo;
import model.ObservadorJogo;
import view.PainelTabuleiroJogador.MelhoriaVisual;

public class TelaJogador extends JPanel implements ObservadorJogo {

    private final Color vermelho = new Color(166, 45, 48);
    private final Color dourado = new Color(190, 145, 68);
    private final Color pergaminho = new Color(239, 225, 190);
    private final Color texto = new Color(75, 51, 30);
    private final Color azul = new Color(190, 218, 239);
    private final Color verde = new Color(190, 220, 173);
    
    private PainelCartaConselheiroSecreto painelCartaConselheiroSecreto;
    
    
    private JLabel valorMoedas; 
    private JLabel valorEspadas; 
    private JLabel valorYurtsEstoque; 
    private JLabel valorTributosYurt; 
    
    private PainelTabuleiroJogador painelTabuleiroJogador;
    private PainelPecasBonus painelPecasBonus;
    private JLabel informacaoTabuleiro;
    private final HashMap<String, JLabel[]> contadoresTesouros = new HashMap<>();
 
    

    public TelaJogador() {
        setLayout(new BorderLayout(8, 8));
        setBackground(pergaminho);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        criarCabecalho();
        criarConteudo();
    }

    private JPanel  criarCabecalho() {
    	JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(vermelho);
        cabecalho.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel titulo = new JLabel(
            "TABULEIRO DO JOGADOR",
            SwingConstants.LEFT
        );

        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Serif", Font.BOLD, 16));

        cabecalho.add(titulo, BorderLayout.WEST);

        return cabecalho;
 
    }

    private void criarConteudo() {
        JPanel conteudo = new JPanel(new BorderLayout(6, 6));
        conteudo.setOpaque(false);

        // Tabuleiro individual
        JPanel tabuleiro = new JPanel(new BorderLayout());
        tabuleiro.setBackground(pergaminho);
        tabuleiro.setBorder(new LineBorder(dourado, 1));

        tabuleiro.add(criarCabecalho(), BorderLayout.NORTH);
        tabuleiro.add(criarGradeTabuleiro(), BorderLayout.CENTER);
        
        JPanel inferior = new JPanel(new GridBagLayout());
        inferior.setOpaque(false);
        
     // Peças bônus

        JPanel bonus = criarSecao( "PEÇAS DE BÔNUS", new Color(213, 202, 235));


        painelPecasBonus = new PainelPecasBonus();
        bonus.setName("painelBonus");
        painelPecasBonus.setAlignmentX(Component.LEFT_ALIGNMENT);
        bonus.add(painelPecasBonus);

        JPanel cartaConselheiroSecreto = criarSecao( "<html>CONSELHEIRO<br>SECRETO</html>", new Color(213, 202, 235));

        cartaConselheiroSecreto.setName("painelConselheiro");
        painelCartaConselheiroSecreto = new PainelCartaConselheiroSecreto();

        JPanel centralizarCarta = new JPanel(new BorderLayout());
        centralizarCarta.setOpaque(false);
        centralizarCarta.setAlignmentX(Component.LEFT_ALIGNMENT);
        centralizarCarta.add(painelCartaConselheiroSecreto, BorderLayout.CENTER);
        cartaConselheiroSecreto.add(centralizarCarta);

        JPanel recursos = criarSecao("RECURSOS", azul);
        valorMoedas =adicionarRecurso(recursos, "Moedas", "0");
        valorMoedas.setName("recurso.MOEDA");
        valorEspadas = adicionarRecurso(recursos, "Espadas", "0");
        valorEspadas.setName("recurso.ESPADA");
        valorYurtsEstoque = adicionarRecurso(recursos, "<html>Yurts<br>(estoque)</html>", "12");
        valorYurtsEstoque.setName("estoque.YURTS");
        valorTributosYurt = adicionarRecurso(recursos, "<html>Tributos<br>de Yurts</html>", "0");
        valorTributosYurt.setName("recurso.YURT");
        JLabel aviso = new JLabel("<html>Virtuais: somente neste turno.<br>Usados antes dos recursos reais.</html>");
        aviso.setName("avisoRecursosVirtuais");
        aviso.setAlignmentX(Component.LEFT_ALIGNMENT);
        aviso.setFont(new Font("SansSerif", Font.PLAIN, 11));
        aviso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        recursos.add(aviso);

        JPanel tesouros = criarSecao("TESOUROS", azul);
        tesouros.setName("painelTesouros");
        tesouros.add(criarPainelTesouros());
        recursos.setName("painelRecursos");
        bonus.setMinimumSize(new Dimension(145, 210));
        recursos.setMinimumSize(new Dimension(225, 210));
        tesouros.setMinimumSize(new Dimension(260, 210));
        cartaConselheiroSecreto.setMinimumSize(new Dimension(150, 210));
        inferior.setName("informacoesJogador");
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.insets = new Insets(0, 3, 0, 3);
        c.gridx = 0; c.weightx = 1;
        inferior.add(bonus, c);
        c.gridx = 1; c.weightx = 1.15;
        inferior.add(recursos, c);
        c.gridx = 2; c.weightx = 1.3;
        inferior.add(tesouros, c);
        c.gridx = 3; c.weightx = 0.7;
        inferior.add(cartaConselheiroSecreto, c);

        conteudo.add(tabuleiro, BorderLayout.CENTER);
        conteudo.add(inferior, BorderLayout.SOUTH);

        add(conteudo, BorderLayout.CENTER);
    }


    private JPanel criarGradeTabuleiro() {
        JPanel cartela = new JPanel(new BorderLayout(5, 5));

        cartela.setBackground(new Color(214, 190, 145));
        cartela.setBorder(new CompoundBorder(
            new LineBorder(dourado, 2),
            new EmptyBorder(5, 5, 5, 5)
        ));

        painelTabuleiroJogador =
            new PainelTabuleiroJogador(null);

        cartela.add(criarMolduraTabuleiro(painelTabuleiroJogador), BorderLayout.CENTER);

        informacaoTabuleiro = new JLabel(
            "Aguardando início da partida.",
            SwingConstants.CENTER
        );

        cartela.add(informacaoTabuleiro, BorderLayout.SOUTH);

        return cartela;
    }
    
    static JPanel criarMolduraTabuleiro(final PainelTabuleiroJogador painel) {
        JPanel moldura = new JPanel(null) {
            @Override
            public Dimension getPreferredSize() { return painel.getPreferredSize(); }
            @Override
            public void doLayout() {
                Dimension proporcao = painel.getPreferredSize();
                double escala = Math.min((double) getWidth() / proporcao.width,
                        (double) getHeight() / proporcao.height);
                int largura = Math.max(0, (int) (proporcao.width * escala));
                int altura = Math.max(0, (int) (proporcao.height * escala));
                painel.setBounds((getWidth() - largura) / 2, (getHeight() - altura) / 2, largura, altura);
            }
        };
        moldura.setOpaque(false);
        moldura.setMinimumSize(new Dimension(180, 100));
        moldura.add(painel);
        return moldura;
    }

    private JPanel criarSecao(String titulo, Color fundo) {
        JPanel secao = new JPanel();
        secao.setLayout(new BoxLayout(secao, BoxLayout.Y_AXIS));
        secao.setBackground(fundo);
        secao.setBorder(new CompoundBorder(
            new LineBorder(dourado, 1),
            new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel rotulo = new JLabel(titulo);
        rotulo.setForeground(vermelho);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        secao.add(rotulo);
        secao.add(Box.createVerticalStrut(8));

        return secao;
    }

    private JLabel adicionarRecurso(JPanel painel, String nome, String quantidade) {

        JPanel linha = new JPanel(new BorderLayout(5, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel rotulo = new JLabel(nome);
        rotulo.setForeground(texto);

        JLabel valor = new JLabel(quantidade, SwingConstants.CENTER);
        valor.setOpaque(true);
        valor.setBackground(Color.WHITE);
        valor.setBorder(new LineBorder(dourado));
        valor.setPreferredSize(new Dimension(140, 28));
        valor.setMinimumSize(new Dimension(112, 28));
        valor.setFont(new Font("SansSerif", Font.BOLD, 11));

        linha.add(rotulo, BorderLayout.CENTER);
        linha.add(valor, BorderLayout.EAST);

        painel.add(linha);
        
        return valor; 
    }
    
    public void atualizarRecursos(int moedas, int espadas, int yurtsEstoque, int tributosYurt) {
        atualizarRecursos(moedas, espadas, yurtsEstoque, tributosYurt, new HashMap<String, Integer>());
    }

    private void atualizarRecursos(int moedas, int espadas, int yurtsEstoque, int tributosYurt,
            HashMap<String, Integer> virtuais) {
        atualizarRecurso(valorMoedas, moedas, virtuais.getOrDefault("MOEDA", 0));
        atualizarRecurso(valorEspadas, espadas, virtuais.getOrDefault("ESPADA", 0));
        atualizarRecurso(valorTributosYurt, tributosYurt, virtuais.getOrDefault("YURT", 0));
        valorYurtsEstoque.setText(String.valueOf(yurtsEstoque));
    }

    private void atualizarRecurso(JLabel rotulo, int real, int virtual) {
        rotulo.setText(virtual == 0 ? String.valueOf(real)
                : (real + virtual) + " (" + real + " + " + virtual + " virtual)");
        rotulo.setToolTipText(real + " real(is) + " + virtual + " virtual(is) neste turno");
    }

    private JPanel criarPainelTesouros() {
        JPanel painel = new JPanel(new BorderLayout(6, 6));
        painel.setBackground(azul);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.setBorder(new EmptyBorder(0, 0, 0, 0));
        JPanel tabela = new JPanel(new GridBagLayout());
        tabela.setOpaque(false);
        GridBagConstraints celula = new GridBagConstraints();
        celula.fill = GridBagConstraints.BOTH;
        celula.weighty = 1;
        celula.insets = new Insets(2, 2, 2, 2);
        celula.gridy = 0;
        int coluna = 0;
        for (String titulo : new String[] {"Tesouro", "Estoque", "Virtuais", "Total"}) {
            JLabel cabecalho = new JLabel(titulo, SwingConstants.CENTER);
            cabecalho.setForeground(texto);
            cabecalho.setFont(new Font("SansSerif", Font.BOLD, 11));
            celula.gridx = coluna++;
            celula.weightx = celula.gridx == 0 ? 1.5 : 1;
            tabela.add(cabecalho, celula);
        }
        String[] tipos = {"BOI", "FERRO", "PELE", "TECIDO", "ALIMENTO"};
        String[] nomes = {"Boi", "Ferro", "Pele", "Tecido", "Alimento"};
        int[] imagens = {1, 3, 4, 2, 5};
        for (int i = 0; i < tipos.length; i++) {
            java.net.URL caminho = getClass().getResource("/fichas/cartoes7-" + imagens[i] + ".jpg");
            final Image imagem;
            try {
                if (caminho == null) throw new java.io.IOException("Recurso não encontrado");
                imagem = javax.imageio.ImageIO.read(caminho);
                if (imagem == null) throw new java.io.IOException("Imagem inválida");
            } catch (java.io.IOException erro) {
                throw new IllegalStateException("Não foi possível carregar o tesouro " + tipos[i], erro);
            }
            JPanel figura = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D desenho = (Graphics2D) g.create();
                    try {
                        desenho.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        int lado = Math.min(getWidth(), getHeight());
                        desenho.drawImage(imagem, (getWidth() - lado) / 2,
                                (getHeight() - lado) / 2, lado, lado, this);
                    } finally {
                        desenho.dispose();
                    }
                }
            };
            figura.setOpaque(false);
            figura.setName("imagemTesouro." + tipos[i]);
            figura.setPreferredSize(new Dimension(26, 26));
            JLabel nome = new JLabel(nomes[i]);
            nome.setForeground(texto);
            nome.setFont(new Font("SansSerif", Font.PLAIN, 11));
            celula.gridy = i + 1;
            celula.gridx = 0; celula.weightx = 1.5;
            JPanel identificacao = new JPanel(new BorderLayout(4, 0));
            identificacao.setOpaque(false);
            identificacao.add(figura, BorderLayout.WEST);
            identificacao.add(nome, BorderLayout.CENTER);
            tabela.add(identificacao, celula);
            JLabel[] campos = new JLabel[3];
            String[] colunas = {"real", "virtual", "total"};
            for (int j = 0; j < campos.length; j++) {
                campos[j] = new JLabel("0", SwingConstants.CENTER);
                campos[j].setName("tesouro." + tipos[i] + "." + colunas[j]);
                campos[j].setForeground(texto);
                campos[j].setOpaque(true);
                campos[j].setBackground(new Color(250, 245, 226));
                campos[j].setBorder(new LineBorder(dourado));
                celula.gridx = j + 1; celula.weightx = 1;
                tabela.add(campos[j], celula);
            }
            contadoresTesouros.put(tipos[i], campos);
        }
        painel.add(tabela, BorderLayout.CENTER);
        JLabel aviso = new JLabel("<html>Virtuais desaparecem<br>ao terminar o turno.</html>");
        aviso.setFont(new Font("SansSerif", Font.PLAIN, 11));
        aviso.setForeground(texto);
        painel.add(aviso, BorderLayout.SOUTH);
        return painel;
    }

    private void atualizarTesouros(ObservadoJogo observado) {
        HashMap<String, Integer> reais = observado.getTesourosJogadorAtual();
        HashMap<String, Integer> virtuais = observado.getTesourosVirtuaisJogadorAtual();
        for (String tipo : contadoresTesouros.keySet()) {
            int real = reais.getOrDefault(tipo, 0);
            int virtual = virtuais.getOrDefault(tipo, 0);
            JLabel[] campos = contadoresTesouros.get(tipo);
            campos[0].setText(String.valueOf(real));
            campos[1].setText(String.valueOf(virtual));
            campos[2].setText(String.valueOf(real + virtual));
        }
    }

    public PainelTabuleiroJogador getPainelTabuleiroJogador() {
        return painelTabuleiroJogador;
    }

    public void mostrarInformacaoTabuleiro(String mensagem) {
        informacaoTabuleiro.setText(mensagem);
    }

    public void atualizarEstadoColunas( boolean[] usadas, boolean podeEscolher,boolean podeTerminar) {

        painelTabuleiroJogador.atualizarEstadoColunas( usadas, podeEscolher);

    }

    @Override
    public void atualizar(final ObservadoJogo observado) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    atualizarInterface(observado);
                }
            });
            return;
        }

        atualizarInterface(observado);
    }

    private void atualizarInterface(ObservadoJogo observado) {
        atualizarTesouros(observado);
       
        if (!observado.existePartida()) {
            painelCartaConselheiroSecreto.definirCarta(null);
            painelTabuleiroJogador.definirPersonagem(null);
            painelTabuleiroJogador.definirCorFicha(null);
            painelTabuleiroJogador.atualizarMelhorias(
                new ArrayList<MelhoriaVisual>()
            );
            
            //Limpa peças bônus quando não existe partidas
            
            painelPecasBonus.atualizarPecas(
            	    new int[] {-1, -1, -1, -1, -1, -1, -1},
            	    new boolean[7]
            	);

            atualizarEstadoColunas(
                new boolean[4], false, false
            );

            atualizarRecursos(0, 0, 0, 0);
            mostrarInformacaoTabuleiro(
                "Aguardando início da partida."
            );

            return;
        }

        painelTabuleiroJogador.definirPersonagem(
            observado.getNomePersonagemAtual()
        );
        painelTabuleiroJogador.definirCorFicha(observado.getCorJogadorAtual());

        String conselheiro = observado.getNomeConselheiroSecretoJogadorAtual();
        if (conselheiro == null) {
            painelCartaConselheiroSecreto.definirCarta(null);
        } else {
            painelCartaConselheiroSecreto.carregarCarta(
                conselheiro.toLowerCase(java.util.Locale.ROOT) + ".jpg"
            );
        }

        painelTabuleiroJogador.atualizarMelhorias(
            criarMelhoriasVisuais(observado)
        );

        boolean[] usadas = new boolean[4];

        for (int coluna = 0; coluna < usadas.length; coluna++) {
            usadas[coluna] =
                observado.isColunaJogadorAtualAtivada(coluna);
        }

        boolean turnoAberto = observado.isTurnoEmAndamento();

        atualizarEstadoColunas(
            usadas,
            observado.podeEscolherColuna(),
            turnoAberto && !observado.isPartidaEncerrada()
        );

        atualizarRecursos(
            observado.getMoedasJogadorAtual(),
            observado.getEspadasJogadorAtual(),
            observado.getYurtsEstoqueJogadorAtual(),
            observado.getTributosYurtJogadorAtual(),
            observado.getTributosVirtuaisJogadorAtual()
        );

        String situacao;
        
        painelPecasBonus.atualizarPecas(observado.getOrdemPecasBonusJogadorAtual(), observado.getPecasBonusUsadasJogadorAtual());

        if (observado.isPartidaEncerrada()) {
            situacao = "Partida encerrada.";
        } else if (turnoAberto) {
            situacao = "Turno em andamento.";
        } else if (observado.podeEscolherColuna()) {
            situacao = "Clique no retângulo inferior de uma coluna.";
        } else {
            situacao = "Conclua a escolha dos conselheiros.";
        }

        mostrarInformacaoTabuleiro(
            observado.getNomePersonagemAtual()
            + " | Fichas: "
            + observado.getFichasAtivacaoJogadorAtual()
            + " | "
            + situacao
        );
    }

    public static ArrayList<MelhoriaVisual> criarMelhoriasVisuais(
            ObservadoJogo observado) {
        ArrayList<MelhoriaVisual> melhorias =
            new ArrayList<>();

        if (!observado.existePartida()) {
            return melhorias;
        }

        for (int coluna = 0; coluna < 4; coluna++) {
            ArrayList<String> lista =
                observado.getMelhoriasTabuleiroJogador(coluna);

            for (int linha = 0; linha < lista.size(); linha++) {
                melhorias.add(new MelhoriaVisual(
                    lista.get(linha), coluna, linha
                ));
            }
        }

        return melhorias;
    }

}
