package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ModelAPI {
	Partida partida;
	ModelAPI(ArrayList<Cor> cores){
		partida = new Partida(cores);
		partida.escolherConselheiroJogadorAtual(0);
		partida.avancarJogador();
		partida.escolherConselheiroJogadorAtual(0);
		partida.avancarJogador();
		partida.iniciarTurno(Regiao.RUSSIA);
		
	}
	
	public void moverPeao(ParadaID id) {
		partida.moverPeao(id);
		return;
	}
	
	public void pegarTributo(ProvinciaID id) {
		partida.pegarTributo(id);
		return;
	}
	
	public HashMap<ProvinciaID, Integer> getCargasProvincias() {
		return partida.getCargasProvincias();
	}
	
	public void construirYurt(ParadaID id) {
		partida.construirYurt(id);
		return;
	}
	
	public void avancarTurno() {
		partida.TerminarTurno();
		partida.iniciarTurno(Regiao.PERSIA);
		return;
	}
	
	public HashMap<ParadaID, ArrayList<Cor>> getYurts(){
		return partida.getYurts();
	}
	
	public HashMap<ParadaID, ArrayList<Cor>> getPosicoesPeoes(){
		HashMap<ParadaID, ArrayList<Cor>> ret = new HashMap<ParadaID, ArrayList<Cor>>();
		for(Jogador jogador: partida.getJogadores()) {
			Cor cor = jogador.getTabuleiroJogador().getPersonagem().getCor();
			for(int i = 0; i < jogador.getQuantidadePeoes(); ++i) {
				ParadaID id = jogador.getPeao(i).getPosicao().getId();
				if(!ret.containsKey(id)) ret.put(id, new ArrayList<Cor>());
				ret.get(id).add(cor);
			}
		}
		return ret;
	}
	
	public HashSet<ParadaID> getAlcancaveisTotais(){
		return partida.getAlcancaveisTotais();
	}
	
	public HashSet<ParadaID> getVizinhosPossiveis(){
		return partida.getVizinhosPossiveis();
	}
	
	public HashSet<ProvinciaID> getProvincias(){
		return partida.getProvincias();
	}
}
