package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.junit.Test;

public class TabuleiroPrincipalTest {

	@Test
	public void testTabuleiroPrincipal() {
		HashMap<Peao, Jogador> peoes = new HashMap<Peao, Jogador>();
		Jogador jogador = criarJogadorTeste();
		peoes.put(new Peao(jogador), jogador);
		TabuleiroPrincipal tabuleiro = new TabuleiroPrincipal(peoes, new Stack<Melhoria>());
		HashMap<ParadaID, Parada> mapa = tabuleiro.getMapa();
		for(Parada p: mapa.values()) {
			for(Parada v: p.getVizinhos().values()) assertTrue(v.getVizinhos().values().contains(p));
		}
	}

	@Test
	public void testMoverPeao() {
		HashMap<Peao, Jogador> peoes = new HashMap<Peao, Jogador>();
		Jogador jogador = criarJogadorTeste();
		Peao peao = new Peao(jogador);
		peoes.put(peao, jogador);
		HashSet<Provincia> provincias = new HashSet<Provincia>();
		HashSet<Provincia> refProvincias = new HashSet<Provincia>();
		TabuleiroPrincipal tabuleiro = new TabuleiroPrincipal(peoes, new Stack<Melhoria>());
		refProvincias.add(tabuleiro.getProvincias().get(ProvinciaID.TR_ESPADA));
		refProvincias.add(tabuleiro.getProvincias().get(ProvinciaID.BL_YURT));
		refProvincias.add(tabuleiro.getProvincias().get(ProvinciaID.BR_MOEDA));
		refProvincias.add(tabuleiro.getProvincias().get(ProvinciaID.BR_YURT));
		refProvincias.add(tabuleiro.getProvincias().get(ProvinciaID.BR_ESPADA));
		
		assertEquals(1, tabuleiro.getMapa().get(ParadaID.KARAKORUM).getNumPeoes());
		assertEquals(0, tabuleiro.getMapa().get(ParadaID.PEQUIM1).getNumPeoes());
		assertEquals(tabuleiro.getMapa().get(ParadaID.KARAKORUM), peao.getPosicao());
		
		provincias.addAll(tabuleiro.moverPeao(peao, ParadaID.PEQUIM1));
		assertEquals(0, tabuleiro.getMapa().get(ParadaID.KARAKORUM).getNumPeoes());
		assertEquals(1, tabuleiro.getMapa().get(ParadaID.PEQUIM1).getNumPeoes());
		assertEquals(tabuleiro.getMapa().get(ParadaID.PEQUIM1), peao.getPosicao());
		
		Parada parada = tabuleiro.getMapa().get(ParadaID.PEQUIM_CABUL_CANTAO);
		provincias.addAll(tabuleiro.moverPeao(peao, parada));
		assertEquals(0, tabuleiro.getMapa().get(ParadaID.PEQUIM1).getNumPeoes());
		assertEquals(1, tabuleiro.getMapa().get(ParadaID.PEQUIM_CABUL_CANTAO).getNumPeoes());
		assertEquals(tabuleiro.getMapa().get(ParadaID.PEQUIM_CABUL_CANTAO), peao.getPosicao());
		HashSet<ParadaID> vizinhos = new HashSet<ParadaID>();
		vizinhos.add(ParadaID.PEQUIM3);
		vizinhos.add(ParadaID.CABUL2);
		vizinhos.add(ParadaID.CANTAO3);
		assertEquals(vizinhos, peao.getPosicao().getVizinhos().keySet());
		System.out.println(provincias);
		assertEquals(refProvincias, provincias);
	}

	@Test
	public void testGetMovimentosPossiveis() {
		HashMap<Peao, Jogador> peoes = new HashMap<Peao, Jogador>();
		Jogador jogador1 = criarJogadorTeste();
		Jogador jogador2 = criarJogadorTeste();
		Peao peao1 = new Peao(jogador1);
		Peao peao2 = new Peao(jogador2);
		peoes.put(peao1, jogador1);
		peoes.put(peao2, jogador2);
		TabuleiroPrincipal tabuleiro = new TabuleiroPrincipal(peoes, new Stack<Melhoria>());
		HashMap<ParadaID, Parada> mapa = tabuleiro.getMapa();
		tabuleiro.moverPeao(peao1, ParadaID.CANTAO2);
		tabuleiro.moverPeao(peao2, ParadaID.CANTAO3);
		mapa.get(ParadaID.CANTAO1).setYurt(jogador1);
		HashMap<Parada, Integer> possiveis1 = new HashMap<Parada,Integer>();
		possiveis1.put(mapa.get(ParadaID.CANTAO1), 1);
		possiveis1.put(mapa.get(ParadaID.PEQUIM2), 1);
		possiveis1.put(mapa.get(ParadaID.KAESONG_CANTAO), 1);
		HashMap<Parada, Integer> possiveis2 = new HashMap<Parada,Integer>();
		possiveis2.putAll(possiveis1);
		possiveis2.put(mapa.get(ParadaID.PEQUIM_CABUL_CANTAO), 2);
		possiveis2.put(mapa.get(ParadaID.PEQUIM1), 2);
		possiveis2.put(mapa.get(ParadaID.PEQUIM3), 2);
		possiveis2.put(mapa.get(ParadaID.KAESONG3), 2);
		assertEquals(possiveis1, tabuleiro.getMovimentosPossiveis(peao1, 1));
		assertEquals(possiveis2, tabuleiro.getMovimentosPossiveis(peao1, 2));

	}
	
	private Jogador criarJogadorTeste() {

	    Personagem personagem = new Altani();

	    TabuleiroJogador tabuleiro = new TabuleiroJogador(personagem);

	    ArrayList<ConselheiroSecreto> conselheiros = new ArrayList<>();

	    conselheiros.add(new ConselheiroSecreto());
	    conselheiros.add(new ConselheiroSecreto());

	    return new Jogador(conselheiros, tabuleiro);
	}

}

