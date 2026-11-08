package model;

import java.util.HashMap;
import java.util.HashSet;

class Turno {

	private Jogador jogador;

	private HashMap<TipoAcao, Integer> acoesDisponiveis;

	private HashMap<TipoMelhoria, Integer> recursosVirtuais;

	private boolean khanPendente;

	private HashSet<Provincia> provinciasVisitadas;
	
	Turno(Jogador jogador, HashMap<Enum<?>, Integer> resultadoColuna) {

	    this.jogador = jogador;

	    this.acoesDisponiveis = new HashMap<>();
	    this.recursosVirtuais = new HashMap<>();

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
	                    recursosVirtuais.put(
	                        melhoria,
	                        quantidade
	                    );
	                    break;
	            }
	        }
	    }
	}
	
	Jogador getJogador() {
	    return jogador;
	}
	
	private void adicionarAcao(TipoAcao tipo, int quantidade) {

	    int atual = acoesDisponiveis.getOrDefault(tipo, 0);

	    acoesDisponiveis.put(tipo, atual + quantidade);
	}
	
	int getQuantidadeAcao(TipoAcao tipo) {
	    return acoesDisponiveis.getOrDefault(tipo, 0);
	}
	
	boolean usarAcao(TipoAcao tipo) {

	    int quantidade = getQuantidadeAcao(tipo);

	    if (quantidade <= 0) {
	        return false;
	    }

	    acoesDisponiveis.put(tipo, quantidade - 1);

	    return true;
	}
	
	int getQuantidadeRecursoVirtual(TipoMelhoria tipo) {
	    return recursosVirtuais.getOrDefault(tipo, 0);
	}

	boolean usarRecursoVirtual(TipoMelhoria tipo) {

	    int quantidade = getQuantidadeRecursoVirtual(tipo);

	    if (quantidade <= 0) {
	        return false;
	    }

	    recursosVirtuais.put(tipo, quantidade - 1);

	    return true;
	}
	
	boolean isKhanPendente() {
	    return khanPendente;
	}
	
	void concluirAcaoKhan() {
	    khanPendente = false;
	}
	
	void adicionarProvinciasVisitadas(HashSet<Provincia> provincias) {

	    provinciasVisitadas.addAll(provincias);
	}
	
	boolean visitouProvincia(Provincia provincia) {

	    return provinciasVisitadas.contains(provincia);
	}
	
	HashSet<Provincia> getProvinciasVisitadas() {

	    return new HashSet<>(provinciasVisitadas);
	}
}
