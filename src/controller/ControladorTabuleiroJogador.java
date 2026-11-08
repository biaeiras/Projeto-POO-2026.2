package controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import java.util.HashMap;
import model.ParadaID;
import model.FachadaJogo;
import model.ObservadoJogo;
import model.ObservadorJogo;
import view.PainelTabuleiroJogador;
import view.TelaJogador;

public class ControladorTabuleiroJogador {

    private final FachadaJogo fachada;
    private final PainelTabuleiroJogador painel;

    private TelaJogador tela;

    // Mantém compatibilidade com os testes existentes.
    public ControladorTabuleiroJogador(
            PainelTabuleiroJogador painel) {

        this.fachada = FachadaJogo.getInstancia();
        this.painel = painel;
    }

    public ControladorTabuleiroJogador(TelaJogador tela) {
        this(tela.getPainelTabuleiroJogador());

        this.tela = tela;

        MouseAdapter mouseColunas = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (evento.getButton() != MouseEvent.BUTTON1
                        || evento.getClickCount() != 1) {
                    return;
                }

                int coluna = painel.getColunaDisponivelNoPonto(
                    evento.getX(), evento.getY()
                );

                if (coluna < 0) {
                    return;
                }

                boolean ativou = fachada.ativarColunaJogadorAtual(coluna);

                if (!ativou) {
                    ControladorTabuleiroJogador.this.tela
                        .mostrarInformacaoTabuleiro(
                            "Não foi possível ativar essa coluna."
                        );
                }

                atualizarCursor(evento);
            }

            @Override
            public void mouseMoved(MouseEvent evento) {
                atualizarCursor(evento);
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                painel.setCursor(Cursor.getDefaultCursor());
            }

            private void atualizarCursor(MouseEvent evento) {
                int coluna = painel.getColunaDisponivelNoPonto(
                    evento.getX(), evento.getY()
                );

                painel.setCursor(Cursor.getPredefinedCursor(
                    coluna >= 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR
                ));
            }
        };

        painel.addMouseListener(mouseColunas);
        painel.addMouseMotionListener(mouseColunas);

        tela.getBotaoTerminarTurno().addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evento) {
                    boolean terminou =
                        fachada.terminarTurnoJogadorAtual();

                    if (!terminou) {
                        ControladorTabuleiroJogador.this.tela
                            .mostrarInformacaoTabuleiro(
                                "Existem pendências no turno."
                            );
                    }
                }
            }
        );

        tela.getBotaoEntregarTesouros().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                ParadaID local = ControladorTabuleiroJogador.this.tela.escolherLocalEntrega();
                if (local == null) return;
                HashMap<String, Integer> entrega =
                        ControladorTabuleiroJogador.this.tela.escolherTesourosEntrega(local);
                if (entrega == null) return;
                boolean entregou = fachada.entregarTesourosJogadorAtual(local, entrega);
                ControladorTabuleiroJogador.this.tela.mostrarInformacaoTabuleiro(entregou
                        ? "Entrega realizada! Votos e peças de bônus atualizados."
                        : "Não foi possível entregar. Confira o conjunto exigido, os tesouros disponíveis e o local visitado.");
            }
        });

        ObservadoJogo observado = registrarObservador(tela);

        // A View consulta o observado para carregar seu estado inicial.
        tela.atualizar(observado);
    }

    public ObservadoJogo registrarObservador(ObservadorJogo observador) {
        fachada.adicionarObservador(observador);
        return fachada;
    }

    // Mantém a entrada utilizada pelos testes visuais existentes.
    public void atualizarTabuleiro() {
        if (tela != null) {
            tela.atualizar(fachada);
        } else {
            painel.atualizarMelhorias(
                TelaJogador.criarMelhoriasVisuais(fachada)
            );
        }
    }

    // Chamar quando a tela for descartada definitivamente.
    public void encerrar() {
        if (tela != null) {
            fachada.removerObservador(tela);
        }
    }
}
