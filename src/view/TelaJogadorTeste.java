package view;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import controller.ControladorTabuleiroJogador;

public class TelaJogadorTeste {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame janela =
                    new JFrame("Herdeiros de Khan - Jogador");

                janela.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
                );
                janela.setSize(900, 650);
                janela.setLocationRelativeTo(null);

                TelaJogador tela = new TelaJogador();

                ControladorTabuleiroJogador controlador =
                    new ControladorTabuleiroJogador(tela);

                janela.setContentPane(tela);
                janela.setVisible(true);
            }
        });
    }
}