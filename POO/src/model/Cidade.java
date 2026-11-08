package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

class Cidade {
	private final static int MAX_TESOUROS = 4;
	private ArrayList<TipoTesouro> tesouros = new ArrayList<>();
	private Jogador conquistador = null;
	private Regiao regiao = null;
	private CidadeID id;
	
	Cidade(CidadeID id){
		this.id = id;
	}
	
	protected void revelar(Stack<TipoTesouro> pilhaTesouros) {
		for(int i = 0; i<MAX_TESOUROS; ++i) {
			TipoTesouro tempTesouro = pilhaTesouros.pop();
			tesouros.add(tempTesouro);
		}
		return;
	}
	
	void atacar(Jogador jogador, TipoTesouro tesouro) {
		tesouros.remove(tesouro);
		if(tesouros.isEmpty()) conquistador = jogador;
		return;
	}
	
	ArrayList<TipoTesouro> getTesouros() {
		return this.tesouros;
	}
	
	int getContadorDeTesouros() {
		return tesouros.size();
	}
	
	Jogador getConquistador() {
		return this.conquistador;
	}
	
	void setRegiao(Regiao regiao) {
		this.regiao = regiao;
	}
	
	Regiao getRegiao() {
		return this.regiao;
	}
	
	CidadeID getId() {
		return id;
	}
	
	
}