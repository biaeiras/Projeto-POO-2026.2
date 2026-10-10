package model;

import java.util.HashMap;
import java.util.HashSet;

class Turno {

	private Jogador jogador;

	private HashMap<TipoAcao, Integer> acoesDisponiveis;

	private HashMap<TipoMelhoria, Integer> melhoriasDisponiveis;

	private boolean khanPendente;

	private HashSet<Provincia> provinciasVisitadas;
	
	Turno(Jogador jogador, HashMap<Enum<?>, Integer> resultadoColuna) {

	    this.jogador = jogador;

	    this.acoesDisponiveis = new HashMap<>();
	    this.melhoriasDisponiveis = new HashMap<>();

	    this.khanPendente = false;

	    this.provinciasVisitadas = new HashSet<>();

	    processarResultadoColuna(resultadoColuna);
	}
	
	private void processarResultadoColuna(
	        HashMap<Enum<?>, Integer> resultado) {

	    for (Enum<?> chave : resultado.keySet()) {

	        int quantidade = resultado.get(chave);

	        if (chave instanceof TipoAcao) {

	            TipoAcao acao = (TipoAcao) chave;

	            if (acao == TipoAcao.USAR_KHAN) {
	                khanPendente = quantidade > 0;
	            } else {
	                adicionarAcao(acao, quantidade);
	            }

	        } else if (chave instanceof TipoMelhoria) {

	            TipoMelhoria melhoria = (TipoMelhoria) chave;

	            switch (melhoria) {

	                case MOVIMENTO_EXTRA:
	                    adicionarAcao(
	                        TipoAcao.MOVIMENTO,
	                        quantidade * 2
	                    );
	                    break;

	                case PEGAR_TRIBUTO_EXTRA:
	                    adicionarAcao(
	                        TipoAcao.PEGAR_TRIBUTO,
	                        quantidade
	                    );
	                    break;

	                default:
	                    melhoriasDisponiveis.put(
	                        melhoria,
	                        quantidade
	                    );
	                    break;
	            }
	        }
	    }
	}
	
	private void adicionarAcao(TipoAcao tipo, int quantidade) {

	    int atual = acoesDisponiveis.getOrDefault(tipo, 0);

	    acoesDisponiveis.put(tipo, atual + quantidade);
	}
}
