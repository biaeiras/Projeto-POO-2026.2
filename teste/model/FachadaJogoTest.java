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

public class FachadaJogoTest {

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
            java.net.URL recurso = getClass().getResource("/imagens/melhorias/" + nomes.get(0));
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
