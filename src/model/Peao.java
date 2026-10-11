package model;

class Peao {
	private Jogador jogador;
	private Parada posicao = null;
	Peao() {
        this.jogador = null;
    }
	
	Peao(Jogador jogador) {
        this.jogador = jogador;
    }

    void setJogador(Jogador jogador) {
        if (jogador == null) {
            throw new IllegalArgumentException(
                "O jogador não pode ser nulo."
            );
        }

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
