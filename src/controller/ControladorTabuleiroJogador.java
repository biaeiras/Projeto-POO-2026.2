package controller;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


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

                if (fachada.isEscolhaColunaMelhoriaPendente()) {
                    int destino = painel.getColunaMelhoriaNoPonto(evento.getX(), evento.getY());
                    if (destino >= 0) fachada.escolherColunaMelhoria(destino);
                    atualizarCursor(evento);
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
                int coluna = fachada.isEscolhaColunaMelhoriaPendente()
                    ? painel.getColunaMelhoriaNoPonto(evento.getX(), evento.getY())
                    : painel.getColunaDisponivelNoPonto(evento.getX(), evento.getY());

                painel.setCursor(Cursor.getPredefinedCursor(
                    coluna >= 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR
                ));
            }
        };

        painel.addMouseListener(mouseColunas);
        painel.addMouseMotionListener(mouseColunas);

        final view.PainelPecasBonus pecas = tela.getPainelPecasBonus();
        MouseAdapter mouseBonus = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (evento.getButton() != MouseEvent.BUTTON1 || evento.getClickCount() != 1) return;
                int indice = pecas.getPecaNoPonto(evento.getX(), evento.getY());
                if (indice < 0) return;
                if (!fachada.isEscolhaTesouroBonusPendente() && !fachada.usarPecaBonus(indice)) return;
                if (fachada.isEscolhaTesouroBonusPendente()) {
                    int escolha = ControladorTabuleiroJogador.this.tela.escolherTesouroBonus();
                    if (escolha >= 0) fachada.escolherTesouroBonus(escolha);
                }
            }

            @Override
            public void mouseMoved(MouseEvent evento) {
                pecas.setCursor(Cursor.getPredefinedCursor(pecas.podeUsarNoPonto(evento.getX(), evento.getY())
                        || fachada.isEscolhaTesouroBonusPendente() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent evento) { pecas.setCursor(Cursor.getDefaultCursor()); }
        };
        pecas.addMouseListener(mouseBonus);
        pecas.addMouseMotionListener(mouseBonus);
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
