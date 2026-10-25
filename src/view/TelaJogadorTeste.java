package view;

import javax.swing.*;

public class TelaJogadorTeste {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("Herdeiros de Khan - Jogador");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setSize(900, 650);
            janela.setLocationRelativeTo(null);
            janela.setContentPane(new TelaJogador());
            janela.setVisible(true);
        });
    }
}