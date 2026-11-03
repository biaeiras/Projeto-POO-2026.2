package controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import model.FachadaJogo;
import model.ObservadoJogo;
import model.ObservadorJogo;
import view.PainelTabuleiroJogador;
import view.PainelTabuleiroJogador.MelhoriaVisual;
import view.TelaJogador;

public class ControladorTabuleiroJogador
        implements ObservadorJogo {

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

        fachada.adicionarObservador(this);

        // Carrega o estado existente ao abrir a tela.
        atualizar(fachada);
    }

    @Override
    public void atualizar(ObservadoJogo observado) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    atualizarTela();
                }
            });

            return;
        }

        atualizarTela();
    }

    private void atualizarTela() {
        if (tela == null) {
            return;
        }

        if (!fachada.existePartida()) {
            painel.definirPersonagem(null);
            painel.definirCorFicha(null);
            painel.atualizarMelhorias(
                new ArrayList<MelhoriaVisual>()
            );

            tela.atualizarEstadoColunas(
                new boolean[4], false, false
            );

            tela.atualizarRecursos(0, 0, 0, 0);
            tela.mostrarInformacaoTabuleiro(
                "Aguardando início da partida."
            );

            return;
        }

        painel.definirPersonagem(
            fachada.getNomePersonagemAtual()
        );
        painel.definirCorFicha(fachada.getCorJogadorAtual());

        atualizarTabuleiro();

        boolean[] usadas = new boolean[4];

        for (int coluna = 0; coluna < usadas.length; coluna++) {
            usadas[coluna] =
                fachada.isColunaJogadorAtualAtivada(coluna);
        }

        boolean turnoAberto = fachada.isTurnoEmAndamento();

        tela.atualizarEstadoColunas(
            usadas,
            fachada.podeEscolherColuna(),
            turnoAberto && !fachada.isPartidaEncerrada()
        );

        tela.atualizarRecursos(
            fachada.getMoedasJogadorAtual(),
            fachada.getEspadasJogadorAtual(),
            fachada.getYurtsEstoqueJogadorAtual(),
            fachada.getTributosYurtJogadorAtual()
        );

        String situacao;

        if (fachada.isPartidaEncerrada()) {
            situacao = "Partida encerrada.";
        } else if (turnoAberto) {
            situacao = "Turno em andamento.";
        } else if (fachada.podeEscolherColuna()) {
            situacao = "Clique no retângulo inferior de uma coluna.";
        } else {
            situacao = "Conclua a escolha dos conselheiros.";
        }

        tela.mostrarInformacaoTabuleiro(
            fachada.getNomePersonagemAtual()
            + " | Fichas: "
            + fachada.getFichasAtivacaoJogadorAtual()
            + " | "
            + situacao
        );
    }

    public void atualizarTabuleiro() {
        ArrayList<MelhoriaVisual> melhorias =
            new ArrayList<>();

        for (int coluna = 0; coluna < 4; coluna++) {
            ArrayList<String> lista =
                fachada.getMelhoriasTabuleiroJogador(coluna);

            for (int linha = 0; linha < lista.size(); linha++) {
                melhorias.add(new MelhoriaVisual(
                    lista.get(linha), coluna, linha
                ));
            }
        }

        painel.atualizarMelhorias(melhorias);
    }

    // Chamar quando a tela for descartada definitivamente.
    public void encerrar() {
        fachada.removerObservador(this);
    }
}
