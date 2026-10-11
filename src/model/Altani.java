package model;

class Altani extends Personagem {

    Altani() {
        super("Altani", Cor.VERDE);
    }
    
    
    @Override
    int getQuantidadePeoesIniciais() {
        return 2;
    }
}