package view;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import controller.ControladorTabuleiroJogador;

public class TesteTabuleiroJogador {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                JFrame janela =
                        new JFrame("Teste - Tabuleiro Jogador");

                PainelTabuleiroJogador painel =
                        new PainelTabuleiroJogador("Altani");

                ControladorTabuleiroJogador controlador =
                        new ControladorTabuleiroJogador(painel);

                // Só chamar depois de existir uma partida
                // inicializada na FachadaJogo.
                // controlador.atualizarTabuleiro();

                janela.add(painel);
                janela.pack();

                janela.setDefaultCloseOperation(
                        JFrame.EXIT_ON_CLOSE
                );

                janela.setLocationRelativeTo(null);
                janela.setVisible(true);
            }
        });
    }
}