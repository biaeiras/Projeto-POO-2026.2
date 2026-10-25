package model;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import controller.ControladorTabuleiroJogador;
import view.PainelTabuleiroJogador;

/** Cenário de teste; não altera as regras nem a API do jogo. */
public class TesteVisualTabuleiroJogador {

    static Partida prepararPartida() {
        Partida partida = new Partida(new ArrayList<>(Arrays.asList(Cor.VERDE, Cor.VERMELHO)));
        TabuleiroJogador tabuleiro = partida.getJogadorAtual().getTabuleiroJogador();

        if (!tabuleiro.adicionarMelhoria(Regiao.CHINA,
                    new Melhoria(TipoMelhoria.FERRO_VIRTUAL, Regiao.CHINA))
                || !tabuleiro.adicionarMelhoria(Regiao.PERSIA,
                    new Melhoria(TipoMelhoria.TECIDO_VIRTUAL, Regiao.PERSIA))
                || !tabuleiro.adicionarMelhoria(Regiao.RUSSIA,
                    new Melhoria(TipoMelhoria.BOI_VIRTUAL, Regiao.RUSSIA))
                || !tabuleiro.adicionarMelhoriaBranca(
                    new Melhoria(TipoMelhoria.MOEDA_VIRTUAL, Regiao.CHINA))) {
            throw new IllegalStateException("Não foi possível preparar as melhorias do teste.");
        }
        return partida;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FachadaJogo.getInstancia().definirPartida(prepararPartida());
            PainelTabuleiroJogador painel = new PainelTabuleiroJogador("Altani");
            new ControladorTabuleiroJogador(painel).atualizarTabuleiro();

            JFrame janela = new JFrame("Teste visual - melhorias nas quatro colunas");
            janela.add(new JLabel("Esperado, da esquerda para a direita: China = ferro | "
                    + "Pérsia = tecido | Rússia = boi | Branca = moeda"), BorderLayout.NORTH);
            janela.add(painel, BorderLayout.CENTER);
            janela.pack();
            janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
}
