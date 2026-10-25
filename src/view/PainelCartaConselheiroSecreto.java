package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class PainelCartaConselheiroSecreto extends JPanel {

    private BufferedImage imagemCarta;

    public PainelCartaConselheiroSecreto() {
        setOpaque(false);
        setPreferredSize(new Dimension(110, 150));
        setMinimumSize(new Dimension(110, 150));
        setMaximumSize(new Dimension(110, 150));
    }

    // Chamado quando o Controller informar qual carta mostrar
    public void definirCarta(BufferedImage carta) {
        this.imagemCarta = carta;
        repaint();
    }

    // Permite carregar uma carta a partir do arquivo de imagem
    public void carregarCarta(String nomeArquivo) {
        try {
            imagemCarta = ImageIO.read(
                getClass().getResource("/" + nomeArquivo)
            );
        } catch (Exception e) {
            System.err.println(
                "Não foi possível carregar a carta: " + nomeArquivo
            );
            imagemCarta = null;
        }

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
            int largura = getWidth() - 2 * margem;
            int altura = getHeight() - 2 * margem;

            g2.setColor(new Color(250, 237, 203));
            g2.fillRect(margem, margem, largura, altura);

            g2.setColor(new Color(190, 145, 68));
            g2.drawRect(margem, margem, largura, altura);

            if (imagemCarta != null) {
                g2.drawImage(
                    imagemCarta,
                    margem + 2,
                    margem + 2,
                    largura - 4,
                    altura - 4,
                    this
                );
            } else {
                g2.setColor(new Color(75, 51, 30));
                g2.drawString(
                    "Carta escolhida",
                    margem + 20,
                    getHeight() / 2
                );
            }
        } finally {
            g2.dispose();
        }
    }
    
    public void carregarCartaInicial() {
    	carregarCarta("cidadao.jpg");
 
    }
    
  
}

