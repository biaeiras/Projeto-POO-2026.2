package model;

import static org.junit.Assert.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.junit.Test;

import view.PainelCartasConselheirosAberto;

public class ConselheirosAbertosPartidaTest {

    private Partida criarPartida() {
        return new Partida(new ArrayList<>(Arrays.asList(Cor.VERDE, Cor.VERMELHO)),
            Arrays.asList(TipoConselheiroAberto.QUATRO_ESPADAS,
                TipoConselheiroAberto.QUATRO_TESOUROS,
                TipoConselheiroAberto.SEIS_YURTS,
                TipoConselheiroAberto.DUAS_PECAS_BONUS));
    }

    private void prepararJogadores(Partida partida) {
        for (int i = 0; i < 2; i++) {
            assertTrue(partida.escolherConselheiroJogadorAtual(0));
            partida.avancarJogador();
        }
    }

    private ArrayList<Parada> getParadas(Partida partida) {
        ArrayList<Parada> paradas = new ArrayList<>();
        paradas.add(partida.getJogadorAtual().getPeao(0).getPosicao());
        for (int i = 0; i < paradas.size(); i++) {
            for (Parada vizinha : paradas.get(i).getVizinhos().values()) {
                if (!paradas.contains(vizinha)) {
                    paradas.add(vizinha);
                }
            }
        }
        return paradas;
    }

