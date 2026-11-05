package model;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.junit.Test;

import controller.ControladorTabuleiroJogador;
import view.PainelTabuleiroJogador;
import view.PainelCartaConselheiroSecreto;
import view.TelaJogador;

public class FachadaJogoTest {

    @Test
    public void testeTelaMostraConselheiroEscolhidoELimpaCartaAoTrocarJogador() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.iniciarPartida(new ArrayList<>(Arrays.asList(Cor.VERDE, Cor.VERMELHO)));
                TelaJogador tela = new TelaJogador();
                ControladorTabuleiroJogador controlador = new ControladorTabuleiroJogador(tela);
                PainelCartaConselheiroSecreto painel = encontrarCarta(tela);
                assertNotNull(painel);
                try {
                    assertNull(fachada.getNomeConselheiroSecretoJogadorAtual());
                    conferirImagem(painel, null);

                    ArrayList<String> opcoes = fachada.getOpcoesConselheiroJogadorAtual();
                    int indice = "CIDADAO".equals(opcoes.get(0)) ? 1 : 0;
                    String primeira = opcoes.get(indice);
                    assertTrue(fachada.escolherConselheiroJogadorAtual(indice));
                    assertEquals(primeira, fachada.getNomeConselheiroSecretoJogadorAtual());
                    conferirImagem(painel, primeira);

                    fachada.avancarJogadorNaPreparacao();
                    assertNull(fachada.getNomeConselheiroSecretoJogadorAtual());
                    conferirImagem(painel, null);
                    String segunda = fachada.getOpcoesConselheiroJogadorAtual().get(1);
                    assertTrue(fachada.escolherConselheiroJogadorAtual(1));
                    conferirImagem(painel, segunda);

                    fachada.avancarJogadorNaPreparacao();
                    conferirImagem(painel, primeira);
                    assertTrue(fachada.ativarColunaJogadorAtual(1));
                    assertTrue(fachada.terminarTurnoJogadorAtual());
                    assertEquals(segunda, fachada.getNomeConselheiroSecretoJogadorAtual());
                    conferirImagem(painel, segunda);
                } finally {
                    controlador.encerrar();
                }
            }
        });
    }

    private static PainelCartaConselheiroSecreto encontrarCarta(java.awt.Container raiz) {
        for (java.awt.Component componente : raiz.getComponents()) {
            if (componente instanceof PainelCartaConselheiroSecreto) {
                return (PainelCartaConselheiroSecreto) componente;
            }
            if (componente instanceof java.awt.Container) {
                PainelCartaConselheiroSecreto carta = encontrarCarta((java.awt.Container) componente);
                if (carta != null) return carta;
            }
        }
        return null;
    }

    private static void conferirImagem(PainelCartaConselheiroSecreto painel, String nome) {
        PainelCartaConselheiroSecreto esperado = new PainelCartaConselheiroSecreto();
        if (nome != null) {
            try {
                esperado.definirCarta(ImageIO.read(FachadaJogoTest.class.getResource(
                    "/" + nome.toLowerCase(java.util.Locale.ROOT) + ".jpg")));
            } catch (java.io.IOException e) {
                throw new AssertionError(e);
            }
        }
        assertArrayEquals("A carta desenhada deve corresponder à escolha do jogador",
            desenharCarta(esperado), desenharCarta(painel));
    }

    private static int[] desenharCarta(PainelCartaConselheiroSecreto painel) {
        painel.setSize(110, 150);
        BufferedImage imagem = new BufferedImage(110, 150, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = imagem.createGraphics();
        try {
            painel.paint(g);
        } finally {
            g.dispose();
        }
        return imagem.getRGB(0, 0, 110, 150, null, 0, 110);
    }

    @Test
    public void testeOrdemDasColunasENomesDasImagens() throws Exception {
        Partida partida = TesteVisualTabuleiroJogador.prepararPartida();
        FachadaJogo fachada = FachadaJogo.getInstancia();
        fachada.definirPartida(partida);

        String[] esperados = {
            "ferro_virtual_china.jpg",
            "tecido_virtual_persia.jpg",
            "boi_virtual_russia.jpg",
            "moeda_virtual_china.jpg"
        };

        for (int coluna = 0; coluna < esperados.length; coluna++) {
            ArrayList<String> nomes = fachada.getMelhoriasTabuleiroJogador(coluna);
            assertEquals(Arrays.asList(esperados[coluna]), nomes);
            java.net.URL recurso = getClass().getResource("/melhorias/" + nomes.get(0));
            assertNotNull("Imagem da coluna " + coluna, recurso);
            assertNotNull(ImageIO.read(recurso));
        }
    }

    @Test
    public void testeControladorEPainelCarregamMelhoriasSemExtensaoDuplicada() throws Exception {
        FachadaJogo.getInstancia().definirPartida(TesteVisualTabuleiroJogador.prepararPartida());

        // Executa o desenho real em memória, sem precisar abrir uma janela.
        SwingUtilities.invokeAndWait(() -> {
            PainelTabuleiroJogador painel = new PainelTabuleiroJogador("Altani");
            new ControladorTabuleiroJogador(painel).atualizarTabuleiro();
            painel.setSize(800, 450);
            BufferedImage imagem = new BufferedImage(800, 450, BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g = imagem.createGraphics();
            try {
                painel.paint(g);
            } finally {
                g.dispose();
            }
        });
    }
}
