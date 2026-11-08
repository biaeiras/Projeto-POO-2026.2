package model;

import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.SwingUtilities;
import org.junit.Test;

import controller.ControladorTabuleiroJogador;
import view.PainelPecasBonus;
import view.TelaJogador;

public class EntregaTesourosTest {
    private Partida criarPartida() {
        Partida partida = new Partida(new ArrayList<>(Arrays.asList(Cor.VERDE, Cor.VERMELHO)),
                Arrays.asList(TipoConselheiroAberto.QUATRO_ESPADAS,
                        TipoConselheiroAberto.SEIS_YURTS, TipoConselheiroAberto.DEZ_YURTS,
                        TipoConselheiroAberto.DUAS_PECAS_BONUS));
        for (int i = 0; i < 2; i++) {
            assertTrue(partida.escolherConselheiroJogadorAtual(0));
            partida.avancarJogador();
        }
        return partida;
    }

    private HashMap<ParadaID, Parada> mapa(Partida partida) {
        HashMap<ParadaID, Parada> mapa = new HashMap<>();
        ArrayList<Parada> fila = new ArrayList<>();
        fila.add(partida.getJogadorAtual().getPeao(0).getPosicao());
        for (int i = 0; i < fila.size(); i++) {
            Parada parada = fila.get(i);
            mapa.put(parada.getId(), parada);
            for (Parada vizinha : parada.getVizinhos().values()) {
                if (!fila.contains(vizinha)) fila.add(vizinha);
            }
        }
        return mapa;
    }

    // Prepara posições antes de abrir o turno; a entrega passa pelas regras reais.
    private void posicionar(Jogador jogador, Parada parada) {
        Peao peao = jogador.getPeao(0);
        peao.getPosicao().removePeao(peao);
        peao.setPosicao(parada);
        parada.insertPeao(peao);
    }

    private HashMap<TipoTesouro, Integer> conjunto(TipoTesouro... tipos) {
        HashMap<TipoTesouro, Integer> entrega = new HashMap<>();
        for (TipoTesouro tipo : tipos) entrega.put(tipo, entrega.getOrDefault(tipo, 0) + 1);
        return entrega;
    }

    private void darTesouros(Jogador jogador, TipoTesouro... tipos) {
        for (TipoTesouro tipo : tipos) assertTrue(jogador.adicionarTesouro(tipo));
    }

    @Test
    public void seteExigenciasCorrespondemAoMapaEIndependemDoBonus() {
        Partida partida = criarPartida();
        HashMap<ParadaID, Parada> mapa = mapa(partida);
        ParadaID[] locais = {ParadaID.KIEV_MOSCOU,
                ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA, ParadaID.SARAI_KAESONG_KARAKORUM,
                ParadaID.KAESONG_CANTAO, ParadaID.BAGDA_CABUL};
        TipoTesouro[] tipos = {TipoTesouro.TECIDO, TipoTesouro.PELE, TipoTesouro.FERRO,
                TipoTesouro.BOI, TipoTesouro.ALIMENTO};
        int conselheiros = 0;
        for (Parada parada : mapa.values()) if (parada.ehConselheiro()) conselheiros++;
        assertEquals(7, conselheiros);
        for (int i = 0; i < locais.length; i++) {
            Parada parada = mapa.get(locais[i]);
            parada.setBonus(null);
            assertEquals(conjunto(tipos[i], tipos[i]), partida.getExigenciaEntrega(locais[i]));
            assertTrue(parada.aceitaEntrega(conjunto(tipos[i], tipos[i])));
            assertEquals(1, partida.getVotosEntrega(locais[i]));
        }
        assertTrue(mapa.get(ParadaID.PEQUIM_CABUL_CANTAO).ehConselheiro());
        assertTrue(partida.getExigenciaEntrega(ParadaID.PEQUIM_CABUL_CANTAO).isEmpty());
        assertEquals(conjunto(TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI),
                partida.getExigenciaEntrega(ParadaID.KARAKORUM));
        assertEquals(2, partida.getVotosEntrega(ParadaID.KARAKORUM));
    }