    @Test
    public void construirSextoYurtPontuaAntesDoFimDoTurno() {
        Partida partida = criarPartida();
        prepararJogadores(partida);
        Jogador jogador = partida.getJogadorAtual();
        int construidos = 0;
        for (Parada parada : getParadas(partida)) {
            if (parada.getId() != ParadaID.PEQUIM1 && parada.podeReceberYurt()) {
                assertTrue(parada.adicionarYurt(jogador));
                if (++construidos == 5) {
                    break;
                }
            }
        }
        jogador.adicionarTributo(TipoTributo.YURT, 1);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.moverPeao(ParadaID.PEQUIM1));
        assertEquals(0, jogador.getVotos());
        assertTrue(partida.construirYurt(ParadaID.PEQUIM1));
        assertEquals(1, jogador.getVotos());
        assertTrue(partida.getConselheirosAbertos().get(2).isCumprida());
        assertNotNull(partida.getTurnoAtual());
    }

    @Test
    public void quartoTesouroObtidoEmAtaquePontuaImediatamente() {
        Partida partida = criarPartida();
        prepararJogadores(partida);
        Jogador jogador = partida.getJogadorAtual();
        Cidade cidade = null;
        for (Parada parada : getParadas(partida)) {
            if (parada.getId() == ParadaID.PEQUIM1) {
                cidade = parada.getCidade();
                break;
            }
        }
        assertNotNull(cidade);
        cidade.getTesouros().clear();
        cidade.getTesouros().add(TipoTesouro.FERRO);
        for (int i = 0; i < 3; i++) {
            jogador.adicionarTesouro(TipoTesouro.FERRO);
        }
        jogador.adicionarTributo(TipoTributo.ESPADA, 1);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.moverPeao(ParadaID.PEQUIM1));
        assertTrue(partida.atacarCidade(jogador.getPeao(0), cidade, TipoTesouro.FERRO));
        assertEquals(1, jogador.getVotos());
        assertTrue(partida.getConselheirosAbertos().get(1).isCumprida());
        assertNotNull(partida.getTurnoAtual());
    }

    @Test
    public void sorteiaQuatroCartasDistintasSemExporAColecaoInterna() {
        Partida partida = new Partida(new ArrayList<>(Arrays.asList(Cor.AZUL, Cor.AMARELO)));
        ArrayList<ConselheiroAberto> cartas = partida.getConselheirosAbertos();
        assertEquals(4, cartas.size());
        java.util.HashSet<TipoConselheiroAberto> tipos = new java.util.HashSet<>();
        for (ConselheiroAberto carta : cartas) {
            assertTrue(tipos.add(carta.getTipo()));
        }
        cartas.clear();
        assertEquals(4, partida.getConselheirosAbertos().size());
    }

    @Test
    public void coletaPontuaImediatamenteERemoveSemReposicao() {
        Partida partida = criarPartida();
        prepararJogadores(partida);
        Jogador jogador = partida.getJogadorAtual();
        jogador.adicionarTributo(TipoTributo.ESPADA, 3);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertEquals(0, jogador.getVotos());

        assertTrue(partida.pegarTributo(ProvinciaID.TR_ESPADA));
        assertEquals(1, jogador.getVotos());
        assertEquals(1, partida.getVotosFichaNeutra());
        assertTrue(partida.getConselheirosAbertos().get(0).isCumprida());
        assertSame(jogador, partida.getConselheirosAbertos().get(0).getCumpridaPor());

        // Gastar depois não pode apagar a conquista nem gerar outro voto.
        assertTrue(jogador.gastarTributo(TipoTributo.ESPADA, 4));
        assertEquals(0, partida.verificarConselheirosAbertos());
        assertTrue(partida.TerminarTurno());
        assertEquals(1, jogador.getVotos());

        Jogador outro = partida.getJogadorAtual();
        outro.adicionarTributo(TipoTributo.ESPADA, 4);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertEquals(0, outro.getVotos());
        assertEquals(4, partida.getConselheirosAbertos().size());
        assertEquals(TipoConselheiroAberto.QUATRO_ESPADAS,
            partida.getConselheirosAbertos().get(0).getTipo());
    }

    @Test
    public void podeCumprirDuasCartasSemRepetirPontuacao() {
        Partida partida = criarPartida();
        prepararJogadores(partida);
        Jogador jogador = partida.getJogadorAtual();
        jogador.adicionarTributo(TipoTributo.ESPADA, 4);
        for (int i = 0; i < 4; i++) {
            jogador.adicionarTesouro(TipoTesouro.FERRO);
        }
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertEquals(2, jogador.getVotos());
        assertEquals(2, partida.getVotosFichaNeutra());
        assertEquals(0, partida.verificarConselheirosAbertos());
        assertEquals(2, jogador.getVotos());
    }

    @Test
    public void bonusRecebidoDuranteTurnoDisparaDemanda() {
        Partida partida = criarPartida();
        prepararJogadores(partida);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.receberPecaBonus(new PecaBonus(TipoBonus.DUAS_ESPADAS)));
        assertEquals(0, partida.getJogadorAtual().getVotos());
        assertTrue(partida.receberPecaBonus(new PecaBonus(TipoBonus.DUAS_MOEDAS)));
        assertEquals(1, partida.getJogadorAtual().getVotos());
        assertFalse(partida.receberPecaBonus(new PecaBonus(TipoBonus.DUAS_MOEDAS)));
        assertEquals(1, partida.getJogadorAtual().getVotos());
    }

    @Test
    public void recompensaAtualizaFichaNeutraEDisparaFim() {
        Partida partida = criarPartida();
        prepararJogadores(partida);
        Jogador jogador = partida.getJogadorAtual();
        partida.registrarVoto(jogador, 9);
        jogador.adicionarTributo(TipoTributo.ESPADA, 3);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.pegarTributo(ProvinciaID.TR_ESPADA));
        assertEquals(10, partida.getVotosFichaNeutra());
        assertTrue(partida.isFimDisparado());
    }

    @Test
    public void fachadaNotificaAViewComCartaRetiradaEPlacarAtualizado() {
        Partida partida = criarPartida();
        prepararJogadores(partida);
        partida.getJogadorAtual().adicionarTributo(TipoTributo.ESPADA, 3);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        FachadaJogo fachada = FachadaJogo.getInstancia();
        fachada.definirPartida(partida);
        final ArrayList<String> eventos = new ArrayList<>();
        ObservadorJogo observador = new ObservadorJogo() {
            @Override
            public void atualizar(ObservadoJogo observado) {
                assertNull(observado.getConselheirosAbertos().get(0));
                assertEquals(Integer.valueOf(1), observado.getPlacarVotos().get("Altani"));
                assertEquals(1, observado.getVotosFichaNeutra());
                eventos.add("atualizou");
            }
        };
        fachada.adicionarObservador(observador);
        try {
            fachada.pegarTributo(ProvinciaID.TR_ESPADA);
            assertEquals(1, eventos.size());
            fachada.pegarTributo(ProvinciaID.TR_ESPADA);
            assertEquals("Ação recusada não deve gerar notificação", 1, eventos.size());
        } finally {
            fachada.removerObservador(observador);
        }
    }

    @Test
    public void painelCarregaTodosOsTiposEPodeApagarCartaCumprida() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                PainelCartasConselheirosAberto painel = new PainelCartasConselheirosAberto();
                painel.setSize(400, 160);
                for (TipoConselheiroAberto tipo : TipoConselheiroAberto.values()) {
                    painel.definirConselheiros(new ArrayList<>(Arrays.asList(
                        tipo.name(), null, null, null)));
                }
                painel.definirConselheiros(new ArrayList<>(Arrays.asList(
                    "QUATRO_ESPADAS", null, null, null)));
                BufferedImage antes = desenhar(painel);
                painel.definirConselheiros(new ArrayList<>(Arrays.asList(
                    null, null, null, null)));
                BufferedImage depois = desenhar(painel);
                int diferentes = 0;
                for (int x = 10; x < 90; x++) {
                    for (int y = 10; y < 150; y++) {
                        if (antes.getRGB(x, y) != depois.getRGB(x, y)) {
                            diferentes++;
                        }
                    }
                }
                assertTrue("A imagem da carta deve desaparecer", diferentes > 100);
            }
        });
    }

    private BufferedImage desenhar(PainelCartasConselheirosAberto painel) {
        BufferedImage imagem = new BufferedImage(400, 160, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();
        try {
            painel.paint(g);
        } finally {
            g.dispose();
        }
        return imagem;
    }
}
