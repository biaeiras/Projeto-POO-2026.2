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
