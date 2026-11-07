package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TabuleiroJogadorTest {

    @Test
    public void testeMelhoriasNasRegioesCorretas() {
        TabuleiroJogador tabuleiro = new TabuleiroJogador(new Altani());

        for (Regiao regiao : Regiao.values()) {
            Melhoria melhoria = new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, regiao);
            assertTrue(tabuleiro.adicionarMelhoria(regiao, melhoria));
            assertEquals(1, tabuleiro.getQuantidadeMelhorias(regiao));
            assertSame(melhoria, tabuleiro.getMelhorias(regiao).get(0));
        }
    }

    @Test
    public void testeRejeitaMelhoriaDeOutraRegiao() {
        TabuleiroJogador tabuleiro = new TabuleiroJogador(new Altani());
        Melhoria melhoria = new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, Regiao.CHINA);

        assertFalse(tabuleiro.adicionarMelhoria(Regiao.PERSIA, melhoria));
        assertFalse(tabuleiro.adicionarMelhoria(Regiao.RUSSIA, melhoria));
        for (Regiao regiao : Regiao.values()) {
            assertTrue(tabuleiro.getMelhorias(regiao).isEmpty());
        }
    }

    @Test
    public void testeColunaBrancaAceitaTodasAsRegioes() {
        TabuleiroJogador tabuleiro = new TabuleiroJogador(new Altani());
        int indice = 0;
        for (Regiao regiao : Regiao.values()) {
            Melhoria melhoria = new Melhoria(TipoMelhoria.MOVIMENTO_EXTRA, regiao);
            assertTrue(tabuleiro.adicionarMelhoriaBranca(melhoria));
            assertSame(melhoria, tabuleiro.getMelhoriasColunaBranca().get(indice++));
            assertTrue(tabuleiro.getMelhorias(regiao).isEmpty());
        }
    }

    @Test
    public void testeRecuperacaoFichasColunaBranca() {

        // Cria o tabuleiro de Altani
        Personagem personagem = new Altani();

        TabuleiroJogador tabuleiro = new TabuleiroJogador(personagem);

        // O jogador começa com quatro fichas
        assertEquals(4, tabuleiro.getFichasAtivacaoDisponiveis());

        // Ativa duas colunas
        assertNotNull(tabuleiro.ativarColuna(Regiao.RUSSIA));
        assertNotNull(tabuleiro.ativarColuna(Regiao.CHINA));

        // Deve restar apenas duas fichas
        assertEquals(2, tabuleiro.getFichasAtivacaoDisponiveis());

        // As colunas utilizadas estão ativadas
        assertTrue(tabuleiro.colunaEstaAtivada(Regiao.RUSSIA));
        assertTrue(tabuleiro.colunaEstaAtivada(Regiao.CHINA));

        // Não pode ativar novamente uma coluna já ativada
        assertNull(tabuleiro.ativarColuna(Regiao.RUSSIA));

        // A tentativa inválida não deve consumir uma ficha
        assertEquals(2, tabuleiro.getFichasAtivacaoDisponiveis());

        // Ativa a coluna branca
        assertNotNull(tabuleiro.ativarColunaBranca());

        // Todas as fichas são recuperadas
        assertEquals(4, tabuleiro.getFichasAtivacaoDisponiveis());

        // As colunas ficam disponíveis novamente
        assertFalse(tabuleiro.colunaEstaAtivada(Regiao.RUSSIA));
        assertFalse(tabuleiro.colunaEstaAtivada(Regiao.CHINA));

        // Agora é possível ativar a Rússia novamente
        assertNotNull(tabuleiro.ativarColuna(Regiao.RUSSIA));

        assertEquals(3, tabuleiro.getFichasAtivacaoDisponiveis());
    }
}
