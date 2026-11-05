package model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class ConselheiroAbertoTest {

    private Jogador criarJogador() {
        ArrayList<ConselheiroSecreto> opcoes = new ArrayList<>();
        opcoes.add(new ConselheiroSecreto(TipoConselheiroSecreto.CIDADAO));
        opcoes.add(new ConselheiroSecreto(TipoConselheiroSecreto.SOLDADO));
        return new Jogador(opcoes, new TabuleiroJogador(new Altani()));
    }

    private void prepararDemanda(TipoConselheiroAberto tipo, Jogador jogador,
            TabuleiroPrincipal tabuleiro) {
        switch (tipo) {
            case QUATRO_ESPADAS:
                jogador.adicionarTributo(TipoTributo.ESPADA, 4);
                break;
            case SEIS_YURTS:
            case DEZ_YURTS:
                int minimo = tipo == TipoConselheiroAberto.SEIS_YURTS ? 6 : 10;
                int total = 0;
                for (Parada parada : tabuleiro.getMapa().values()) {
                    if (parada.podeReceberYurt()) {
                        tabuleiro.adicionarYurt(jogador, parada);
                        if (++total == minimo) {
                            break;
                        }
                    }
                }
                break;
            case QUATRO_TESOUROS:
                for (int i = 0; i < 4; i++) {
                    jogador.adicionarTesouro(TipoTesouro.FERRO);
                }
                break;
            case CINCO_TESOUROS_DIFERENTES:
                for (TipoTesouro tesouro : TipoTesouro.values()) {
                    jogador.adicionarTesouro(tesouro);
                }
                break;
            case UM_YURT_POR_REGIAO:
            case DOIS_YURTS_POR_REGIAO:
                int porRegiao = tipo == TipoConselheiroAberto.UM_YURT_POR_REGIAO ? 1 : 2;
                for (Regiao regiao : Regiao.values()) {
                    int construidos = 0;
                    for (Parada parada : tabuleiro.getMapa().values()) {
                        if (parada.getCidade() != null
                                && parada.getCidade().getRegiao() == regiao) {
                            tabuleiro.adicionarYurt(jogador, parada);
                            if (++construidos == porRegiao) {
                                break;
                            }
                        }
                    }
                }
                break;
            case DUAS_PECAS_BONUS:
            case TRES_PECAS_BONUS:
                jogador.adicionarPecaBonus(new PecaBonus(TipoBonus.DUAS_ESPADAS));
                jogador.adicionarPecaBonus(new PecaBonus(TipoBonus.DUAS_MOEDAS));
                if (tipo == TipoConselheiroAberto.TRES_PECAS_BONUS) {
                    jogador.adicionarPecaBonus(new PecaBonus(TipoBonus.DOIS_YURTS));
                }
                break;
            case CONQUISTAR_DUAS_CIDADES:
            case CONQUISTAR_TRES_CIDADES:
                int cidades = tipo == TipoConselheiroAberto.CONQUISTAR_DUAS_CIDADES ? 2 : 3;
                ParadaID[] paradas = {ParadaID.KIEV1, ParadaID.MOSCOU1, ParadaID.SARAI1};
                for (int i = 0; i < cidades; i++) {
                    Cidade cidade = tabuleiro.getMapa().get(paradas[i]).getCidade();
                    cidade.getTesouros().clear();
                    cidade.getTesouros().put(TipoTesouro.FERRO, 1);
                    cidade.atacar(jogador, TipoTesouro.FERRO);
                }
                break;
            case LINHA_DE_MELHORIAS:
                for (Regiao regiao : Regiao.values()) {
                    jogador.getTabuleiroJogador().adicionarMelhoria(regiao,
                        new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, regiao));
                }
                break;
            case DUAS_COLUNAS_COM_DUAS_MELHORIAS:
                for (int i = 0; i < 2; i++) {
                    jogador.getTabuleiroJogador().adicionarMelhoria(Regiao.CHINA,
                        new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.CHINA));
                    jogador.getTabuleiroJogador().adicionarMelhoriaBranca(
                        new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.RUSSIA));
                }
                break;
            case QUATRO_MELHORIAS_NA_COLUNA:
                for (int i = 0; i < 4; i++) {
                    jogador.getTabuleiroJogador().adicionarMelhoriaBranca(
                        new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.RUSSIA));
                }
                break;
            default:
                fail("Faltou cenário para " + tipo);
        }
    }

    @Test
    public void todasAsDemandasExigemCondicaoEReconhecemOEstadoCorreto() {
        for (TipoConselheiroAberto tipo : TipoConselheiroAberto.values()) {
            Jogador jogador = criarJogador();
            TabuleiroPrincipal tabuleiro = new TabuleiroPrincipal(new HashMap<Peao, Jogador>());
            ConselheiroAberto carta = new ConselheiroAberto(tipo);

            assertFalse("Não pode cumprir sem requisitos: " + tipo,
                carta.demandaCumprida(jogador, tabuleiro));
            prepararDemanda(tipo, jogador, tabuleiro);
            assertTrue("Deve reconhecer a demanda: " + tipo,
                carta.demandaCumprida(jogador, tabuleiro));

            carta.marcarCumprida(jogador);
            assertSame(jogador, carta.getCumpridaPor());
            assertFalse("Carta cumprida não deve ser concedida novamente: " + tipo,
                carta.demandaCumprida(jogador, tabuleiro));
        }
    }

    @Test
    public void tesourosRepetidosNaoContamComoCincoTipos() {
        Jogador jogador = criarJogador();
        TabuleiroPrincipal tabuleiro = new TabuleiroPrincipal(new HashMap<Peao, Jogador>());
        for (int i = 0; i < 5; i++) {
            jogador.adicionarTesouro(TipoTesouro.FERRO);
        }
        assertTrue(new ConselheiroAberto(TipoConselheiroAberto.QUATRO_TESOUROS)
            .demandaCumprida(jogador, tabuleiro));
        assertFalse(new ConselheiroAberto(TipoConselheiroAberto.CINCO_TESOUROS_DIFERENTES)
            .demandaCumprida(jogador, tabuleiro));
    }

    @Test
    public void bonusUsadoContinuaContandoSemDuplicarAPeca() {
        Jogador jogador = criarJogador();
        PecaBonus peca = new PecaBonus(TipoBonus.DUAS_ESPADAS);
        assertTrue(jogador.adicionarPecaBonus(peca));
        peca.usar();
        assertFalse(jogador.adicionarPecaBonus(peca));
        assertFalse(jogador.adicionarPecaBonus(new PecaBonus(TipoBonus.DUAS_ESPADAS)));
        assertEquals(1, jogador.getQuantidadePecasBonus());
    }

    @Test
    public void yurtsNoEstoqueNaoContamComoConstruidos() {
        Jogador jogador = criarJogador();
        jogador.adicionarTributo(TipoTributo.YURT, 10);
        TabuleiroPrincipal tabuleiro = new TabuleiroPrincipal(new HashMap<Peao, Jogador>());
        assertFalse(new ConselheiroAberto(TipoConselheiroAberto.SEIS_YURTS)
            .demandaCumprida(jogador, tabuleiro));
    }

    @Test
    public void linhaDeMelhoriasExigeAsTresColunasColoridas() {
        Jogador jogador = criarJogador();
        TabuleiroPrincipal tabuleiro = new TabuleiroPrincipal(new HashMap<Peao, Jogador>());
        TabuleiroJogador cartela = jogador.getTabuleiroJogador();
        cartela.adicionarMelhoria(Regiao.CHINA,
            new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.CHINA));
        cartela.adicionarMelhoria(Regiao.PERSIA,
            new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.PERSIA));
        cartela.adicionarMelhoriaBranca(
            new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.RUSSIA));
        assertFalse(new ConselheiroAberto(TipoConselheiroAberto.LINHA_DE_MELHORIAS)
            .demandaCumprida(jogador, tabuleiro));
    }
}
