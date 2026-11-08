package view;

import static org.junit.Assert.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import org.junit.Test;
import controller.ControladorTabuleiroJogador;
import model.Cor;
import model.FachadaJogo;

public class LayoutResponsivoTest {
    private Component encontrar(Container raiz, String nome) {
        for (Component c : raiz.getComponents()) {
            if (nome.equals(c.getName())) return c;
            if (c instanceof Container) {
                Component resultado = encontrar((Container) c, nome);
                if (resultado != null) return resultado;
            }
        }
        return null;
    }

    private void organizar(Container raiz) {
        raiz.doLayout();
        for (Component c : raiz.getComponents()) if (c instanceof Container) organizar((Container) c);
    }

    @Test
    public void quatroPaineisDoJogadorFicamVisiveisLadoALadoEmTresTamanhos() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.iniciarPartida(new ArrayList<>(Arrays.asList(Cor.VERMELHO, Cor.CINZA)));
                for (int i = 0; i < 2; i++) {
                    assertTrue(fachada.escolherConselheiroJogadorAtual(0));
                    fachada.avancarJogadorNaPreparacao();
                }
                TelaJogador tela = new TelaJogador();
                ControladorTabuleiroJogador controlador = new ControladorTabuleiroJogador(tela);
                assertTrue(fachada.ativarColunaJogadorAtual(1));
                try {
                    for (Dimension tamanho : new Dimension[] {
                            new Dimension(900, 600), new Dimension(1200, 800), new Dimension(1600, 1000)}) {
                        tela.setSize(tamanho);
                        for (int i = 0; i < 3; i++) organizar(tela);
                        Rectangle anterior = null;
                        for (String nome : new String[] {"painelBonus", "painelRecursos", "painelTesouros", "painelConselheiro"}) {
                            Component painel = encontrar(tela, nome);
                            assertNotNull(nome, painel);
                            assertTrue(painel.isVisible());
                            Rectangle limites = SwingUtilities.convertRectangle(painel.getParent(), painel.getBounds(), tela);
                            assertTrue(nome + " fora da tela em " + tamanho,
                                    new Rectangle(0, 0, tamanho.width, tamanho.height).contains(limites));
                            assertTrue(limites.width > 100 && limites.height >= 200);
                            if (anterior != null) {
                                assertEquals(anterior.y, limites.y);
                                assertTrue(anterior.x + anterior.width <= limites.x);
                            }
                            anterior = limites;
                        }
                        JLabel moedas = (JLabel) encontrar(tela, "recurso.MOEDA");
                        assertTrue("Contagem virtual cortada em " + tamanho,
                                moedas.getWidth() >= moedas.getFontMetrics(moedas.getFont()).stringWidth(moedas.getText()));
                        JLabel aviso = (JLabel) encontrar(tela, "avisoRecursosVirtuais");
                        assertTrue("Aviso cortado em " + tamanho, aviso.getHeight() >= aviso.getPreferredSize().height);
                        assertTrue("Aviso sem espaço horizontal em " + tamanho,
                                aviso.getWidth() >= aviso.getPreferredSize().width);
                        assertFalse(temAbas(tela));
                    }
                } finally { controlador.encerrar(); }
            }
        });
    }

    private boolean temAbas(Container raiz) {
        for (Component c : raiz.getComponents()) {
            if (c instanceof JTabbedPane || (c instanceof Container && temAbas((Container) c))) return true;
        }
        return false;
    }

    @Test
    public void legendaComecaEscondidaEAbreEFechaPeloBotao() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                JPanel painel = TelaPrincipal.criarLegendaRecolhivel();
                JToggleButton botao = (JToggleButton) encontrar(painel, "alternarLegenda");
                Component legenda = encontrar(painel, "conteudoLegenda");
                assertFalse(legenda.isVisible());
                int recolhido = painel.getPreferredSize().height;
                botao.doClick();
                assertTrue(legenda.isVisible());
                assertTrue(painel.getPreferredSize().height > recolhido);
                botao.doClick();
                assertFalse(legenda.isVisible());
                assertEquals(recolhido, painel.getPreferredSize().height);
            }
        });
    }

    @Test
    public void lateralAcompanhaLarguraEUsaRolagemAoInvesDeAchatarConteudo() {
        JPanel lateral = TelaPrincipal.criarPainelLateralRolavel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        JPanel conteudo = new JPanel();
        conteudo.setPreferredSize(new Dimension(250, 900));
        conteudo.setMinimumSize(new Dimension(200, 900));
        lateral.add(conteudo);
        JScrollPane rolagem = new JScrollPane(lateral);
        rolagem.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        for (int largura : new int[] {260, 380}) {
            rolagem.setSize(largura, 300);
            for (int i = 0; i < 3; i++) organizar(rolagem);
            assertEquals(rolagem.getViewport().getExtentSize().width, lateral.getWidth());
            assertTrue(lateral.getHeight() >= 900);
            assertTrue(conteudo.getHeight() >= 900);
            assertTrue(rolagem.getVerticalScrollBar().isVisible());
        }
        assertEquals(260, TelaPrincipal.larguraLateral(900));
        assertEquals(300, TelaPrincipal.larguraLateral(1200));
        assertEquals(380, TelaPrincipal.larguraLateral(2000));
    }

    @Test
    public void mapaPreencheTodaAreaEmJanelasLargasOuAltas() {
        JPanel mapa = new JPanel();
        JPanel moldura = TelaPrincipal.criarMolduraMapa(mapa);
        moldura.setSize(1000, 600);moldura.doLayout();
        assertEquals(new Rectangle(0, 0, 1000, 600), mapa.getBounds());
        moldura.setSize(600, 900);moldura.doLayout();
        assertEquals(new Rectangle(0, 0, 600, 900), mapa.getBounds());
    }

    @Test
    public void cartasAbertasMantemProporcaoMesmoQuandoOPainelEhBaixo() {
        BufferedImage carta = new BufferedImage(20, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D desenho = carta.createGraphics();
        desenho.setColor(Color.MAGENTA);desenho.fillRect(0, 0, 20, 40);desenho.dispose();
        PainelCartasConselheirosAberto painel = new PainelCartasConselheirosAberto();
        painel.definirCartas(new BufferedImage[] {carta, null, null, null});
        for (Dimension tamanho : new Dimension[] {new Dimension(320, 70), new Dimension(260, 130), new Dimension(400, 160)}) {
            painel.setSize(tamanho);
            BufferedImage imagem = new BufferedImage(tamanho.width, tamanho.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = imagem.createGraphics();painel.paint(g);g.dispose();
            int minX = tamanho.width, maxX = -1, minY = tamanho.height, maxY = -1;
            for (int x = 0; x < tamanho.width / 4; x++) {
                for (int y = 0; y < tamanho.height; y++) {
                    if (imagem.getRGB(x, y) == Color.MAGENTA.getRGB()) {
                        minX = Math.min(minX, x);maxX = Math.max(maxX, x);
                        minY = Math.min(minY, y);maxY = Math.max(maxY, y);
                    }
                }
            }
            assertTrue(maxX > minX && maxY > minY);
            assertEquals(2.0, (double) (maxY - minY + 1) / (maxX - minX + 1), 0.1);
            assertTrue(painel.getMinimumSize().height > 50);
        }
    }
    @Test
    public void cartelaMantemProporcaoDaImagemAoRedimensionar() {
        PainelTabuleiroJogador cartela = new PainelTabuleiroJogador("Altani");
        JPanel moldura = TelaJogador.criarMolduraTabuleiro(cartela);
        Dimension original = cartela.getPreferredSize();
        for (Dimension tamanho : new Dimension[] {new Dimension(1000, 300), new Dimension(300, 800)}) {
            moldura.setSize(tamanho);moldura.doLayout();
            assertTrue(new Rectangle(0, 0, tamanho.width, tamanho.height).contains(cartela.getBounds()));
            assertEquals((double) original.width / original.height,
                    (double) cartela.getWidth() / cartela.getHeight(), 0.02);
        }
    }
}
