package model;

import java.util.HashMap;
import java.util.HashSet;

class Parada {
	private HashMap<ParadaID, Parada> vizinhos = new HashMap<ParadaID, Parada>();
	private HashMap<ProvinciaID, Provincia> provincias = new HashMap<ProvinciaID, Provincia>();
	private Jogador yurt = null;
	private int multiplicidade = 1;
	private Cidade cidade = null;
	private HashSet<Peao> peoes = new HashSet<Peao>();
	private TipoBonus bonus = null;
	
	public int getNumPeoes() {
		return peoes.size();
	}
 	public void insertVizinhos(ParadaID id, Parada parada) {
		this.vizinhos.put(id, parada);
		return;
	}
	public void insertProvincia(ProvinciaID nome, Provincia provincia) {
		this.provincias.put(nome, provincia);
	}
	public void setYurt(Jogador yurt) {
		this.yurt = yurt;
		return;
	}
	public void setMultiplicidade(int multiplicidade) {
		this.multiplicidade = multiplicidade;
		return;
	}
	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
		return;
	}
	public void setBonus(TipoBonus bonus) {
		this.bonus = bonus;
		return;
	}
	public HashMap<ParadaID, Parada> getVizinhos(){
		return this.vizinhos;
	}
	public HashMap<ProvinciaID, Provincia> getProvincias(){
		return this.provincias;
	}
	public Jogador getYurt() {
		return this.yurt;
	}
	public int getMultiplicidade() {
		return this.multiplicidade;
	}
	public Cidade getCidade() {
		return this.cidade;
	}
	public boolean isMoveble() {
		return (peoes.size() < multiplicidade);
	}
	public void insertPeao(Peao p) {
		this.peoes.add(p);
		return;
	}
	public void removePeao(Peao p) {
		this.peoes.remove(p);
		return;
	}
	public TipoBonus getBonus() {
		return this.bonus;
	}

}
