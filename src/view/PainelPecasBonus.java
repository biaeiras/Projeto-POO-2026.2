package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class PainelPecasBonus extends JPanel {

    private final BufferedImage[] imagens = new BufferedImage[7];
    private final boolean[] conquistadas = new boolean[7];
    private final boolean[] usadas = new boolean[7];

    private int quantidadeConquistada = 0;

    public PainelPecasBonus() {
        setOpaque(false);
        setPreferredSize(new Dimension(250, 115));

        carregarImagensPecas();
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

    public void carregarImagensPecas() {
        imagens[0] = carregarImagem("TesourosAleatorios.jpg");
        imagens[1] = carregarImagem("1VotoExtraTrilha.jpg");
        imagens[2] = carregarImagem("2PecasTributoEspada.jpg");
        imagens[3] = carregarImagem("2PecasTributoYurt.jpg");
        imagens[4] = carregarImagem("2TributoMoeda.jpg");
        imagens[5] = carregarImagem("2ExtrasPegarTributos.jpg");
        imagens[6] = carregarImagem("3MovimentosE1AcaoExtras.jpg");

        repaint();
    }

    public void atualizarPecas(
            boolean[] pecasConquistadas,
            boolean[] pecasUsadas) {

        if (pecasConquistadas == null || pecasConquistadas.length != 7
                || pecasUsadas == null || pecasUsadas.length != 7) {
            throw new IllegalArgumentException(
                "Devem ser informadas exatamente 7 peças."
            );
        }

        System.arraycopy(
            pecasConquistadas, 0, conquistadas, 0, 7
        );

        System.arraycopy(
            pecasUsadas, 0, usadas, 0, 7
        );

        quantidadeConquistada = 0;

        for (boolean conquistada : conquistadas) {
            if (conquistada) {
                quantidadeConquistada++;
            }
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

            int margem = 3;
            int espacamento = 4;
            int larguraContador = 42;

            int larguraPeca = (
                getWidth() - 2 * margem
                - 3 * espacamento - larguraContador
            ) / 4;

            int alturaPeca = (
                getHeight() - 2 * margem - espacamento
            ) / 2;

            for (int i = 0; i < 7; i++) {

                int coluna = i % 4;
                int linha = i / 4;

                int x = margem
                    + coluna * (larguraPeca + espacamento);

                int y = margem
                    + linha * (alturaPeca + espacamento);

                // Na segunda linha, as peças ficam à esquerda.
                if (i >= 4) {
                    x = margem
                        + (i - 4) * (larguraPeca + espacamento);
                }

                // Fundo do espaço
                g2.setColor(new Color(238, 225, 204));
                g2.fillRect(x, y, larguraPeca, alturaPeca);

                // Borda do espaço
                g2.setColor(new Color(190, 145, 68));
                g2.drawRect(x, y, larguraPeca, alturaPeca);

                // Mostrar somente peças conquistadas
                if (conquistadas[i] && imagens[i] != null) {

                    g2.drawImage(
                        imagens[i],
                        x + 1,
                        y + 1,
                        larguraPeca - 2,
                        alturaPeca - 2,
                        this
                    );

                    // Peça usada continua visível, mas esmaecida
                    if (usadas[i]) {
                        g2.setColor(
                            new Color(80, 80, 80, 140)
                        );

                        g2.fillRect(
                            x + 1,
                            y + 1,
                            larguraPeca - 2,
                            alturaPeca - 2
                        );
                    }
                } else {
                    // Espaço vazio: borda tracejada
                    g2.setColor(new Color(160, 140, 110));
                    g2.setStroke(
                        new BasicStroke(
                            1,
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER,
                            10,
                            new float[] {3, 3},
                            0
                        )
                    );

                    g2.drawRect(
                        x + 1,
                        y + 1,
                        larguraPeca - 2,
                        alturaPeca - 2
                    );

                    g2.setStroke(new BasicStroke(1));
                }
            }

            // Contador de peças conquistadas
            int xContador = getWidth() - larguraContador - margem;
            int yContador = getHeight() - alturaPeca - margem;

            g2.setColor(new Color(250, 245, 226));
            g2.fillRect(
                xContador, yContador,
                larguraContador, alturaPeca
            );

            g2.setColor(new Color(190, 145, 68));
            g2.drawRect(
                xContador, yContador,
                larguraContador, alturaPeca
            );

            g2.setColor(new Color(75, 51, 30));
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));

            String contador = quantidadeConquistada + " / 7";

            FontMetrics fm = g2.getFontMetrics();

            int textoX = xContador
                + (larguraContador - fm.stringWidth(contador)) / 2;

            int textoY = yContador
                + (alturaPeca - fm.getHeight()) / 2
                + fm.getAscent();

            g2.drawString(contador, textoX, textoY);

        } finally {
            g2.dispose();
        }
    }
}
