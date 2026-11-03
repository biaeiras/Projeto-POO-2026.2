package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import model.Cor;
import model.FachadaJogo;
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

    public ControllerAPI() {
        fachada = FachadaJogo.getInstancia();
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
            abrirTelaJogador();
        } else {
            preparacao.mostrarVezDeEscolher(
                fachada.getNomePersonagemAtual()
            );
        }
    }

    private void abrirTelaJogador() {
        TelaJogador tela = new TelaJogador();

        controladorJogador =
            new ControladorTabuleiroJogador(tela);

        JFrame janela =
            new JFrame("Herdeiros de Khan - Jogador");

        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setContentPane(tela);
        janela.setSize(1200, 800);
        janela.setLocationRelativeTo(null);

        janela.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                controladorJogador.encerrar();
            }
        });

        preparacao.dispose();
        janela.setVisible(true);
    }
}