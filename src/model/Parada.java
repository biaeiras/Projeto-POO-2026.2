package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Parada {
	private HashMap<ParadaID, Parada> vizinhos = new HashMap<ParadaID, Parada>();
	private HashMap<ProvinciaID, Provincia> provincias = new HashMap<ProvinciaID, Provincia>();
	private ArrayList<Jogador> yurt = new ArrayList<Jogador>();
	private int multiplicidade = 1;
	private Cidade cidade = null;
	private HashSet<Peao> peoes = new HashSet<Peao>();
	private TipoBonus bonus = null;
    private HashMap<TipoTesouro, Integer> exigenciaConselheiro;
	private ParadaID id;
	
	Parada(ParadaID id){
		this.id = id;
	}
	
	int getNumPeoes() {
		return peoes.size();
	}
 	void insertVizinhos(ParadaID id, Parada parada) {
		this.vizinhos.put(id, parada);
		return;
	}
	void insertProvincia(ProvinciaID nome, Provincia provincia) {
		this.provincias.put(nome, provincia);
	}
	void setYurt(ArrayList<Jogador> yurt) {
		this.yurt = yurt;
		return;
	}
	void setMultiplicidade(int multiplicidade) {
		this.multiplicidade = multiplicidade;
		return;
	}
	void setCidade(Cidade cidade) {
		this.cidade = cidade;
		return;
	}
	void setBonus(TipoBonus bonus) {
		this.bonus = bonus;
		return;
	}
	HashMap<ParadaID, Parada> getVizinhos(){
		return this.vizinhos;
	}
	HashMap<ProvinciaID, Provincia> getProvincias(){
		return this.provincias;
	}
	ArrayList<Jogador> getYurt() {
		return this.yurt;
	}
	int getMultiplicidade() {
		return this.multiplicidade;
	}
	Cidade getCidade() {
		return this.cidade;
	}
	boolean isMoveble() {
		return (peoes.size() < multiplicidade);
	}
	void insertPeao(Peao p) {
		this.peoes.add(p);
		return;
	}
	void removePeao(Peao p) {
		this.peoes.remove(p);
		return;
	}
	TipoBonus getBonus() {
		return this.bonus;
	}
    // Sem tipos específicos, o conselheiro aceita três tesouros quaisquer.
    void configurarConselheiro(TipoTesouro... tesouros) {
        exigenciaConselheiro = new HashMap<>();
        for (TipoTesouro tipo : tesouros) {
            exigenciaConselheiro.put(tipo,
                    exigenciaConselheiro.getOrDefault(tipo, 0) + 1);
        }
    }

    boolean ehConselheiro() {
        return exigenciaConselheiro != null;
    }

    HashMap<TipoTesouro, Integer> getExigenciaConselheiro() {
        return ehConselheiro() ? new HashMap<>(exigenciaConselheiro) : new HashMap<>();
    }

    int getVotosEntrega() {
        return !ehConselheiro() ? 0 : (id == ParadaID.KARAKORUM ? 2 : 1);
    }

    boolean aceitaEntrega(HashMap<TipoTesouro, Integer> entrega) {
        if (!ehConselheiro() || entrega == null || entrega.isEmpty()) {
            return false;
        }
        long total = 0;
        for (TipoTesouro tipo : entrega.keySet()) {
            Integer quantidade = entrega.get(tipo);
            if (tipo == null || quantidade == null || quantidade <= 0) {
                return false;
            }
            total += quantidade;
        }
        return exigenciaConselheiro.isEmpty()
                ? total == 3 : exigenciaConselheiro.equals(entrega);
    }

	boolean podeReceberYurt() {
	    return id!=ParadaID.KARAKORUM&&yurt.size()<multiplicidade;
	}

	ParadaID getId() {
		return id;
	}
	boolean adicionarYurt(Jogador jogador) {
	    if (jogador != null && podeReceberYurt()) {
	        yurt.add(jogador);
	        return true;
	    }
	    return false;
	}

}
