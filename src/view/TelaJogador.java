package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class TelaJogador extends JPanel {

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

    	PainelPecasBonus painelPecasBonus = new PainelPecasBonus();

    	bonus.add(painelPecasBonus);
    	
    	//SÓ PARA TESTAR : depois vai ser alterado para o controller
    	
    	painelPecasBonus.atualizarPecas(
    		    new boolean[] {
    		        true, true, false, false, false, false, false
    		    },
    		    new boolean[] {
    		        false, true, false, false, false, false, false
    		    }
    		);
        
        
        JPanel cartaConselheiroSecreto = criarSecao( "MEU CONSELHEIRO SECRETO", new Color(213, 202, 235));

        painelCartaConselheiroSecreto = new PainelCartaConselheiroSecreto();
        painelCartaConselheiroSecreto.carregarCartaInicial();

    	JPanel centralizarCarta = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    	centralizarCarta.setOpaque(false);
    	centralizarCarta.add(painelCartaConselheiroSecreto);
    	

        cartaConselheiroSecreto.add(centralizarCarta);

        JPanel acoes = criarSecao("AÇÕES DISPONÍVEIS", verde);
        
        acoes.setLayout(new BorderLayout(0,8)); 
        
        JPanel gradeAcoes = new JPanel(new GridLayout(3,2,6,6)); 
        gradeAcoes.setOpaque(false);

        gradeAcoes.add(criarBotao("Pegar tributo"));
        gradeAcoes.add(criarBotao("Construir yurt"));
        gradeAcoes.add(criarBotao("Atacar cidade"));
        gradeAcoes.add(criarBotao("Comprar melhoria"));
        gradeAcoes.add(criarBotao("Entregar tesouros"));
        gradeAcoes.add(criarBotao("Cumprir conselheiros"));
    
        
        JButton terminarTurno = criarBotao("Terminar Turno"); 
        
        acoes.add(gradeAcoes,BorderLayout.CENTER ); 
        acoes.add(terminarTurno, BorderLayout.SOUTH);
        
        
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
	    JPanel cartela = new JPanel(new BorderLayout());
	
	    cartela.setBackground(new Color(214, 190, 145));
	    cartela.setBorder(new CompoundBorder(
	        new LineBorder(dourado, 2),
	        new EmptyBorder(5, 5, 5, 5)
	    ));
	
	    JLabel legenda = new JLabel(
	        "Área reservada para a cartela do jogador",
	        SwingConstants.CENTER
	    );
	
	    legenda.setForeground(this.texto);
	    legenda.setFont(new Font("Serif", Font.PLAIN, 16));
	
	    cartela.add(legenda, BorderLayout.CENTER);
	    cartela.setPreferredSize(new Dimension(500, 300));
	
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
}
