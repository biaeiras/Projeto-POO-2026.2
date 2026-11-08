package model;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.Test;

public class IntegracaoMapaTest {
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

    private Parada encontrarParada(Partida partida, ParadaID id) {
        ArrayList<Parada> fila = new ArrayList<>();
        fila.add(partida.getJogadorAtual().getPeao(0).getPosicao());
        for (int i = 0; i < fila.size(); i++) {
            Parada parada = fila.get(i);
            if (parada.getId() == id) return parada;
            for (Parada vizinha : parada.getVizinhos().values()) {
                if (!fila.contains(vizinha)) fila.add(vizinha);
            }
        }
        throw new AssertionError("Parada ausente: " + id);
    }

    private Cidade prepararPequim(Partida partida, TipoTesouro... tesouros) {
        Cidade cidade = encontrarParada(partida, ParadaID.PEQUIM1).getCidade();
        cidade.getTesouros().clear();
        cidade.getTesouros().addAll(Arrays.asList(tesouros));
        return cidade;
    }

    @Test
    public void mapaMostraTresCidadesEBonusAntesDoPrimeiroTurno() {
        Partida partida = criarPartida();
        assertEquals(3, partida.getCidades().size());
        assertFalse(partida.getCidades().containsKey(CidadeID.KARAKORUM));
        for (ArrayList<TipoTesouro> tesouros : partida.getCidades().values()) {
            assertEquals(4, tesouros.size());
        }
        assertEquals(7, partida.getBonus().size());
        assertTrue(partida.getKhans().isEmpty());
        assertTrue(partida.getCidadesAtacaveis().isEmpty());
    }

