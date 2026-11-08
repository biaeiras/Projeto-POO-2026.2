package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Cor;
import model.FachadaJogo;
import model.ObservadoJogo;
import model.ObservadorJogo;
import view.TelaJogador;
import view.TelaMenuInicial;
import view.TelaPreparacaoPartida;
import view.ViewAPI;

public class ControllerAPI {

    private final FachadaJogo fachada;

    private MouseTabuleiro mouseTabuleiro;
    private TelaMenuInicial menu;
    private TelaPreparacaoPartida preparacao;

    private ControladorTabuleiroJogador controladorJogador;
    private ViewAPI viewPrincipal;
    private JFrame janelaJogador;

    public ControllerAPI() {
        this(FachadaJogo.getInstancia());
    }

    public ControllerAPI(FachadaJogo fachada) {
        this.fachada = fachada;
    }

    public ObservadoJogo registrarObservador(ObservadorJogo observador) {
        fachada.adicionarObservador(observador);
        return fachada;
    }

    public void removerObservador(ObservadorJogo observador) {
        fachada.removerObservador(observador);
    }

    public ControllerAPI(ViewAPI viewAPI, FachadaJogo fachada) {
        this.fachada = fachada;

        mouseTabuleiro = new MouseTabuleiro(viewAPI, fachada);

        viewAPI.getTabulerioPanel()
               .addMouseListener(mouseTabuleiro);
    }

    public void abrirMenu() {
        menu = new TelaMenuInicial();

        menu.getBotaoIniciar().addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evento) {
                    abrirPreparacao();
                }
            }
        );

        menu.setVisible(true);
    }

    private void abrirPreparacao() {
        preparacao = new TelaPreparacaoPartida();

        for (Cor cor : Cor.values()) {
            final Cor escolhida = cor;
            final JPanel cartela = preparacao.getCartelaPersonagem(cor);
            cartela.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent evento) {
                    preparacao.destacarFocoTeclado(escolhida, false);
                }

                @Override
                public void mouseClicked(MouseEvent evento) {
                    if (evento.getButton() == MouseEvent.BUTTON1) {
                        preparacao.destacarFocoTeclado(escolhida, false);
                        cartela.requestFocusInWindow();
                        preparacao.alternarPersonagem(escolhida);
                    }
                }
            });
            cartela.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evento) {
                    if (evento.getKeyCode() == KeyEvent.VK_SPACE
                            || evento.getKeyCode() == KeyEvent.VK_ENTER) {
                        preparacao.destacarFocoTeclado(escolhida, true);
                        preparacao.alternarPersonagem(escolhida);
                        evento.consume();
                    }
                }
            });
            cartela.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent evento) {
                    FocusEvent.Cause causa = evento.getCause();
                    preparacao.destacarFocoTeclado(escolhida,
                        causa == FocusEvent.Cause.TRAVERSAL
                        || causa == FocusEvent.Cause.TRAVERSAL_FORWARD
                        || causa == FocusEvent.Cause.TRAVERSAL_BACKWARD
                        || causa == FocusEvent.Cause.TRAVERSAL_UP
                        || causa == FocusEvent.Cause.TRAVERSAL_DOWN);
                }
                @Override
                public void focusLost(FocusEvent evento) {
                    preparacao.destacarFocoTeclado(escolhida, false);
                }
            });
        }

        preparacao.getBotaoConfirmarPersonagens()
            .addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evento) {
                    confirmarPersonagens();
                }
            });

        preparacao.getBotaoContinuar().addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evento) {
                    mostrarOpcoesConselheiro();
                }
            }
        );

        preparacao.getPrimeiraCarta().setCursor(
        	    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        	);

        	preparacao.getSegundaCarta().setCursor(
        	    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        	);

        	preparacao.getPrimeiraCarta().addMouseListener(
        	    new MouseAdapter() {
        	        @Override
        	        public void mouseClicked(MouseEvent evento) {
        	            if (evento.getButton() == MouseEvent.BUTTON1
        	                    && evento.getClickCount() == 1) {
        	                escolherConselheiro(0);
        	            }
        	        }
        	    }
        	);

        	preparacao.getSegundaCarta().addMouseListener(
        	    new MouseAdapter() {
        	        @Override
        	        public void mouseClicked(MouseEvent evento) {
        	            if (evento.getButton() == MouseEvent.BUTTON1
        	                    && evento.getClickCount() == 1) {
        	                escolherConselheiro(1);
        	            }
        	        }
        	    }
        	);

        menu.dispose();
        preparacao.setVisible(true);
    }

    private void confirmarPersonagens() {
        ArrayList<Cor> cores =
            preparacao.getCoresEscolhidas();

        if (cores.size() < 2 || cores.size() > 5) {
            preparacao.mostrarMensagem(
                "Selecione de 2 a 5 personagens."
            );

            return;
        }

        fachada.iniciarPartida(cores);

        preparacao.mostrarVezDeEscolher(
            fachada.getNomePersonagemAtual()
        );
    }

    private void mostrarOpcoesConselheiro() {
        preparacao.mostrarConselheiros(
            fachada.getNomePersonagemAtual(),
            fachada.getOpcoesConselheiroJogadorAtual()
        );
    }

    private void escolherConselheiro(int indice) {
        boolean escolheu =
            fachada.escolherConselheiroJogadorAtual(indice);

        if (!escolheu) {
            preparacao.mostrarMensagem(
                "Não foi possível escolher esse conselheiro."
            );

            return;
        }

        // Também avança depois da última escolha.
        // Assim, o primeiro jogador volta a ser o atual.
        fachada.avancarJogadorNaPreparacao();

        if (fachada.todosEscolheramConselheiro()) {
            abrirJanelasDaPartida();
        } else {
            preparacao.mostrarVezDeEscolher(
                fachada.getNomePersonagemAtual()
            );
        }
    }

    private void abrirJanelasDaPartida() {
        viewPrincipal = new ViewAPI(this);
        mouseTabuleiro = new MouseTabuleiro(viewPrincipal, fachada);
        viewPrincipal.getTabulerioPanel().addMouseListener(mouseTabuleiro);

        viewPrincipal.getTelaPrincipal().getBotaoJogador().addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evento) {
                    mostrarJanelaJogador();
                }
            }
        );

        viewPrincipal.getTelaPrincipal().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                if (controladorJogador != null) {
                    controladorJogador.encerrar();
                }
                if (janelaJogador != null) {
                    janelaJogador.dispose();
                }
                viewPrincipal.encerrar();
            }
        });

        preparacao.dispose();
        viewPrincipal.exibir();
        mostrarJanelaJogador();
    }

    private void mostrarJanelaJogador() {
        if (janelaJogador == null) {
            TelaJogador tela = new TelaJogador();
            controladorJogador = new ControladorTabuleiroJogador(tela);

            janelaJogador = new JFrame("Herdeiros de Khan - Jogador");
            janelaJogador.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            janelaJogador.setContentPane(tela);
            janelaJogador.setSize(1200, 800);
            janelaJogador.setLocationRelativeTo(viewPrincipal.getTelaPrincipal());
        }

        janelaJogador.setVisible(true);
        janelaJogador.setState(JFrame.NORMAL);
        janelaJogador.toFront();
    }
}
