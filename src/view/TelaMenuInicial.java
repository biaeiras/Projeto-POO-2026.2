package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TelaMenuInicial extends JFrame {

    private final Color dourado = new Color(190, 145, 68);
    private final Color pergaminho = new Color(245, 224, 177);
    private final Color texto = new Color(75, 43, 25);

    public TelaMenuInicial() {
        setTitle("Herdeiros do Khan");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);

        setContentPane(criarTela());
    }

    private JPanel criarTela() {
        PainelFundo fundo = new PainelFundo();
        fundo.setLayout(new BorderLayout());

        JPanel areaBotoes = new JPanel();
        areaBotoes.setOpaque(false);
        areaBotoes.setLayout(new BoxLayout(
            areaBotoes, BoxLayout.Y_AXIS
        ));

        // Espaço acima dos botões
        areaBotoes.add(Box.createVerticalGlue());

        JPanel moldura = new JPanel();
        moldura.setLayout(new BoxLayout(
            moldura, BoxLayout.Y_AXIS
        ));
        moldura.setBackground(new Color(45, 28, 22, 235));
        moldura.setBorder(new CompoundBorder(
            new LineBorder(dourado, 3),
            new EmptyBorder(14, 28, 14, 28)
        ));
        moldura.setAlignmentX(Component.CENTER_ALIGNMENT);
        moldura.setMaximumSize(new Dimension(330, 220));

        JButton iniciar = criarBotao("INICIAR PARTIDA");
        JButton comoJogar = criarBotao("COMO JOGAR");
        JButton sair = criarBotao("SAIR");

        moldura.add(iniciar);
        moldura.add(Box.createVerticalStrut(9));
        moldura.add(comoJogar);
        moldura.add(Box.createVerticalStrut(9));
        moldura.add(sair);

        areaBotoes.add(moldura);
        areaBotoes.add(Box.createVerticalStrut(25));

        fundo.add(areaBotoes, BorderLayout.CENTER);

        // Ações provisórias dos botões
        iniciar.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this,
                "Aqui vamos abrir a configuração da partida."
            );
        });

        comoJogar.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this,
                "Aqui vamos apresentar as regras do jogo."
            );
        });

        sair.addActionListener(e -> dispose());

        return fundo;
    }

    private JButton criarBotao(String titulo) {
        JButton botao = new JButton(titulo);

        botao.setFont(new Font("Serif", Font.BOLD, 17));
        botao.setForeground(texto);
        botao.setBackground(pergaminho);
        botao.setBorder(new CompoundBorder(
            new LineBorder(dourado, 2),
            new EmptyBorder(10, 20, 10, 20)
        ));
        botao.setFocusPainted(false);
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setMaximumSize(new Dimension(270, 48));
        botao.setPreferredSize(new Dimension(270, 48));
        botao.setCursor(Cursor.getPredefinedCursor(
            Cursor.HAND_CURSOR
        ));

        return botao;
    }

    private class PainelFundo extends JPanel {

        private BufferedImage imagem;

        public PainelFundo() {
            try {
                imagem = ImageIO.read(
                    getClass().getResource(
                        "/herdeirosDoKhan.jpg"
                    )
                );
            } catch (Exception e) {
                System.err.println(
                    "Não foi possível carregar a imagem de fundo."
                );
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            if (imagem != null) {
                g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
                );

                g2.drawImage(
                    imagem, 0, 0,
                    getWidth(), getHeight(), this
                );
            } else {
                g2.setColor(new Color(30, 75, 100));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	TelaMenuInicial menu = new TelaMenuInicial();
            menu.setVisible(true);
        });
    }
}