    @Test
    public void ataquesInvalidosNaoLancamExcecaoNemGastamEspadas() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        jogador.adicionarTributo(TipoTributo.ESPADA, 3);
        Cidade cidade = prepararPequim(partida, TipoTesouro.FERRO);
        assertFalse(partida.atacarCidade(CidadeID.PEQUIM, 0));
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertFalse(partida.atacarCidade(CidadeID.PEQUIM, 0));
        assertFalse(partida.atacarCidade(null, 0));
        assertFalse(partida.atacarCidade(CidadeID.KARAKORUM, 0));
        assertTrue(partida.moverPeao(ParadaID.PEQUIM1));
        assertFalse(partida.atacarCidade(CidadeID.PEQUIM, -1));
        assertFalse(partida.atacarCidade(CidadeID.PEQUIM, 1));
        assertEquals(3, jogador.getQuantidadeTributo(TipoTributo.ESPADA));
        assertEquals(1, cidade.getTesouros().size());
    }

    @Test
    public void custoDoAtaqueEhUmDepoisDoisEConquistaContinuaVisivelSemTurno() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        jogador.adicionarTributo(TipoTributo.ESPADA, 3);
        Cidade cidade = prepararPequim(partida, TipoTesouro.FERRO, TipoTesouro.BOI);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.moverPeao(ParadaID.PEQUIM1));
        assertTrue(partida.atacarCidade(CidadeID.PEQUIM, 1));
        assertEquals(2, jogador.getQuantidadeTributo(TipoTributo.ESPADA));
        assertEquals(1, jogador.getQuantidadeTesouro(TipoTesouro.BOI));
        assertEquals(Arrays.asList(TipoTesouro.FERRO), cidade.getTesouros());
        assertTrue(partida.atacarCidade(CidadeID.PEQUIM, 0));
        assertEquals(0, jogador.getQuantidadeTributo(TipoTributo.ESPADA));
        assertSame(jogador, cidade.getConquistador());
        assertEquals(Cor.VERDE, partida.getCidadesConquistadas().get(CidadeID.PEQUIM));
        assertFalse(partida.getCidades().containsKey(CidadeID.PEQUIM));
        assertTrue(partida.TerminarTurno());
        assertEquals(Cor.VERDE, partida.getCidadesConquistadas().get(CidadeID.PEQUIM));
        assertTrue(partida.getCidadesAtacaveis().isEmpty());
    }

    @Test
    public void ataqueUsaEspadaVirtualAntesDaNormal() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        jogador.adicionarTributo(TipoTributo.ESPADA, 1);
        jogador.getTabuleiroJogador().adicionarMelhoria(Regiao.PERSIA,
                new Melhoria(TipoMelhoria.ESPADA_VIRTUAL, Regiao.PERSIA));
        prepararPequim(partida, TipoTesouro.FERRO, TipoTesouro.BOI);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.moverPeao(ParadaID.PEQUIM1));
        assertTrue(partida.atacarCidade(CidadeID.PEQUIM, 0));
        assertEquals(1, jogador.getQuantidadeTributo(TipoTributo.ESPADA));
        assertEquals(0, partida.getTurnoAtual().getQuantidadeRecursoVirtual(TipoMelhoria.ESPADA_VIRTUAL));
        assertFalse(partida.atacarCidade(CidadeID.PEQUIM, 0));
        assertEquals(1, jogador.getTotalTesouros());
    }

    @Test
    public void paradaSemCidadeNaoQuebraConsultaDeAtaques() {
        Partida partida = criarPartida();
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.moverPeao(ParadaID.SARAI_KAESONG_KARAKORUM));
        assertTrue(partida.getCidadesAtacaveis().isEmpty());
        assertTrue(partida.getLocaisEntregaDisponiveis().contains(ParadaID.SARAI_KAESONG_KARAKORUM));
    }

    @Test
    public void tesourosDoAtaqueNaoPodemSerEntreguesEmKarakorumDepoisDeSair() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        // Dois votos de Karakorum, sem peça extra neste cenário.
        encontrarParada(partida, ParadaID.KARAKORUM).setBonus(null);
        prepararPequim(partida, TipoTesouro.FERRO, TipoTesouro.PELE, TipoTesouro.BOI);
        jogador.adicionarTributo(TipoTributo.ESPADA, 5);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        assertTrue(partida.moverPeao(ParadaID.PEQUIM1));
        for (int i = 0; i < 3; i++) assertTrue(partida.atacarCidade(CidadeID.PEQUIM, 0));
        assertEquals(3, jogador.getTotalTesouros());
        HashMap<TipoTesouro, Integer> entrega = partida.getExigenciaEntrega(ParadaID.KARAKORUM);
        int votosAntes = jogador.getVotos(); // A carta de quatro espadas já pode ter pontuado.
        assertFalse(partida.entregarTesouros(ParadaID.KARAKORUM, entrega));
        assertTrue(partida.getLocaisEntregaDisponiveis().isEmpty());
        assertEquals(3, jogador.getTotalTesouros());
        assertEquals(votosAntes, jogador.getVotos());
    }

    @Test
    public void khanCarregaDuasProvinciasDoDestinoSemConsumirTributoDoJogador() {
        Partida partida = criarPartida();
        assertFalse(partida.selecionarKhan(KhanID.TL));
        HashMap<ProvinciaID, Integer> antes = partida.getCargasProvincias();
        assertTrue(partida.iniciarTurno(Regiao.CHINA));
        int tributos = partida.getTurnoAtual().getQuantidadeAcao(TipoAcao.PEGAR_TRIBUTO);
        assertTrue(partida.selecionarKhan(KhanID.TL));
        assertFalse(partida.TerminarTurno());
        assertFalse(partida.moverPeao(ParadaID.PEQUIM1));
        assertFalse(partida.selecionarTributo(null));
        assertFalse(partida.selecionarTributo(ProvinciaID.BR_ESPADA));
        assertTrue(partida.selecionarTributo(ProvinciaID.TL_ESPADA));
        assertFalse(partida.selecionarTributo(ProvinciaID.TL_ESPADA));
        assertTrue(partida.getTurnoAtual().isKhanPendente());
        assertTrue(partida.selecionarTributo(ProvinciaID.TL_MOEDA));
        assertFalse(partida.getTurnoAtual().isKhanPendente());
        assertNull(partida.getKhanSelecionado());
        assertEquals(tributos, partida.getTurnoAtual().getQuantidadeAcao(TipoAcao.PEGAR_TRIBUTO));
        assertEquals(antes.get(ProvinciaID.TL_ESPADA) + 1,
                (int) partida.getCargasProvincias().get(ProvinciaID.TL_ESPADA));
        assertEquals(antes.get(ProvinciaID.TL_MOEDA) + 1,
                (int) partida.getCargasProvincias().get(ProvinciaID.TL_MOEDA));
        assertEquals(antes.get(ProvinciaID.TL_YURT), partida.getCargasProvincias().get(ProvinciaID.TL_YURT));
        assertTrue(partida.TerminarTurno());
        assertTrue(partida.iniciarTurno(Regiao.CHINA));
        assertFalse(partida.getKhans().contains(KhanID.TL));
        assertFalse(partida.selecionarKhan(KhanID.TL));
    }

    @Test
    public void destinoMelhoriasConcluiKhanSemEscolherTributos() {
        Partida partida = criarPartida();
        assertTrue(partida.iniciarTurno(Regiao.CHINA));
        assertTrue(partida.selecionarKhan(KhanID.M));
        assertFalse(partida.getTurnoAtual().isKhanPendente());
        assertNull(partida.getKhanSelecionado());
        assertTrue(partida.TerminarTurno());
        assertTrue(partida.iniciarTurno(Regiao.CHINA));
        assertFalse(partida.selecionarKhan(KhanID.M));
    }

    @Test
    public void coletaNormalContinuaFuncionandoPelaNovaEntrada() {
        Partida partida = criarPartida();
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        int antes = partida.getJogadorAtual().getQuantidadeTributo(TipoTributo.ESPADA);
        assertTrue(partida.selecionarTributo(ProvinciaID.TR_ESPADA));
        assertEquals(antes + 1, partida.getJogadorAtual().getQuantidadeTributo(TipoTributo.ESPADA));
        assertFalse(partida.selecionarTributo(ProvinciaID.TR_ESPADA));
        assertFalse(partida.selecionarKhan(KhanID.TL));
    }

    @Test
    public void fachadaNotificaSomenteAcoesAceitasEProtegeConsultasDoMapa() {
        Partida partida = criarPartida();
        prepararPequim(partida, TipoTesouro.FERRO, TipoTesouro.BOI);
        partida.getJogadorAtual().adicionarTributo(TipoTributo.ESPADA, 1);
        FachadaJogo fachada = FachadaJogo.getInstancia();
        fachada.definirPartida(partida);
        assertTrue(fachada.ativarColunaJogadorAtual(1));
        fachada.moverPeao(ParadaID.PEQUIM1);
        final int[] notificacoes = {0};
        ObservadorJogo contador = new ObservadorJogo() {
            @Override
            public void atualizar(ObservadoJogo observado) { notificacoes[0]++; }
        };
        fachada.adicionarObservador(contador);
        try {
            fachada.getCidades().get(CidadeID.PEQUIM).clear();
            fachada.getBonus().clear();
            assertEquals(2, fachada.getCidades().get(CidadeID.PEQUIM).size());
            assertEquals(7, fachada.getBonus().size());
            assertFalse(fachada.atacarCidade(CidadeID.PEQUIM, 20));
            assertFalse(fachada.selecionarKhan(KhanID.TL));
            assertEquals(0, notificacoes[0]);
            assertTrue(fachada.atacarCidade(CidadeID.PEQUIM, 0));
            assertEquals(1, notificacoes[0]);
            assertFalse(fachada.atacarCidade(CidadeID.PEQUIM, 0));
            assertEquals(1, notificacoes[0]);
        } finally {
            fachada.removerObservador(contador);
        }
    }
    @Test
    public void conquistaAbreOutraCidadeEAUltimaNaoEstouraAPilha() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        TabuleiroPrincipal tabuleiro = new TabuleiroPrincipal(new HashMap<Peao, Jogador>());
        assertEquals(3, contarCidadesReveladas(tabuleiro));
        assertFalse(tabuleiro.todasCidadesConquistadas());
        for (int conquistas = 1; conquistas <= 9; conquistas++) {
            Cidade alvo = null;
            for (CidadeID id : CidadeID.values()) {
                Cidade cidade = tabuleiro.getCidade(id);
                if (id != CidadeID.KARAKORUM && !cidade.getTesouros().isEmpty()) {
                    alvo = cidade;
                    break;
                }
            }
            assertNotNull(alvo);
            int reveladasAntes = contarCidadesReveladas(tabuleiro);
            while (alvo.getTesouros().size() > 1) {
                assertTrue(tabuleiro.atacarCidade(alvo.getId(), jogador, alvo.getTesouros().get(0)));
                assertEquals(reveladasAntes, contarCidadesReveladas(tabuleiro));
            }
            assertTrue(tabuleiro.atacarCidade(alvo.getId(), jogador, alvo.getTesouros().get(0)));
            assertSame(jogador, alvo.getConquistador());
            assertEquals(Math.min(9, 3 + conquistas), contarCidadesReveladas(tabuleiro));
            assertFalse(tabuleiro.atacarCidade(alvo.getId(), jogador, TipoTesouro.FERRO));
            assertEquals(conquistas == 9, tabuleiro.todasCidadesConquistadas());
        }
        assertNull(tabuleiro.getCidade(CidadeID.KARAKORUM).getConquistador());
    }

    private int contarCidadesReveladas(TabuleiroPrincipal tabuleiro) {
        int total = 0;
        for (CidadeID id : CidadeID.values()) {
            Cidade cidade = tabuleiro.getCidade(id);
            if (!cidade.getTesouros().isEmpty() || cidade.getConquistador() != null) total++;
        }
        return total;
    }

    @Test
    public void ataquePelaFachadaNotificaComANovaCidadeJaRevelada() {
        Partida partida = criarPartida();
        Jogador jogador = partida.getJogadorAtual();
        Cidade alvo = null;
        Parada acesso = null;
        for (ParadaID id : ParadaID.values()) {
            Parada parada = encontrarParada(partida, id);
            Cidade cidade = parada.getCidade();
            if (cidade != null && !cidade.getTesouros().isEmpty()) {
                alvo = cidade;
                acesso = parada;
                break;
            }
        }
        assertNotNull(alvo);
        alvo.getTesouros().clear();
        alvo.getTesouros().add(TipoTesouro.FERRO);
        Peao peao = jogador.getPeao(0);
        peao.getPosicao().removePeao(peao);
        peao.setPosicao(acesso);
        acesso.insertPeao(peao);
        jogador.adicionarTributo(TipoTributo.ESPADA, 1);
        assertTrue(partida.iniciarTurno(Regiao.PERSIA));
        FachadaJogo fachada = FachadaJogo.getInstancia();
        fachada.definirPartida(partida);
        final CidadeID conquistada = alvo.getId();
        final int[] notificacoes = {0};
        ObservadorJogo observador = new ObservadorJogo() {
            @Override
            public void atualizar(ObservadoJogo observado) {
                assertFalse(observado.getCidades().containsKey(conquistada));
                assertEquals(3, observado.getCidades().size());
                assertEquals(Cor.VERDE, observado.getCidadesConquistadas().get(conquistada));
                notificacoes[0]++;
            }
        };
        fachada.adicionarObservador(observador);
        try {
            assertTrue(fachada.atacarCidade(conquistada, 0));
            assertEquals(1, notificacoes[0]);
        } finally {
            fachada.removerObservador(observador);
        }
    }

    @Test
    public void posicaoAtualDoKhanPersisteAoTrocarTurno() {
        Partida partida = criarPartida();
        FachadaJogo fachada = FachadaJogo.getInstancia();
        fachada.definirPartida(partida);
        assertNull(fachada.getKhan());
        assertTrue(fachada.ativarColunaJogadorAtual(0));
        assertTrue(fachada.selecionarKhan(KhanID.TL));
        assertTrue(fachada.selecionarTributo(ProvinciaID.TL_ESPADA));
        assertTrue(fachada.selecionarTributo(ProvinciaID.TL_MOEDA));
        assertEquals(KhanID.TL, fachada.getKhan());
        assertTrue(fachada.terminarTurnoJogadorAtual());
        assertEquals(KhanID.TL, fachada.getKhan());
        assertTrue(fachada.getKhans().isEmpty());
        assertTrue(fachada.ativarColunaJogadorAtual(0));
        assertFalse(fachada.getKhans().contains(fachada.getKhan()));
        assertTrue(fachada.selecionarKhan(KhanID.M));
        assertEquals(KhanID.M, fachada.getKhan());
        assertTrue(fachada.terminarTurnoJogadorAtual());
        assertEquals(KhanID.M, fachada.getKhan());
    }
}
