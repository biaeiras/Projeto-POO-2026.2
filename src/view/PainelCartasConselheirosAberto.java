package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class PainelCartasConselheirosAberto extends JPanel {

    private BufferedImage[] imagens = new BufferedImage[4];
    private final HashMap<String, BufferedImage> cache = new HashMap<>();
    private static final HashMap<String, String> ARQUIVOS = criarArquivos();

    private static HashMap<String, String> criarArquivos() {
        HashMap<String, String> arquivos = new HashMap<>();
        arquivos.put("QUATRO_ESPADAS", "4espadas.jpg");
        arquivos.put("SEIS_YURTS", "6yurts.jpg");
        arquivos.put("DEZ_YURTS", "10yurts.jpg");
        arquivos.put("QUATRO_TESOUROS", "4Tesouros.jpg");
        arquivos.put("CINCO_TESOUROS_DIFERENTES", "Primeiro5TesourosDiferentes.jpg");
        arquivos.put("UM_YURT_POR_REGIAO", "1YurtEmCadaRegiao.jpg");
        arquivos.put("DOIS_YURTS_POR_REGIAO", "2YurtsEmCadaRegiao.jpg");
        arquivos.put("DUAS_PECAS_BONUS", "PrimeroConquistar2PecasBonus.jpg");
        arquivos.put("TRES_PECAS_BONUS", "PrimeroConquistar3PecasBonus.jpg");
        arquivos.put("CONQUISTAR_DUAS_CIDADES", "Primeiro2Cidades.jpg");
        arquivos.put("CONQUISTAR_TRES_CIDADES", "Primeiro3Cidades.jpg");
        arquivos.put("LINHA_DE_MELHORIAS", "PrimeiroCompletarLinhaMelhoria.jpg");
        arquivos.put("DUAS_COLUNAS_COM_DUAS_MELHORIAS", "Primero2colunasCom2Melhorias.jpg");
        arquivos.put("QUATRO_MELHORIAS_NA_COLUNA", "Primeiro4MelhoriasEm1Coluna.jpg");
        return arquivos;
    }

    public void definirConselheiros(ArrayList<String> nomes) {
        if (nomes == null || nomes.size() != 4) {
            throw new IllegalArgumentException("Devem ser informadas as quatro posições das cartas.");
        }
        BufferedImage[] cartas = new BufferedImage[4];
        for (int i = 0; i < nomes.size(); i++) {
            String nome = nomes.get(i);
            if (nome != null) {
                String arquivo = ARQUIVOS.get(nome);
                if (arquivo == null) {
                    throw new IllegalArgumentException("Conselheiro desconhecido: " + nome);
                }
                cartas[i] = carregarImagem(arquivo);
            }
        }
        definirCartas(cartas);
    }

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
        if (cache.containsKey(nomeArquivo)) {
            return cache.get(nomeArquivo);
        }
        java.net.URL recurso = getClass().getResource("/" + nomeArquivo);
        if (recurso == null) {
            throw new IllegalArgumentException("Imagem não encontrada: " + nomeArquivo);
        }
        try {
            BufferedImage imagem = ImageIO.read(recurso);
            if (imagem == null) {
                throw new IllegalStateException("Imagem inválida: " + nomeArquivo);
            }
            cache.put(nomeArquivo, imagem);
            return imagem;
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar carta: " + nomeArquivo, e);
        }
    }
}
