package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.junit.Test;

public class TabuleiroPrincipalTest {

	@Test
	public void testTabuleiroPrincipal() {
		/*
		 * Testa o construtor do tabuleiro principal. Responsavel por inicializar o tabuleiro da forma correta.
		 * Neste teste, e verificado que cada parada esta na lista de vizinhos de cada uma das paradas da sua propria lista de vizinhos.
		 * Dessa forma, e testado as conexoes do grafo que representa o mapa do tabuleiro
		 */
		HashMap<Peao, Jogador> peoes = new HashMap<Peao, Jogador>();
		Jogador jogador = new Jogador();
		peoes.put(new Peao(jogador), jogador);
		TabuleiroPrincipal tabuleiro = new TabuleiroPrincipal(peoes, new Stack<Melhoria>());
		HashMap<ParadaID, Parada> mapa = tabuleiro.getMapa();
		for(Parada p: mapa.values()) {
			for(Parada v: p.getVizinhos().values()) assertTrue(v.getVizinhos().values().contains(p));
		}
	}

	@Test
	public void testMoverPeao() {
		/*
		 * Testa a funcao moverPeao, verificando se os objetos associados (Peao e parada) são atualizados corretamente e o valor de retorno.
		 * Nesse teste, inicializa-se um peao e o tabuleiro. O peao e entao movido para a parada Pequim1 e depois Pequim_Cabul_Cantao.
		 * A cada movimento, os objetos são verificados para que suas atualizaçoes sejam feitas de forma correta.
		 * Ainda, o valor de retorno da funcao moverPeao, que representa o conjunto das provincias vizinhas à cidade alvo do movimento, é acrescentado num conjunto
		 * que armazena todas as provincias retornadas durante o teste.
		 * Por fim, o conjunto de provincias resultante e comparado com um conjunto de provincias criado e construido manualmente como referencia.
		 * O teste verifica se o conjunto resultante e equivalente ao de referencia.
		 * 
		 */
		
		
		
		HashMap<Peao, Jogador> peoes = new HashMap<Peao, Jogador>();
		Jogador jogador = new Jogador();
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
		/*
		 * Teste da funcao getMovimentosPossiveis, que retorna um hashMap que associa as paradas alcancaveis por um peao e o numero de movimentos necessarios para alcanca-la.
		 * O numero maximo de movimentos e passado como argumento.
		 * Neste teste, sao inicializados dois jogadores. Um deles e estatico e usado como obstaculo. Ainda, uma das paradas adjacentes ao jogador que se movimenta tem um yurt do proprio
		 * A funcao e chamada duas vezes, uma com o maximo de 1 movimento e uma com o maximo de 2 movimentos.
		 * O retorno e comparado com dois HashMaps construidos manualmente.
		 */
		HashMap<Peao, Jogador> peoes = new HashMap<Peao, Jogador>();
		Jogador jogador1 = new Jogador();
		Jogador jogador2 = new Jogador();
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

}

