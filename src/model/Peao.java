package model;

class Peao {
	private Jogador jogador;
	private Parada posicao = null;
	
	public Peao(Jogador jogador) {
		this.jogador = jogador;
	}
	
	public void setPosicao(Parada posicao) {
		this.posicao = posicao;
		return;
	}
	public Parada getPosicao() {
		return this.posicao;
	}
	public Jogador getJogador() {
		return this.jogador;
	}
}
