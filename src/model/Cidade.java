package model;

import java.util.HashMap;
import java.util.Stack;

class Cidade {
	private final static int MAX_TESOUROS = 4;
	private HashMap<TipoTesouro, Integer> tesouros = new HashMap<TipoTesouro, Integer>();
	private Jogador conquistador = null;
	private Regiao regiao = null;
	
	protected void revelar(Stack<TipoTesouro> pilhaTesouros) {
		for(int i = 0; i<MAX_TESOUROS; ++i) {
			TipoTesouro tempTesouro = pilhaTesouros.pop();
			if(tesouros.keySet().contains(tempTesouro)) tesouros.put(tempTesouro, tesouros.get(tempTesouro) + 1);
			else tesouros.put(tempTesouro, 1);
		}
		return;
	}
	
	public void atacar(Jogador jogador, TipoTesouro tesouro) {
		int quantidade = tesouros.get(tesouro);
		quantidade--;
		tesouros.put(tesouro, quantidade);
		if(quantidade<=0) tesouros.remove(tesouro);
		if(tesouros.isEmpty()) conquistador = jogador;
		return;
	}
	
	public HashMap<TipoTesouro, Integer> getTesouros() {
		return this.tesouros;
	}
	
	public int getContadorDeTesouros() {
		return tesouros.size();
	}
	
	public Jogador getConquistador() {
		return this.conquistador;
	}
	
	public void setRegiao(Regiao regiao) {
		this.regiao = regiao;
	}
	
	public Regiao getRegiao() {
		return this.regiao;
	}

}