    @Test
    public void entregaDepoisDeMoverPodeSerRepetidaMasBonusSoSaiUmaVez() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        Parada local = mapa(partida).get(ParadaID.SARAI_KAESONG_KARAKORUM);
        local.setBonus(TipoBonus.DUAS_MOEDAS);
        darTesouros(jogador, TipoTesouro.FERRO, TipoTesouro.FERRO,
                TipoTesouro.FERRO, TipoTesouro.FERRO);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertFalse(partida.getLocaisEntregaDisponiveis().contains(local.getId()));
        assertFalse(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.FERRO, TipoTesouro.FERRO)));
        assertTrue(partida.moverPeao(local.getId()));
        assertTrue(partida.getLocaisEntregaDisponiveis().contains(local.getId()));
        assertTrue(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.FERRO, TipoTesouro.FERRO)));
        assertNull(local.getBonus());
        assertTrue(jogador.possuiPecaBonus(TipoBonus.DUAS_MOEDAS));
        assertFalse(jogador.isPecaBonusUsada(TipoBonus.DUAS_MOEDAS));
        assertTrue(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.FERRO, TipoTesouro.FERRO)));
        assertEquals(2, jogador.getVotos());
        assertEquals(2, partida.getVotosFichaNeutra());
        assertEquals(0, jogador.getQuantidadeTesouro(TipoTesouro.FERRO));
        assertEquals(1, jogador.getQuantidadePecasBonus());
    }

    @Test
    public void outroJogadorPodeEntregarDepoisQueBonusFoiRecolhido() {
        Partida partida = criarPartida();
        Jogador primeiro = partida.getJogadorAtual();
        Parada local = mapa(partida).get(ParadaID.KAESONG_CANTAO);
        local.setBonus(TipoBonus.DUAS_MOEDAS);
        posicionar(primeiro, local);
        darTesouros(primeiro, TipoTesouro.BOI, TipoTesouro.BOI);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.BOI, TipoTesouro.BOI)));
        assertTrue(partida.TerminarTurno());
        Jogador segundo = partida.getJogadorAtual();
        posicionar(segundo, local);
        darTesouros(segundo, TipoTesouro.BOI, TipoTesouro.BOI);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.BOI, TipoTesouro.BOI)));
        assertEquals(1, segundo.getVotos());
        assertEquals(0, segundo.getQuantidadePecasBonus());
        assertEquals(1, primeiro.getQuantidadePecasBonus());
    }

    @Test
    public void conjuntoInvalidoOuInsuficienteNaoConsomeRecursosNemBonus() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        Parada local = mapa(partida).get(ParadaID.KARAKORUM);
        TipoBonus bonus = local.getBonus();
        darTesouros(jogador, TipoTesouro.FERRO, TipoTesouro.BOI);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        HashMap<TipoTesouro, Integer> correto = conjunto(TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI);
        assertFalse(partida.entregarTesouros(local.getId(), correto));
        assertFalse(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.FERRO, TipoTesouro.BOI)));
        assertFalse(partida.entregarTesouros(null, correto));
        assertFalse(partida.entregarTesouros(local.getId(), null));
        assertFalse(partida.entregarTesouros(local.getId(), new HashMap<TipoTesouro, Integer>()));
        HashMap<TipoTesouro, Integer> invalido = new HashMap<>(correto);
        invalido.put(TipoTesouro.PELE, -1);
        assertFalse(partida.entregarTesouros(local.getId(), invalido));
        invalido.put(TipoTesouro.PELE, null);
        assertFalse(partida.entregarTesouros(local.getId(), invalido));
        assertEquals(2, jogador.getTotalTesouros());
        assertEquals(0, jogador.getVotos());
        assertEquals(bonus, local.getBonus());
    }

    @Test
    public void karakorumExigeOsTresTiposImpressosEConcedeDoisVotos() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        mapa(partida).get(ParadaID.KARAKORUM).setBonus(null);
        darTesouros(jogador, TipoTesouro.FERRO, TipoTesouro.FERRO, TipoTesouro.FERRO,
                TipoTesouro.PELE, TipoTesouro.BOI);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertFalse(partida.entregarTesouros(ParadaID.KARAKORUM,
                conjunto(TipoTesouro.FERRO, TipoTesouro.FERRO, TipoTesouro.FERRO)));
        assertTrue(partida.entregarTesouros(ParadaID.KARAKORUM,
                conjunto(TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI)));
        assertEquals(2, jogador.getVotos());
        assertEquals(2, jogador.getTotalTesouros());
    }

    @Test
    public void conselheiroLivreAceitaTresTesourosIguaisOuMisturados() {
        for (boolean iguais : new boolean[] {true, false}) {
            Partida partida = criarPartida();
            Jogador jogador = partida.getJogadorAtual();
            Parada local = mapa(partida).get(ParadaID.PEQUIM_CABUL_CANTAO);
            local.setBonus(null);
            posicionar(jogador, local);
            TipoTesouro[] tesouros = iguais
                    ? new TipoTesouro[] {TipoTesouro.FERRO, TipoTesouro.FERRO, TipoTesouro.FERRO}
                    : new TipoTesouro[] {TipoTesouro.TECIDO, TipoTesouro.PELE, TipoTesouro.BOI};
            darTesouros(jogador, tesouros);
            assertTrue(partida.iniciarTurno(Regiao.PERSIA));
            assertFalse(partida.entregarTesouros(local.getId(), conjunto(tesouros[0], tesouros[1])));
            assertTrue(partida.entregarTesouros(local.getId(), conjunto(tesouros)));
            assertEquals(1, jogador.getVotos());
            assertEquals(0, jogador.getTotalTesouros());
        }
    }

    @Test
    public void tesouroVirtualComplementaEntregaESeEsgota() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        Parada local = mapa(partida).get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA);
        local.setBonus(null);
        posicionar(jogador, local);
        assertTrue(jogador.getTabuleiroJogador().adicionarMelhoria(Regiao.PERSIA,
                new Melhoria(TipoMelhoria.PELE_VIRTUAL, Regiao.PERSIA)));
        darTesouros(jogador, TipoTesouro.PELE);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.PELE, TipoTesouro.PELE)));
        assertEquals(0, jogador.getQuantidadeTesouro(TipoTesouro.PELE));
        assertEquals(0, partida.getQuantidadeTesouroVirtual(TipoTesouro.PELE));
        assertFalse(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.PELE, TipoTesouro.PELE)));
        assertEquals(1, jogador.getVotos());
    }

    @Test
    public void entregaRecusadaPreservaTambemOsTesourosVirtuais() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        assertTrue(jogador.getTabuleiroJogador().adicionarMelhoria(Regiao.PERSIA,
                new Melhoria(TipoMelhoria.PELE_VIRTUAL, Regiao.PERSIA)));
        darTesouros(jogador, TipoTesouro.FERRO);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertFalse(partida.entregarTesouros(ParadaID.KARAKORUM,
                conjunto(TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI)));
        assertEquals(1, jogador.getQuantidadeTesouro(TipoTesouro.FERRO));
        assertEquals(1, partida.getQuantidadeTesouroVirtual(TipoTesouro.PELE));
    }

    @Test
    public void melhoriasPodemPagarTodaAEntrega() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        Parada local = mapa(partida).get(ParadaID.KIEV_MOSCOU_SAMARCANDA_BAGDA);
        local.setBonus(null);
        posicionar(jogador, local);
        for (int i = 0; i < 2; i++) {
            assertTrue(jogador.getTabuleiroJogador().adicionarMelhoria(Regiao.PERSIA,
                    new Melhoria(TipoMelhoria.PELE_VIRTUAL, Regiao.PERSIA)));
        }
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.PELE, TipoTesouro.PELE)));
        assertEquals(0, jogador.getTotalTesouros());
        assertEquals(0, partida.getQuantidadeTesouroVirtual(TipoTesouro.PELE));
        assertEquals(1, jogador.getVotos());
    }

    @Test
    public void votoExtraEAplicadoImediatamenteEPecaPermaneceComoUsada() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        mapa(partida).get(ParadaID.KARAKORUM).setBonus(TipoBonus.VOTO_EXTRA);
        darTesouros(jogador, TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.entregarTesouros(ParadaID.KARAKORUM,
                conjunto(TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI)));
        assertEquals(3, jogador.getVotos());
        assertEquals(3, partida.getVotosFichaNeutra());
        assertEquals(1, jogador.getQuantidadePecasBonus());
        assertTrue(jogador.isPecaBonusUsada(TipoBonus.VOTO_EXTRA));
    }

    @Test
    public void segundaPecaRecebidaNaEntregaCumpreConselheiroAberto() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        assertTrue(jogador.adicionarPecaBonus(new PecaBonus(TipoBonus.DOIS_YURTS)));
        mapa(partida).get(ParadaID.KARAKORUM).setBonus(TipoBonus.DUAS_MOEDAS);
        darTesouros(jogador, TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.entregarTesouros(ParadaID.KARAKORUM,
                conjunto(TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI)));
        assertEquals(3, jogador.getVotos());
        assertEquals(2, jogador.getQuantidadePecasBonus());
        assertTrue(partida.getConselheirosAbertos().get(3).isCumprida());
    }

    @Test
    public void semTurnoOuEmParadaComumNaoPodeEntregar() {
        Partida partida = criarPartida();
        HashMap<TipoTesouro, Integer> entrega = conjunto(TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI);
        assertTrue(partida.getLocaisEntregaDisponiveis().isEmpty());
        assertFalse(partida.entregarTesouros(ParadaID.KARAKORUM, entrega));
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.moverPeao(ParadaID.PEQUIM1));
        assertFalse(partida.entregarTesouros(ParadaID.PEQUIM1, entrega));
        assertEquals(0, partida.getJogadorAtual().getVotos());
    }

    @Test
    public void entregaDoAltaniUsaApenasOPeaoAtivoELimpaVisitasAoTrocarTurno() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        Parada local = mapa(partida).get(ParadaID.SARAI_KAESONG_KARAKORUM);
        Peao segundoPeao = jogador.getPeao(1);
        segundoPeao.getPosicao().removePeao(segundoPeao);
        segundoPeao.setPosicao(local);
        local.insertPeao(segundoPeao);
        darTesouros(jogador, TipoTesouro.FERRO, TipoTesouro.FERRO);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertFalse(partida.getLocaisEntregaDisponiveis().contains(local.getId()));
        assertFalse(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.FERRO, TipoTesouro.FERRO)));
        assertTrue(partida.escolherPeaoAtivo(segundoPeao));
        local.setBonus(null);
        assertTrue(partida.entregarTesouros(local.getId(), conjunto(TipoTesouro.FERRO, TipoTesouro.FERRO)));
        assertFalse(partida.escolherPeaoAtivo(jogador.getPeao(0)));
        assertTrue(partida.TerminarTurno());
        assertTrue(partida.getLocaisEntregaDisponiveis().isEmpty());
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertFalse(partida.getLocaisEntregaDisponiveis().contains(local.getId()));
    }

    @Test
    public void fachadaNotificaViewApenasNoSucessoEAtualizaPecasReais() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Partida partida = criarPartida();
                Jogador jogador = partida.getJogadorAtual();
                mapa(partida).get(ParadaID.KARAKORUM).setBonus(TipoBonus.VOTO_EXTRA);
                darTesouros(jogador, TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI);
                FachadaJogo fachada = FachadaJogo.getInstancia();
                fachada.definirPartida(partida);
                TelaJogador tela = new TelaJogador();
                ControladorTabuleiroJogador controlador = new ControladorTabuleiroJogador(tela);
                final int[] notificacoes = {0};
                ObservadorJogo contador = new ObservadorJogo() {
                    @Override
                    public void atualizar(ObservadoJogo observado) { notificacoes[0]++; }
                };
                fachada.adicionarObservador(contador);
                try {
                    conferirPainelBonus(tela, new boolean[7], new boolean[7]);
                    assertFalse(tela.getBotaoEntregarTesouros().isEnabled());
                    assertArrayEquals(new int[] {-1, -1, -1, -1, -1, -1, -1}, fachada.getOrdemPecasBonusJogadorAtual());
                    assertTrue(fachada.ativarColunaJogadorAtual(1));
                    assertTrue(tela.getBotaoEntregarTesouros().isEnabled());
                    HashMap<String, Integer> entrega = new HashMap<>();
                    entrega.put("FERRO", 1); entrega.put("PELE", 1); entrega.put("BOI", 1);
                    int antes = notificacoes[0];
                    assertTrue(fachada.entregarTesourosJogadorAtual(ParadaID.KARAKORUM, entrega));
                    assertEquals(antes + 1, notificacoes[0]);
                    assertEquals(Integer.valueOf(3), fachada.getPlacarVotos().get("Altani"));
                    assertEquals(TipoBonus.VOTO_EXTRA.ordinal(),fachada.getOrdemPecasBonusJogadorAtual()[0]);
                    assertTrue(fachada.getPecasBonusUsadasJogadorAtual()[1]);
                    conferirPainelBonus(tela,
                            new boolean[] {false, true, false, false, false, false, false},
                            new boolean[] {false, true, false, false, false, false, false});
                    assertFalse(fachada.entregarTesourosJogadorAtual(ParadaID.KARAKORUM, entrega));
                    entrega.put("INVALIDO", 1);
                    assertFalse(fachada.entregarTesourosJogadorAtual(ParadaID.KARAKORUM, entrega));
                    assertEquals(antes + 1, notificacoes[0]);
                    HashMap<String, Integer> exigencia = fachada.getExigenciaEntrega(ParadaID.KARAKORUM);
                    exigencia.clear();
                    assertEquals(3, fachada.getExigenciaEntrega(ParadaID.KARAKORUM).size());
                    assertTrue(fachada.terminarTurnoJogadorAtual());
                    assertFalse(tela.getBotaoEntregarTesouros().isEnabled());
                    assertArrayEquals(new int[] {-1, -1, -1, -1, -1, -1, -1},fachada.getOrdemPecasBonusJogadorAtual());
                    conferirPainelBonus(tela, new boolean[7], new boolean[7]);
                } finally {
                    fachada.removerObservador(contador);
                    controlador.encerrar();
                }
            }
        });
    }

    private void conferirPainelBonus(TelaJogador tela, boolean[] conquistadas, boolean[] usadas) {
        PainelPecasBonus real = encontrarPainelBonus(tela);
        assertNotNull(real);
        PainelPecasBonus esperado = new PainelPecasBonus();
        int[] ordemEsperada = {-1, -1, -1, -1, -1, -1, -1};

        for (int i = 0; i < conquistadas.length; i++) {
            if (conquistadas[i]) {
                ordemEsperada[i] = i;
            }
        }

        esperado.atualizarPecas(ordemEsperada, usadas);
        java.awt.image.BufferedImage[] imagens = new java.awt.image.BufferedImage[2];
        PainelPecasBonus[] paineis = {real, esperado};
        for (int i = 0; i < paineis.length; i++) {
            paineis[i].setSize(280, 120);
            imagens[i] = new java.awt.image.BufferedImage(280, 120,
                    java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = imagens[i].createGraphics();
            paineis[i].paint(g);
            g.dispose();
        }
        assertArrayEquals(imagens[1].getRGB(0, 0, 280, 120, null, 0, 280),
                imagens[0].getRGB(0, 0, 280, 120, null, 0, 280));
    }

    private PainelPecasBonus encontrarPainelBonus(Container raiz) {
        for (Component componente : raiz.getComponents()) {
            if (componente instanceof PainelPecasBonus) return (PainelPecasBonus) componente;
            if (componente instanceof Container) {
                PainelPecasBonus painel = encontrarPainelBonus((Container) componente);
                if (painel != null) return painel;
            }
        }
        return null;
    }
}
