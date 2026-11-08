package model;

import static org.junit.Assert.*;
import org.junit.Test;

public class AcoesColunasTest {
    @Test
    public void acoesDasVinteColunasCorrespondemAsCartelas() {
        Personagem[] personagens = {
            new Altani(), new Chagatai(), new Jochi(), new Ogedei(), new Tolui()
        };
        Regiao[] regioes = {Regiao.CHINA, Regiao.PERSIA, Regiao.RUSSIA, null};
        // Movimento, tributo, Khan e moeda virtual, na ordem das cartelas.
        int[][][] esperado = {
            {{1,2,1,0}, {2,1,0,0}, {1,1,0,1}, {2,0,1,0}},
            {{1,2,1,0}, {1,1,0,1}, {2,1,0,0}, {2,1,1,0}},
            {{1,1,0,1}, {1,1,1,0}, {2,1,0,0}, {2,1,1,0}},
            {{2,1,0,0}, {1,1,0,1}, {1,1,1,0}, {2,1,1,0}},
            {{1,1,0,1}, {2,1,0,0}, {1,1,1,0}, {2,1,1,0}}
        };
        for (int p = 0; p < personagens.length; p++) {
            for (int c = 0; c < regioes.length; c++) {
                TabuleiroJogador tabuleiro = new TabuleiroJogador(personagens[p]);
                Turno turno = new Turno(null, regioes[c] == null
                        ? tabuleiro.ativarColunaBranca()
                        : tabuleiro.ativarColuna(regioes[c]));
                String contexto = personagens[p].getNome() + " coluna " + c;
                int[] valores = esperado[p][c];
                assertEquals(contexto, valores[0], turno.getQuantidadeAcao(TipoAcao.MOVIMENTO));
                assertEquals(contexto, valores[1], turno.getQuantidadeAcao(TipoAcao.PEGAR_TRIBUTO));
                assertEquals(contexto, valores[2] == 1, turno.isKhanPendente());
                assertEquals(contexto, valores[3], turno.getQuantidadeRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL));
                for (int m = 0; m < valores[0]; m++) {
                    assertTrue(contexto, turno.usarAcao(TipoAcao.MOVIMENTO));
                }
                assertFalse(contexto, turno.usarAcao(TipoAcao.MOVIMENTO));
            }
        }
    }

    @Test
    public void melhoriaSomaMovimentosAColunaDoChagatai() {
        TabuleiroJogador tabuleiro = new TabuleiroJogador(new Chagatai());
        assertTrue(tabuleiro.adicionarMelhoria(Regiao.CHINA,
                new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.CHINA)));
        Turno turno = new Turno(null, tabuleiro.ativarColuna(Regiao.CHINA));
        assertEquals(3, turno.getQuantidadeAcao(TipoAcao.MOVIMENTO));
    }

    @Test
    public void moedaVirtualBaseSomaComMelhoria() {
        TabuleiroJogador tabuleiro = new TabuleiroJogador(new Chagatai());
        assertTrue(tabuleiro.adicionarMelhoria(Regiao.PERSIA,
                new Melhoria(TipoMelhoria.MOEDA_VIRTUAL, Regiao.PERSIA)));
        Turno turno = new Turno(null, tabuleiro.ativarColuna(Regiao.PERSIA));
        assertEquals(2, turno.getQuantidadeRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL));
        assertTrue(turno.usarRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL));
        assertTrue(turno.usarRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL));
        assertFalse(turno.usarRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL));
    }
}
