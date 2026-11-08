package view;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.junit.Test;

import controller.ControllerAPI;
import model.Cor;
import model.FachadaJogo;
import model.ObservadoJogo;
import model.ParadaID;

public class PainelTabuleiroTest {

    @Test
    public void mapaDesenhaAntesDaColunaEAtualizaAoMoverETerminarTurno() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.iniciarPartida(new ArrayList<>(Arrays.asList(Cor.AZUL, Cor.AMARELO)));
                for (int i = 0; i < 2; i++) {
                    assertTrue(fachada.escolherConselheiroJogadorAtual(0));
                    fachada.avancarJogadorNaPreparacao();
                }

                assertFalse(fachada.isTurnoEmAndamento());
                assertTrue(fachada.getAlcancaveisTotais().isEmpty());
                assertTrue(fachada.getVizinhosPossiveis().isEmpty());
                assertTrue(fachada.getProvincias().isEmpty());
                assertFalse(fachada.getPosicoesPeoes().isEmpty());
                assertFalse(fachada.getCargasProvincias().isEmpty());

                PainelTabuleiro painel = new PainelTabuleiro();
                painel.setSize(1000, 700);
                ControllerAPI controlador = new ControllerAPI();
                ObservadoJogo observado = controlador.registrarObservador(painel);
                try {
                    painel.atualizar(observado);
                    BufferedImage inicial = desenhar(painel);
                    int destinoX = 738;
                    int destinoY = 302;
                    assertEquals(ParadaID.PEQUIM1,
                        painel.getCollisionParada(destinoX, destinoY));

                    assertTrue(fachada.ativarColunaJogadorAtual(1));
                    assertFalse(fachada.getAlcancaveisTotais().isEmpty());
                    fachada.moverPeao(ParadaID.PEQUIM1);
                    assertTrue(fachada.getPosicoesPeoes().get(ParadaID.PEQUIM1).contains(Cor.AZUL));

                    // Sem atualização manual: o mapa recebe a notificação da fachada.
                    BufferedImage depois = desenhar(painel);
                    assertEquals(Color.BLUE.getRGB(), depois.getRGB(destinoX, destinoY));
                    assertNotEquals(inicial.getRGB(destinoX, destinoY),
                        depois.getRGB(destinoX, destinoY));

                    fachada.avancarTurno();
                    assertFalse("Não deve ativar a Pérsia automaticamente",
                        fachada.isTurnoEmAndamento());
                    assertEquals("Tolui", fachada.getNomePersonagemAtual());
                    assertTrue(fachada.podeEscolherColuna());
                    assertTrue(fachada.getAlcancaveisTotais().isEmpty());
                    assertTrue(fachada.getVizinhosPossiveis().isEmpty());
                    assertTrue(fachada.getProvincias().isEmpty());
                    desenhar(painel);
                } finally {
                    controlador.removerObservador(painel);
                }
            }
        });
    }

    @Test
    public void colisoesAcompanhamORedimensionamentoENaoUsamPainelSemTamanho() {
        PainelTabuleiro painel = new PainelTabuleiro();
        assertNull(painel.getCollisionParada(0, 0));
        assertNull(painel.getCollisionProvincia(0, 0));
        painel.setSize(1000, 700);
        assertEquals(ParadaID.PEQUIM1, painel.getCollisionParada(738, 302));
        painel.setSize(500, 350);
        assertEquals(ParadaID.PEQUIM1, painel.getCollisionParada(369, 151));
    }

    @Test
    public void cliquesDosTesourosEKhanCorrespondemAoDesenhoEmDuasEscalas() {
        PainelTabuleiro painel = new PainelTabuleiro();
        assertEquals(-1, painel.getCollisionCidade(0, 0, model.CidadeID.PEQUIM, 4));
        assertNull(painel.getCollisionKhan(0, 0));
        for (int largura : new int[] {1000, 500}) {
            int altura = largura * 7 / 10;
            painel.setSize(largura, altura);
            for (int i = 0; i < 4; i++) {
                int x = (int) ((0.7176 - 0.015 + 0.035 * (i % 2)) * largura);
                int y = (int) ((0.5051 - 0.015 + 0.035 * (i / 2)) * altura);
                assertEquals(i, painel.getCollisionCidade(x, y, model.CidadeID.PEQUIM, 4));
                assertEquals(-1, painel.getCollisionCidade(x, y, model.CidadeID.PEQUIM, i));
            }
            assertEquals(model.KhanID.TL,
                    painel.getCollisionKhan((int) (0.2170 * largura), (int) (0.2154 * altura)));
        }
        painel.setSize(1000, 700);
        desenhar(painel); // Também deve desenhar sem notificação inicial.
    }

    @Test
    public void marcadorDoKhanPermaneceVisivelDepoisDoTurnoEMudaDeDestino() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.iniciarPartida(new ArrayList<>(Arrays.asList(Cor.VERDE, Cor.VERMELHO)));
                for (int i = 0; i < 2; i++) {
                    assertTrue(fachada.escolherConselheiroJogadorAtual(0));
                    fachada.avancarJogadorNaPreparacao();
                }
                PainelTabuleiro painel = new PainelTabuleiro();
                painel.setSize(1000, 700);
                ControllerAPI controlador = new ControllerAPI();
                painel.atualizar(controlador.registrarObservador(painel));
                try {
                    int corOriginal = desenhar(painel).getRGB(217, 151);
                    assertTrue(fachada.ativarColunaJogadorAtual(0));
                    assertTrue(fachada.selecionarKhan(model.KhanID.TL));
                    assertTrue(fachada.selecionarTributo(model.ProvinciaID.TL_ESPADA));
                    assertTrue(fachada.selecionarTributo(model.ProvinciaID.TL_MOEDA));
                    assertEquals(Color.WHITE.getRGB(), desenhar(painel).getRGB(217, 151));
                    assertTrue(fachada.terminarTurnoJogadorAtual());
                    assertEquals(Color.WHITE.getRGB(), desenhar(painel).getRGB(217, 151));
                    assertTrue(fachada.ativarColunaJogadorAtual(0));
                    assertTrue(fachada.selecionarKhan(model.KhanID.M));
                    assertTrue(fachada.terminarTurnoJogadorAtual());
                    BufferedImage imagem = desenhar(painel);
                    assertEquals(corOriginal, imagem.getRGB(217, 151));
                    assertEquals(Color.WHITE.getRGB(), imagem.getRGB(36, 678));
                } finally {
                    controlador.removerObservador(painel);
                }
            }
        });
    }

    private static BufferedImage desenhar(PainelTabuleiro painel) {
        BufferedImage imagem = new BufferedImage(1000, 700, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();
        try {
            painel.paint(g);
        } finally {
            g.dispose();
        }
        return imagem;
    }
}
