package model;

import java.util.HashMap;

class Altani extends Personagem {

    Altani() {
        super("Altani", Cor.VERDE);
    }
    
    
    @Override
    int getQuantidadePeoesIniciais() {
        return 2;
    }

    @Override
    HashMap<Enum<?>, Integer> getAcoesBase(Regiao regiao) {
        if (regiao == null) {
            return criarAcoesBase(2, 0, true, false);
        }
        switch (regiao) {
            case CHINA:
                return criarAcoesBase(1, 2, true, false);
            case PERSIA:
                return criarAcoesBase(2, 1, false, false);
            case RUSSIA:
                return criarAcoesBase(1, 1, false, true);
            default:
                throw new IllegalArgumentException("Região inválida.");
        }
    }
}
