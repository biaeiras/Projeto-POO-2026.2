package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;


public class PainelCartasConselheirosAberto extends JPanel {

    private BufferedImage[] imagens = new BufferedImage[4];

    public PainelCartasConselheirosAberto() {
        setOpaque(false);
        setPreferredSize(new Dimension(250, 85));
    }

    public void definirCartas(BufferedImage[] cartas) {
        if (cartas == null || cartas.length != 4) {
            throw new IllegalArgumentException(
                "Devem ser fornecidas exatamente 4 cartas."
            );
        }

        imagens = cartas.clone();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );

            int margem = 4;
            int espacamento = 5;
            int larguraCarta =
                (getWidth() - 2 * margem - 3 * espacamento) / 4;
            int alturaCarta = getHeight() - 2 * margem;

            for (int i = 0; i < 4; i++) {
                int x = margem + i * (larguraCarta + espacamento);
                int y = margem;

                g2.setColor(new Color(250, 237, 203));
                g2.fillRect(x, y, larguraCarta, alturaCarta);

                g2.setColor(new Color(190, 145, 68));
                g2.drawRect(x, y, larguraCarta, alturaCarta);

                if (imagens[i] != null) {
                    g2.drawImage(
                        imagens[i],
                        x + 2,
                        y + 2,
                        larguraCarta - 4,
                        alturaCarta - 4,
                        this
                    );
                }
            }
        } finally {
            g2.dispose();
        }
    }
    
    private BufferedImage carregarImagem(String nomeArquivo) {
        try {
            return ImageIO.read(
                getClass().getResource("/" + nomeArquivo)
            );
        } catch (Exception e) {
            System.err.println(
                "Não foi possível carregar: " + nomeArquivo
            );
            return null;
        }
    }
    
    //Visualizar somente enquanto não tem Controller
    public void carregarCartasIniciais() {
        BufferedImage[] cartas = {
            carregarImagem("4espadas.jpg"),
            carregarImagem("6yurts.jpg"),
            carregarImagem("10yurts.jpg"),
            carregarImagem("4Tesouros.jpg")
        };

        definirCartas(cartas);
    }
}
