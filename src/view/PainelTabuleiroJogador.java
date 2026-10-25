package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class PainelTabuleiroJogador extends JPanel {

    private Image imagemTabuleiro;
    private HashMap<String, BufferedImage> imagensMelhorias = new HashMap<>();
    private ArrayList<MelhoriaVisual> melhoriasVisuais = new ArrayList<>();
    
 // Coordenadas relativas à imagem original
    private static final double[] COLUNAS_X = {
    	    0.335,  // China
    	    0.510,  // Pérsia
    	    0.685,  // Rússia
    	    0.885   // Branca
    	};

    private static final double[] LINHAS_Y = {
    	    0.650,  // Primeira melhoria (inferior)
    	    0.485,  // Segunda melhoria
    	    0.355,  // Terceira melhoria
    	    0.195   // Quarta melhoria (superior)
    	};

    public PainelTabuleiroJogador(String nomePersonagem) {

        String caminho = "/tabuleiros/"
                + nomePersonagem.toLowerCase() + ".jpg";

        java.net.URL recurso = getClass().getResource(caminho);

        if (recurso == null) {
            throw new IllegalArgumentException(
                    "Imagem não encontrada: " + caminho
            );
        }

        imagemTabuleiro = new ImageIcon(recurso).getImage();

        setPreferredSize(new Dimension(800, 450));
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        // Melhora a qualidade da imagem ao redimensionar
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        // Desenha o tabuleiro utilizando Java2D
        g2d.drawImage(
                imagemTabuleiro,
                0,
                0,
                getWidth(),
                getHeight(),
                this
        );
        
        for (MelhoriaVisual melhoria : melhoriasVisuais) {

            desenharMelhoria(
                    g2d,
                    melhoria.nomeArquivo,
                    melhoria.coluna,
                    melhoria.linha
            );
        }

        g2d.dispose();
    }
    
    private int[] calcularPosicao(int coluna, int linha) {

        int x = (int) (getWidth() * COLUNAS_X[coluna]);
        int y = (int) (getHeight() * LINHAS_Y[linha]);

        return new int[] {x, y};
    }
    
    private BufferedImage carregarMelhoria(String nomeArquivo) {

        if (imagensMelhorias.containsKey(nomeArquivo)) {
            return imagensMelhorias.get(nomeArquivo);
        }

        String caminho = "/melhorias/" + nomeArquivo;

        java.net.URL recurso = getClass().getResource(caminho);

        if (recurso == null) {
            throw new IllegalArgumentException(
                    "Imagem não encontrada: " + caminho
            );
        }

        try {
            BufferedImage imagem = ImageIO.read(recurso);

            imagensMelhorias.put(nomeArquivo, imagem);

            return imagem;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao carregar melhoria: " + caminho, e
            );
        }
    }
    
    private void desenharMelhoria(
            Graphics2D g2d,
            String nomeMelhoria,
            int coluna,
            int linha
    ) {

        BufferedImage imagem = carregarMelhoria(nomeMelhoria);

        int x = (int) (COLUNAS_X[coluna] * getWidth());
        int y = (int) (LINHAS_Y[linha] * getHeight());

        // Tamanho da melhoria proporcional ao painel
        int largura = (int) (getWidth() * 0.145);
        int altura = (int) (getHeight() * 0.135);

        // Centraliza a imagem na posição
        x -= largura / 2;
        y -= altura / 2;

        g2d.drawImage(
                imagem,
                x,
                y,
                largura,
                altura,
                this
        );
    }
    
    public void atualizarMelhorias(ArrayList<MelhoriaVisual> melhorias) {

        this.melhoriasVisuais 
        = new ArrayList<>(melhorias);

        repaint();
    }
    
    public static class MelhoriaVisual {

        private final String nomeArquivo;
        private final int coluna;
        private final int linha;

        public MelhoriaVisual(String nomeArquivo, int coluna, int linha) {
            this.nomeArquivo = nomeArquivo;
            this.coluna = coluna;
            this.linha = linha;
        }
    }
}
