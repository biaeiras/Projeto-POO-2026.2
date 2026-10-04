package model;

enum Personagem {

    ALTANI(Cor.VERDE),
    CHAGATAI(Cor.VERMELHO),
    JOCHI(Cor.CINZA),
    OGEDEI(Cor.AZUL),
    TOLUI(Cor.AMARELO);

    private final Cor cor;

    Personagem(Cor cor) {
        this.cor = cor;
    }

    Cor getCor() {
        return cor;
    }
}