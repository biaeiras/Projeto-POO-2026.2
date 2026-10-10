package model;

class Peao {

    private ParadaID posicao;

    Peao(ParadaID posicaoInicial) {
        this.posicao = posicaoInicial;
    }

    ParadaID getPosicao() {
        return posicao;
    }

    void setPosicao(ParadaID novaPosicao) {
        this.posicao = novaPosicao;
    }
}