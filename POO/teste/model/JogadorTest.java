package model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class JogadorTest {

    @Test
    public void testeEntregaTesouros() {

        Jogador jogador = criarJogadorTeste();

        // Adiciona os tesouros ao inventário
        jogador.adicionarTesouro(TipoTesouro.BOI);
        jogador.adicionarTesouro(TipoTesouro.BOI);
        jogador.adicionarTesouro(TipoTesouro.FERRO);
        jogador.adicionarTesouro(TipoTesouro.TECIDO);

        // Tenta entregar mais ferros do que possui
        HashMap<TipoTesouro, Integer> entregaInvalida = new HashMap<>();

        entregaInvalida.put(TipoTesouro.BOI, 2);
        entregaInvalida.put(TipoTesouro.FERRO, 2);

        assertFalse(jogador.gastarTesouros(entregaInvalida));

        // Verifica que nenhum tesouro foi removido
        assertEquals(2, jogador.getQuantidadeTesouro(TipoTesouro.BOI));
        assertEquals(1, jogador.getQuantidadeTesouro(TipoTesouro.FERRO));
        assertEquals(1, jogador.getQuantidadeTesouro(TipoTesouro.TECIDO));

        // Agora realiza uma entrega válida
        HashMap<TipoTesouro, Integer> entregaValida = new HashMap<>();

        entregaValida.put(TipoTesouro.BOI, 2);
        entregaValida.put(TipoTesouro.FERRO, 1);

        assertTrue(jogador.gastarTesouros(entregaValida));

        // Verifica as quantidades restantes
        assertEquals(0, jogador.getQuantidadeTesouro(TipoTesouro.BOI));
        assertEquals(0, jogador.getQuantidadeTesouro(TipoTesouro.FERRO));
        assertEquals(1, jogador.getQuantidadeTesouro(TipoTesouro.TECIDO));
    }
    
    @Test
    public void testeCriacaoPeoesAltani() {

        Jogador jogador = criarJogadorTeste();

        // Altani deve possuir dois peões
        assertEquals(2, jogador.getQuantidadePeoes());

        // Os dois peões devem pertencer ao jogador
        assertSame(jogador, jogador.getPeao(0).getJogador());
        assertSame(jogador, jogador.getPeao(1).getJogador());

        // Os peões devem ser objetos diferentes
        assertNotSame(jogador.getPeao(0), jogador.getPeao(1));
    }
    

    private Jogador criarJogadorTeste() {

        // Cria o personagem e o tabuleiro
        Personagem personagem = new Altani();

        TabuleiroJogador tabuleiro = new TabuleiroJogador(personagem);

        // Cria as duas opções de conselheiro secreto
        ArrayList<ConselheiroSecreto> conselheiros = new ArrayList<>();

        conselheiros.add(new ConselheiroSecreto(TipoConselheiroSecreto.values()[0]));
        conselheiros.add(new ConselheiroSecreto(TipoConselheiroSecreto.values()[1]));

        // Cria o jogador (os peões são criados automaticamente)
        return new Jogador(conselheiros, tabuleiro);
    }
}