package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import model.Cor;

public class PainelTabuleiroJogador extends JPanel {

    private Image imagemTabuleiro;
    private HashMap<String, BufferedImage> imagensMelhorias = new HashMap<>();
    private ArrayList<MelhoriaVisual> melhoriasVisuais = new ArrayList<>();
    
    private boolean[] colunasUsadas = new boolean[4];
    private boolean podeEscolherColuna;

    private BufferedImage imagemFicha;
    private Cor corFicha;

    private final HashMap<Cor, BufferedImage> imagensFichas =
        new HashMap<>();
    
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
        setPreferredSize(new Dimension(800, 450));

        if (nomePersonagem != null) {
            definirPersonagem(nomePersonagem);
        }
    }

    private String personagemExibido;

    public void definirPersonagem(String nomePersonagem) {
        if (nomePersonagem == null) {
            personagemExibido = null;
            imagemTabuleiro = null;
            repaint();
            return;
        }

        if (nomePersonagem.equals(personagemExibido)) {
            return;
        }

        String caminho = "/tabuleiros/"
                + nomePersonagem.toLowerCase(java.util.Locale.ROOT)
                + ".jpg";

        java.net.URL recurso = getClass().getResource(caminho);

        if (recurso == null) {
            throw new IllegalArgumentException(
                "Imagem não encontrada: " + caminho
            );
        }

        imagemTabuleiro = new ImageIcon(recurso).getImage();
        setPreferredSize(new Dimension(800,
                (int) Math.round(800.0 * imagemTabuleiro.getHeight(this) / imagemTabuleiro.getWidth(this))));
        revalidate();
        personagemExibido = nomePersonagem;
        repaint();
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
        
        desenharFichas(g2d);

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
    
 // Usa coordenadas relativas para acompanhar o redimensionamento.
    private Rectangle getRetanguloAtivacao(int coluna) {
        int esquerda = (int) (
            (COLUNAS_X[coluna] - 0.075) * getWidth()
        );

        int direita = (int) (
            (COLUNAS_X[coluna] + 0.075) * getWidth()
        );

        int topo = (int) (0.77 * getHeight());
        int base = (int) (0.91 * getHeight());

        return new Rectangle(
            esquerda,
            topo,
            direita - esquerda,
            base - topo
        );
    }

    // Retorna -1 quando o clique não corresponde a uma coluna disponível.
    public int getColunaDisponivelNoPonto(int x, int y) {
        if (imagemTabuleiro == null || !podeEscolherColuna) {
            return -1;
        }

        for (int coluna = 0; coluna < colunasUsadas.length; coluna++) {
            if (!colunasUsadas[coluna]
                    && getRetanguloAtivacao(coluna).contains(x, y)) {
                return coluna;
            }
        }

        return -1;
    }

    public void atualizarEstadoColunas(
            boolean[] usadas, boolean podeEscolher) {

        if (usadas == null || usadas.length != 4) {
            throw new IllegalArgumentException(
                "Devem ser informadas as quatro colunas."
            );
        }

        colunasUsadas = usadas.clone();
        podeEscolherColuna = podeEscolher;

        repaint();
    }

    public void definirCorFicha(Cor cor) {
        if (cor == null) {
            corFicha = null;
            imagemFicha = null;
            repaint();
            return;
        }

        if (cor == corFicha) {
            return;
        }

        BufferedImage imagem = imagensFichas.get(cor);

        if (imagem == null) {
            String nomeArquivo;

            switch (cor) {
                case AZUL:
                    nomeArquivo = "cartoes4-1.jpg";
                    break;

                case AMARELO:
                    nomeArquivo = "cartoes4-2.jpg";
                    break;

                case CINZA:
                    nomeArquivo = "cartoes4-3.jpg";
                    break;

                case VERMELHO:
                    nomeArquivo = "cartoes4-4.jpg";
                    break;

                case VERDE:
                    nomeArquivo = "cartoes4-5.jpg";
                    break;

                default:
                    throw new IllegalArgumentException(
                        "Cor de ficha inválida."
                    );
            }

            String caminho = "/fichas/" + nomeArquivo;
            java.net.URL recurso = getClass().getResource(caminho);

            if (recurso == null) {
                throw new IllegalArgumentException(
                    "Imagem não encontrada: " + caminho
                );
            }

            try {
                imagem = ImageIO.read(recurso);

                if (imagem == null) {
                    throw new IllegalStateException(
                        "Formato de imagem inválido: " + caminho
                    );
                }

                imagensFichas.put(cor, imagem);
            } catch (IOException e) {
                throw new IllegalStateException(
                    "Erro ao carregar ficha: " + caminho, e
                );
            }
        }

        imagemFicha = imagem;
        corFicha = cor;
        repaint();
    }

    private void desenharFichas(Graphics2D g2d) {
        if (imagemTabuleiro == null || imagemFicha == null) {
            return;
        }

        for (int coluna = 0; coluna < colunasUsadas.length; coluna++) {
            if (!colunasUsadas[coluna]) {
                continue;
            }

            Rectangle area = getRetanguloAtivacao(coluna);

            int tamanho = Math.min(area.width, area.height) * 3 / 4;

            if (tamanho <= 0) {
                continue;
            }

            int x = area.x + (area.width - tamanho) / 2;
            int y = area.y + (area.height - tamanho) / 2;

            Graphics2D ficha = (Graphics2D) g2d.create();

            try {
                // Recorta os cantos do JPG para exibir a ficha circular.
                ficha.clip(new Ellipse2D.Double(
                    x, y, tamanho, tamanho
                ));

                ficha.drawImage(
                    imagemFicha,
                    x, y, tamanho, tamanho,
                    this
                );
            } finally {
                ficha.dispose();
            }
        }
    }
}
