package model;

class Peao {
	private Jogador jogador;
	private Parada posicao = null;
	
	Peao(Jogador jogador) {
		this.jogador = jogador;
	}
	
	void setPosicao(Parada posicao) {
		this.posicao = posicao;
		return;
	}
	Parada getPosicao() {
		return this.posicao;
	}
	Jogador getJogador() {
		return this.jogador;
	}
}