package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

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
    private JButton botaoTerminarTurno;
    private JLabel informacaoTabuleiro;
    private PainelPecasBonus painelPecasBonus;
    

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
        
        JPanel inferior = new JPanel(new GridLayout(1, 2, 6, 0));
        inferior.setOpaque(false);
        
     // Peças bônus

        JPanel bonus = criarSecao( "PEÇAS DE BÔNUS", new Color(213, 202, 235));

    	painelPecasBonus = new PainelPecasBonus();

    	bonus.add(painelPecasBonus);
    	
    	
        JPanel cartaConselheiroSecreto = criarSecao( "MEU CONSELHEIRO SECRETO", new Color(213, 202, 235));

        painelCartaConselheiroSecreto = new PainelCartaConselheiroSecreto();

    	JPanel centralizarCarta = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    	centralizarCarta.setOpaque(false);
    	centralizarCarta.add(painelCartaConselheiroSecreto);
    	

        cartaConselheiroSecreto.add(centralizarCarta);
     
        // AÇÕES DISPONÍVEIS
        JPanel acoes = new JPanel(new BorderLayout(0, 6));

        acoes.setBackground(verde);
        acoes.setBorder(new CompoundBorder(
            new LineBorder(dourado, 1),
            new EmptyBorder(8, 8, 8, 8)
        ));

        // Título
        JLabel tituloAcoes = new JLabel("AÇÕES DISPONÍVEIS");
        tituloAcoes.setForeground(vermelho);
        tituloAcoes.setFont(new Font("SansSerif", Font.BOLD, 12));

        acoes.add(tituloAcoes, BorderLayout.NORTH);

        // Grade dos três botões
        JPanel gradeAcoes = new JPanel(new GridLayout(3, 1, 0, 6));
        gradeAcoes.setOpaque(false);

        gradeAcoes.add(criarBotao("Entregar tributo"));
        gradeAcoes.add(criarBotao("Comprar melhoria"));

        botaoTerminarTurno = criarBotao("Terminar Turno");
        gradeAcoes.add(botaoTerminarTurno);

        acoes.add(gradeAcoes, BorderLayout.CENTER);
        
        
        JPanel recursos = criarSecao("RECURSOS", azul);
        valorMoedas =adicionarRecurso(recursos, "Moedas", "0");
        valorEspadas = adicionarRecurso(recursos, "Espadas", "0");
        valorYurtsEstoque = adicionarRecurso(recursos, "Yurts (estoque)", "12");
        valorTributosYurt = adicionarRecurso(recursos, " Tributos de Yurts", "0");

        inferior.add(bonus);
        inferior.add(acoes);
        inferior.add(recursos);
        inferior.add(cartaConselheiroSecreto); 

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

        cartela.add(painelTabuleiroJogador, BorderLayout.CENTER);

        informacaoTabuleiro = new JLabel(
            "Aguardando início da partida.",
            SwingConstants.CENTER
        );

        cartela.add(informacaoTabuleiro, BorderLayout.SOUTH);

        return cartela;
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
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel rotulo = new JLabel(nome);
        rotulo.setForeground(texto);

        JLabel valor = new JLabel(quantidade, SwingConstants.CENTER);
        valor.setOpaque(true);
        valor.setBackground(Color.WHITE);
        valor.setBorder(new LineBorder(dourado));
        valor.setPreferredSize(new Dimension(35, 24));

        linha.add(rotulo, BorderLayout.CENTER);
        linha.add(valor, BorderLayout.EAST);

        painel.add(linha);
        
        return valor; 
    }
    
    public void atualizarRecursos(int moedas, int espadas, int yurtsEstoque, int tributosYurt) {
    	valorMoedas.setText(String.valueOf(moedas)); 
    	valorEspadas.setText(String.valueOf(espadas)); 
    	valorYurtsEstoque.setText(String.valueOf(yurtsEstoque));
        valorTributosYurt.setText(String.valueOf(tributosYurt));
    }

    private JButton criarBotao(String titulo) {
        JButton botao = new JButton(titulo);
        
        botao.setFont(new Font("SansSerif", Font.PLAIN, 12));
        botao.setBackground(new Color(250, 245, 226));
        botao.setForeground(texto);
        botao.setFocusPainted(false);
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        botao.setPreferredSize(new Dimension(150, 32));
        botao.setMaximumSize(new Dimension(180, 32)); 
        botao.setMinimumSize(new Dimension(130,32));
        
        return botao;
    }
    
    public PainelTabuleiroJogador getPainelTabuleiroJogador() {
        return painelTabuleiroJogador;
    }

    public JButton getBotaoTerminarTurno() {
        return botaoTerminarTurno;
    }

    public void mostrarInformacaoTabuleiro(String mensagem) {
        informacaoTabuleiro.setText(mensagem);
    }

    public void atualizarEstadoColunas( boolean[] usadas, boolean podeEscolher,boolean podeTerminar) {

        painelTabuleiroJogador.atualizarEstadoColunas( usadas, podeEscolher);

        botaoTerminarTurno.setEnabled(podeTerminar);
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
            observado.getTributosYurtJogadorAtual()
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
